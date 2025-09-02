package baekgwa.suhoserver.infra.excel.util;

import java.awt.*;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.infra.excel.util
 * FileName    : ExcelStyler
 * Author      : Baekgwa
 * Date        : 2025-09-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-02     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelStyler {

	/** 행 전체 외곽선 (헤더 기준 lastCol 자동 판단) */
	public static void lineOuter(Row row, BorderStyle style,
		boolean top, boolean bottom, boolean left, boolean right) {
		int lastCol = resolveLastCol(row.getSheet(), row);
		ensureCells(row, 0, lastCol);
		for (int c = 0; c <= lastCol; c++) {
			lineOuter(row.getCell(c), style, top, bottom, left, right);
		}
	}

	/** 행 전체 배경색 (헤더 기준 lastCol 자동 판단) */
	public static void backgroundColor(Row row, Color rgb) {
		int lastCol = resolveLastCol(row.getSheet(), row);
		ensureCells(row, 0, lastCol);
		for (int c = 0; c <= lastCol; c++) {
			backgroundColor(row.getCell(c), rgb);
		}
	}

	/** 선택 컬럼만 배경색 (중간 공백 안전) */
	public static void backgroundColor(Row row, Color rgb, int... cols) {
		for (int c : cols) {
			if (row.getCell(c) == null) row.createCell(c);
			backgroundColor(row.getCell(c), rgb);
		}
	}

	/** 선택 컬럼만 외곽선 (중간 공백 안전) */
	public static void lineOuter(Row row, BorderStyle style,
		boolean top, boolean bottom, boolean left, boolean right,
		int... cols) {
		for (int c : cols) {
			if (row.getCell(c) == null) row.createCell(c);
			lineOuter(row.getCell(c), style, top, bottom, left, right);
		}
	}

	/** 단일 셀 외곽선 지정(필요한 변만 true) */
	public static void lineOuter(Cell cell, BorderStyle style,
		boolean top, boolean bottom, boolean left, boolean right) {
		XSSFCellStyle ns = clone(cell);
		if (top)    ns.setBorderTop(style);
		if (bottom) ns.setBorderBottom(style);
		if (left)   ns.setBorderLeft(style);
		if (right)  ns.setBorderRight(style);
		cell.setCellStyle(ns);
	}

	/** 행의 특정 구간 셀들 외곽선 지정 */
	public static void lineOuter(Row row, int fromCol, int toCol, BorderStyle style,
		boolean top, boolean bottom, boolean left, boolean right) {
		ensureCells(row, fromCol, toCol);
		for (int c = fromCol; c <= toCol; c++) {
			lineOuter(row.getCell(c), style, top, bottom, left, right);
		}
	}

	/** 병합영역 외곽선 지정(RegionUtil) */
	public static void lineOuter(Sheet sheet, CellRangeAddress region,
		BorderStyle top, BorderStyle right, BorderStyle bottom, BorderStyle left) {
		RegionUtil.setBorderTop(top, region, sheet);
		RegionUtil.setBorderRight(right, region, sheet);
		RegionUtil.setBorderBottom(bottom, region, sheet);
		RegionUtil.setBorderLeft(left, region, sheet);
	}

	/** 단일 셀 배경색 지정(단색) */
	public static void backgroundColor(Cell cell, Color rgb) {
		XSSFCellStyle ns = clone(cell);
		ns.setFillForegroundColor(ExcelPalette.xcolor(rgb));
		ns.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(ns);
	}

	/** 행의 특정 구간 배경색 지정 */
	public static void backgroundColor(Row row, int fromCol, int toCol, Color rgb) {
		ensureCells(row, fromCol, toCol);
		for (int c = fromCol; c <= toCol; c++) {
			backgroundColor(row.getCell(c), rgb);
		}
	}

	/** 영역 배경색 지정(병합 포함). 병합 셀도 안전하게 전체 칠함 */
	public static void backgroundColor(Sheet sheet, CellRangeAddress region, Color rgb) {
		for (int r = region.getFirstRow(); r <= region.getLastRow(); r++) {
			Row row = getOrCreateRow(sheet, r);
			ensureCells(row, region.getFirstColumn(), region.getLastColumn());
			for (int c = region.getFirstColumn(); c <= region.getLastColumn(); c++) {
				backgroundColor(row.getCell(c), rgb);
			}
		}
	}

	public static void align(Cell cell, HorizontalAlignment ha, VerticalAlignment va) {
		XSSFCellStyle ns = clone(cell);
		if (ha != null) ns.setAlignment(ha);
		if (va != null) ns.setVerticalAlignment(va);
		cell.setCellStyle(ns);
	}

	public static void align(Row row, int fromCol, int toCol, HorizontalAlignment ha, VerticalAlignment va) {
		ensureCells(row, fromCol, toCol);
		for (int c = fromCol; c <= toCol; c++) align(row.getCell(c), ha, va);
	}

	/** 볼드(옵션) — 폰트 캐시는 생략(많이 만들면 폰트/스타일 폭증 가능) */
	public static void bold(Cell cell, boolean bold) {
		Workbook wb = cell.getSheet().getWorkbook();
		Font f = wb.createFont(); f.setBold(bold);
		XSSFCellStyle ns = clone(cell);
		ns.setFont(f);
		cell.setCellStyle(ns);
	}
	public static void bold(Row row, int fromCol, int toCol, boolean bold) {
		ensureCells(row, fromCol, toCol);
		for (int c = fromCol; c <= toCol; c++) bold(row.getCell(c), bold);
	}

	/* ========= 내부 유틸 ========= */
	/** 헤더(0행)가 있으면 그걸로, 없으면 해당 행의 lastCellNum으로 lastCol 산출 */
	static int resolveLastCol(Sheet sheet, Row currentRow) {
		Row header = sheet.getRow(0);
		short last = (header != null) ? header.getLastCellNum() : currentRow.getLastCellNum();
		// getLastCellNum()은 "마지막 인덱스+1"이거나, 행이 비어있으면 -1
		int lastCol = (last > 0) ? (last - 1) : 0;
		return Math.max(lastCol, 0);
	}

	private static XSSFCellStyle clone(Cell cell) {
		Workbook wb = cell.getSheet().getWorkbook();
		XSSFCellStyle ns = (XSSFCellStyle) wb.createCellStyle();
		CellStyle cur = cell.getCellStyle();
		if (cur != null) ns.cloneStyleFrom(cur);
		return ns;
	}

	private static Row getOrCreateRow(Sheet sheet, int r) {
		Row row = sheet.getRow(r);
		return (row != null) ? row : sheet.createRow(r);
	}

	private static void ensureCells(Row row, int fromCol, int toCol) {
		for (int c = fromCol; c <= toCol; c++) {
			if (row.getCell(c) == null) row.createCell(c);
		}
	}
}