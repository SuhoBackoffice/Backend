package baekgwa.suhoserver.infra.excel.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.infra.excel.util
 * FileName    : ExcelMerges
 * Author      : Baekgwa
 * Date        : 2025-09-01
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-01     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelMerges {

	/** 범용 머지: (r1..r2, c1..c2) */
	public static void merge(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		int fr = Math.min(firstRow, lastRow);
		int lr = Math.max(firstRow, lastRow);
		int fc = Math.min(firstCol, lastCol);
		int lc = Math.max(firstCol, lastCol);
		// 단일 셀이면 아무 것도 안 함
		if (fr == lr && fc == lc) return;
		sheet.addMergedRegion(new CellRangeAddress(fr, lr, fc, lc));
	}

	/** 행 구간 한 열만 머지 (예: A{r1}:A{r2}) */
	public static void mergeRows(Sheet sheet, int firstRow, int lastRow, int col) {
		merge(sheet, firstRow, lastRow, col, col);
	}

	/** 열 구간 한 행만 머지 (예: {row}행의 A..L) */
	public static void mergeCols(Sheet sheet, int row, int firstCol, int lastCol) {
		merge(sheet, row, row, firstCol, lastCol);
	}

	/** A1 표기 */
	public static void merge(Sheet sheet, String a1range) {
		CellRangeAddress a = CellRangeAddress.valueOf(a1range);
		merge(sheet, a.getFirstRow(), a.getLastRow(), a.getFirstColumn(), a.getLastColumn());
	}
}
