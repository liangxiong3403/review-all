package org.liangxiong.common.enhance;

import cn.afterturn.easypoi.pdf.PdfExportUtil;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import com.itextpdf.layout.Document;

import java.io.OutputStream;
import java.util.Collection;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-04-04 16:03
 * @description PDF导出携带中文工具类
 **/
public final class ChinesePdfExportUtil extends PdfExportUtil {

    private ChinesePdfExportUtil() {
    }

    public static Document exportPdf(PdfExportParams entity, Class<?> pojoClass,
                                     Collection<?> dataSet, OutputStream outStream) {
        return new ChinesePdfExportServer(outStream, entity).createPdf(entity, pojoClass, dataSet);
    }
}
