package org.liangxiong.common.enhance;

import cn.afterturn.easypoi.cache.ImageCache;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import cn.afterturn.easypoi.pdf.export.PdfExportServer;
import cn.afterturn.easypoi.pdf.styler.IPdfExportStyler;
import cn.afterturn.easypoi.pdf.styler.PdfExportStylerDefaultImpl;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-04-04 16:04
 * @description PDF携带中文导出
 **/
public class ChinesePdfExportServer extends PdfExportServer {

    private static final Logger log = LoggerFactory.getLogger(ChinesePdfExportServer.class);

    private Document document;

    private IPdfExportStyler styler = new PdfExportStylerDefaultImpl();

    private boolean isListData = false;

    private static final Color COLOR = DeviceRgb.GREEN;

    /**
     * 只能为实例变量,否则报错: Pdf indirect object belongs to other PDF document. Copy object to current pdf document
     */
    private PdfFont font = null;

    {
        try {
            // 生成中文字体
            font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false);
        } catch (IOException e) {
            log.error("中文字体创建失败: {}", e.getMessage());
        }
    }

    public ChinesePdfExportServer(OutputStream outStream, PdfExportParams entity) {
        try {
            styler = entity.getStyler() == null ? styler : entity.getStyler();
            document = new Document(new PdfDocument(new PdfWriter(outStream)), entity.getPageSize());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ChinesePdfExportServer() throws IOException {
    }

    @Override
    public Document createPdf(PdfExportParams entity, Class<?> pojoClass, Collection<?> dataSet) {
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<>();
            if (entity.isAddIndex()) {
            }
            Field[] fields = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget target = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = target == null ? null : target.value();
            getAllExcelField(entity.getExclusions(), targetId, fields, excelParams, pojoClass,
                    null, null);
            createPdfByExportEntity(entity, excelParams, dataSet);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                document.close();
            } catch (Exception e) {
                log.error("document关闭失败: {}", e.getMessage());
            }
        }
        return document;
    }

    @Override
    public Document createPdfByExportEntity(PdfExportParams entity,
                                            List<ExcelExportEntity> excelParams,
                                            Collection<?> dataSet) {
        try {
            sortAllParams(excelParams);
            for (ExcelExportEntity excelParam : excelParams) {
                if (excelParam.getList() != null) {
                    isListData = true;
                    break;
                }
            }
            // 设置各个列的宽度
            float[] widths = getCellWidths(excelParams);
            Table table = new Table(widths);
            // 设置表头
            createHeaderAndTitle(entity, table, excelParams);
            int rowHeight = getRowHeight(excelParams) / 50;
            for (Object t : dataSet) {
                createCells(table, t, excelParams, rowHeight);
            }
            document.add(table);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            document.close();
        }
        return document;
    }

    private void createCells(Table table, Object t, List<ExcelExportEntity> excelParams,
                             int rowHeight) throws Exception {
        ExcelExportEntity entity;
        int maxHeight = getThisMaxHeight(t, excelParams);
        for (ExcelExportEntity excelParam : excelParams) {
            entity = excelParam;
            if (entity.getList() != null) {
                Collection<?> list = getListCellValue(entity, t);
                for (Object obj : list) {
                    createListCells(table, obj, entity.getList(), rowHeight);
                }
            } else {
                Object value = getCellValue(entity, t);
                String handleEmptyText = value == null ? "" : String.valueOf(value);
                if (entity.getType() == 1) {
                    createStringCell(table, handleEmptyText, entity,
                            rowHeight, 1, maxHeight, Boolean.TRUE);
                } else {
                    createImageCell(table, handleEmptyText, entity, rowHeight,
                            1, maxHeight, Boolean.TRUE);
                }
            }
        }
    }

    /**
     * 创建集合对象
     *
     * @param table
     * @param obj
     * @param rowHeight
     * @param excelParams
     * @throws Exception
     */
    private void createListCells(Table table, Object obj, List<ExcelExportEntity> excelParams,
                                 int rowHeight) throws Exception {
        ExcelExportEntity entity;
        for (ExcelExportEntity excelParam : excelParams) {
            entity = excelParam;
            Object value = getCellValue(entity, obj);
            String handleEmptyText = value == null ? "" : String.valueOf(value);
            if (entity.getType() == 1) {
                createStringCell(table, handleEmptyText, entity, rowHeight, Boolean.TRUE);
            } else {
                createImageCell(table, handleEmptyText, entity, rowHeight, 1,
                        1, Boolean.TRUE);
            }
        }
    }

    /**
     * 获取这一列的高度
     *
     * @param t           对象
     * @param excelParams 属性列表
     * @return
     * @throws Exception 通过反射过去值得异常
     */
    private int getThisMaxHeight(Object t, List<ExcelExportEntity> excelParams) throws Exception {
        if (isListData) {
            ExcelExportEntity entity;
            int maxHeight = 1;
            for (ExcelExportEntity excelParam : excelParams) {
                entity = excelParam;
                if (entity.getList() != null) {
                    Collection<?> list = getListCellValue(entity, t);
                    maxHeight = (list == null || maxHeight > list.size()) ? maxHeight : list.size();
                }
            }
            return maxHeight;
        }
        return 1;
    }

    /**
     * 获取Cells的宽度数组
     *
     * @param excelParams
     * @return
     */
    private float[] getCellWidths(List<ExcelExportEntity> excelParams) {
        List<Float> widths = new ArrayList<>();
        for (ExcelExportEntity excelParam : excelParams) {
            if (excelParam.getList() != null) {
                List<ExcelExportEntity> list = excelParam.getList();
                for (ExcelExportEntity excelExportEntity : list) {
                    widths.add((float) (20 * excelExportEntity.getWidth()));
                }
            } else {
                widths.add((float) (20 * excelParam.getWidth()));
            }
        }
        float[] widthArr = new float[widths.size()];
        for (int i = 0; i < widthArr.length; i++) {
            widthArr[i] = widths.get(i);
        }
        return widthArr;
    }

    private void createHeaderAndTitle(PdfExportParams entity, Table table,
                                      List<ExcelExportEntity> excelParams) {
        int fieldWidth = getFieldLength(excelParams);
        if (entity.getTitle() != null) {
            createHeaderRow(entity, table, fieldWidth);
        }
        createTitleRow(entity, table, excelParams);
    }

    /**
     * 创建表头
     *
     * @param title
     * @param table
     */
    private int createTitleRow(PdfExportParams title, Table table,
                               List<ExcelExportEntity> excelParams) {
        int rows = getRowNums(excelParams, false);
        for (ExcelExportEntity entity : excelParams) {
            if (entity.getList() != null) {
                if (StringUtils.isNotBlank(entity.getName())) {
                    Cell cell = createStringCell(table, entity.getName(), entity, 10, entity.getList().size(),
                            1, Boolean.FALSE);
                    cell.setBackgroundColor(COLOR);
                    table.addCell(cell);
                }
                List<ExcelExportEntity> sTitle = entity.getList();
                for (ExcelExportEntity excelExportEntity : sTitle) {
                    Cell cell = createStringCell(table, excelExportEntity.getName(), excelExportEntity, 10, Boolean.FALSE);
                    cell.setBackgroundColor(COLOR);
                    table.addCell(cell);
                }
            } else {
                Cell cell = createStringCell(table, entity.getName(), entity, 10, 1, rows == 2 ? 2 : 1, Boolean.FALSE);
                cell.setBackgroundColor(COLOR);
                table.addCell(cell);
            }
        }
        return rows;

    }

    private void createHeaderRow(PdfExportParams entity, Table table, int fieldLength) {
        Cell iCell = new Cell(entity.getSecondTitle() != null ? 2 : 1, fieldLength + 1);
        iCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        iCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        iCell.setHeight(entity.getTitleHeight());
        iCell.add(new Paragraph(entity.getTitle()));
        iCell.setBackgroundColor(COLOR);
        iCell.setFont(font);
        table.addCell(iCell);
        if (entity.getSecondTitle() != null) {
            iCell = new Cell(1, fieldLength + 1);
            iCell.add(new Paragraph(entity.getSecondTitle()));
            iCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            iCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            iCell.setHeight(entity.getSecondTitleHeight());
            iCell.setBackgroundColor(COLOR);
            iCell.setFont(font);
            table.addCell(iCell);
        }
    }

    private Cell createStringCell(Table table, String text, ExcelExportEntity entity,
                                  int rowHeight, int colspan, int rowspan, boolean addToTable) {
        Cell iCell = new Cell(rowspan, colspan);
        iCell.add(new Paragraph(text));
        iCell.setFont(font);
        // 居中对齐
        iCell.setTextAlignment(TextAlignment.CENTER);
        styler.setCellStyler(iCell, entity, text);
        if (addToTable) {
            table.addCell(iCell);
        }
        return iCell;
    }

    private Cell createStringCell(Table table, String text, ExcelExportEntity entity,
                                  int rowHeight, boolean addToTable) {
        Cell iCell = new Cell();
        iCell.add(new Paragraph(text));
        iCell.setFont(font);
        // 居中对齐
        iCell.setTextAlignment(TextAlignment.CENTER);
        styler.setCellStyler(iCell, entity, text);
        if (addToTable) {
            table.addCell(iCell);
        }
        return iCell;
    }

    private Cell createImageCell(Table table, String text, ExcelExportEntity entity,
                                 int rowHeight, int rowSpan, int colSpan, boolean addToTable) {

        Image image = new Image(ImageDataFactory.create(ImageCache.getImage(text)));
        Cell iCell = new Cell(rowSpan, colSpan);
        iCell.add(image);
        iCell.setHeight((int) (rowHeight * 2.5));
        iCell.setFont(font);
        // 居中对齐
        iCell.setTextAlignment(TextAlignment.CENTER);
        styler.setCellStyler(iCell, entity, text);
        if (addToTable) {
            table.addCell(iCell);
        }
        return iCell;
    }
}
