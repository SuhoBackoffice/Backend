package baekgwa.suhoserver.infra.excel.util;

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
	private ExcelRowWriter() {
	}

	/** rowIndex에 행을 만들고 valueWriter로 값만 채운 뒤 Row 반환 */
	public static Row writeRow(Sheet sheet, int rowIndex, java.util.function.Consumer<Row> valueWriter) {
		Row row = sheet.createRow(rowIndex);
		valueWriter.accept(row);
		return row;
	}

	/** 오버로드: 생성과 동시에 높이(포인트) 지정 */
	public static Row writeRow(Sheet sheet, int rowIndex, float heightPt,
		java.util.function.Consumer<Row> valueWriter) {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(heightPt);
		valueWriter.accept(row);
		return row;
	}

	/** 필요 개수만큼 셀을 보장(없으면 생성) */
	public static void ensureCells(Row row, int fromCol, int toCol) {
		for (int c = fromCol; c <= toCol; c++) {
			if (row.getCell(c) == null)
				row.createCell(c);
		}
	}

	/** 문자폭(256*chars) 기준 열 너비 일괄 설정 */
	public static void setColumnWidthsChars(Sheet sheet, int... chars) {
		for (int c = 0; c < chars.length; c++) {
			sheet.setColumnWidth(c, 256 * chars[c]);
		}
	}

	/* ----------------- 행 높이 유틸 ----------------- */

	/** 행 높이를 포인트(pt)로 지정 */
	public static void setRowHeightPt(Row row, float heightPt) {
		row.setHeightInPoints(heightPt);
	}

	/** 행 높이를 twips(1/20 pt)로 지정 */
	public static void setRowHeightTwips(Row row, short twips) {
		row.setHeight(twips); // 1pt = 20 twips
	}

	/** 픽셀 단위를 포인트로 변환해서 행 높이 지정 (가정: 96dpi) */
	public static void setRowHeightPx(Row row, int pixels) {
		row.setHeightInPoints(pxToPt(pixels));
	}

	/** 시트 기본 행 높이(포인트) 지정 */
	public static void setDefaultRowHeightPt(Sheet sheet, float heightPt) {
		sheet.setDefaultRowHeightInPoints(heightPt);
	}

	/** 연속 행 구간의 높이를 포인트로 일괄 지정 */
	public static void setRowsHeightPt(Sheet sheet, int fromRow, int toRow, float heightPt) {
		for (int r = fromRow; r <= toRow; r++) {
			Row row = sheet.getRow(r);
			if (row == null)
				row = sheet.createRow(r);
			row.setHeightInPoints(heightPt);
		}
	}

	/** 픽셀→포인트 변환 (기본 96dpi 가정) */
	public static float pxToPt(int px) {
		return px * 72f / 96f;  // ≈ px / 1.3333
	}

	/** 기본 높이로 되돌리기(행 개별) */
	public static void resetRowHeight(Row row) {
		row.setHeight((short)-1); // 확실: POI에서 -1은 '시트 기본 높이 사용'
	}
}
