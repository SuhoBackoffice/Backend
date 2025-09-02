package baekgwa.suhoserver.infra.excel.util;

import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * PackageName : baekgwa.suhoserver.infra.excel.util
 * FileName    : ExcelRowWriter
 * Author      : Baekgwa
 * Date        : 2025-09-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-02     Baekgwa               Initial creation
 */
public final class ExcelRowWriter {
	private ExcelRowWriter() {}

	/** rowIndex에 행을 만들고 valueWriter로 값만 채운 뒤 Row 반환 */
	public static Row writeRow(Sheet sheet, int rowIndex, Consumer<Row> valueWriter) {
		Row row = sheet.createRow(rowIndex);
		valueWriter.accept(row);
		return row;
	}

	/** 필요 개수만큼 셀을 보장(없으면 생성) */
	public static void ensureCells(Row row, int fromCol, int toCol) {
		for (int c = fromCol; c <= toCol; c++) {
			if (row.getCell(c) == null) row.createCell(c);
		}
	}

	/** 문자폭(256*chars) 기준 열 너비 일괄 설정 */
	public static void setColumnWidthsChars(Sheet sheet, int... chars) {
		for (int c = 0; c < chars.length; c++) {
			sheet.setColumnWidth(c, 256 * chars[c]);
		}
	}
}