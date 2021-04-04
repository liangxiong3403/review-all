package org.liangxiong.common.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-04-04 10:47
 * @description 处理器冻结表头
 **/
public class CustomerWriteHandler implements SheetWriteHandler {

    public int colSplit = 0, rowSplit = 1, leftmostColumn = 0, topRow = 1;

    public String autoFilterRange = "1:1";

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        // 冻结表头
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
        sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
    }
}
