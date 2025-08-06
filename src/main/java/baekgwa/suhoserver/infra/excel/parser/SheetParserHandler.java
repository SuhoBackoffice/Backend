package baekgwa.suhoserver.infra.excel.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * PackageName : baekgwa.suhoserver.domain.branch.service
 * FileName    : SheetParserHandler
 * Author      : Baekgwa
 * Date        : 2025-08-05
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-05     Baekgwa               Initial creation
 */
public class SheetParserHandler extends DefaultHandler {

	private final SharedStringsTable sst;
	private String lastContents = "";
	private boolean isString = false;

	// 현재 셀의 열 위치 추적을 위한 변수
	private int expectedColumn = 0;

	private final List<String> currentRow = new ArrayList<>();
	private final List<List<String>> allRows = new ArrayList<>();

	public SheetParserHandler(SharedStringsTable sst) {
		this.sst = sst;
	}

	public List<List<String>> getRows() {
		return allRows;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) {
		int currentColumn;
		if ("c".equals(name)) {
			// 셀의 참조 정보에서 열 번호 추출 (예: A1, B1, C1...)
			String cellRef = attributes.getValue("r");
			if (cellRef != null) {
				currentColumn = getColumnIndex(cellRef);

				// 현재 셀 위치까지 빈 셀들을 채움
				while (expectedColumn < currentColumn) {
					currentRow.add(""); // 빈 셀 추가
					expectedColumn++;
				}
			}

			String type = attributes.getValue("t");
			isString = "s".equals(type);
		} else if ("row".equals(name)) {
			// 새로운 행 시작시 열 카운터 리셋
			expectedColumn = 0;
		}

		lastContents = "";
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		lastContents += new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) {
		if ("v".equals(name)) {
			// 셀 값 처리
			String cellValue = lastContents;
			if (isString) {
				try {
					int idx = Integer.parseInt(lastContents);
					cellValue = sst.getItemAt(idx).getString();
				} catch (NumberFormatException e) {
					cellValue = ""; // 파싱 실패시 빈 문자열
				}
			}
			currentRow.add(cellValue);
			expectedColumn++;
		} else if ("row".equals(name)) {
			// 행이 끝날 때 현재 행을 전체 행 리스트에 추가
			if (!currentRow.isEmpty()) {
				allRows.add(new ArrayList<>(currentRow));
			}
			currentRow.clear();
			expectedColumn = 0;
		}
	}

	/**
	 * Excel 열 참조(A, B, C, ...)를 숫자 인덱스로 변환
	 * 예: A -> 0, B -> 1, Z -> 25, AA -> 26
	 */
	private int getColumnIndex(String cellRef) {
		if (cellRef == null || cellRef.isEmpty()) {
			return 0;
		}

		// 셀 참조에서 문자 부분만 추출 (예: "A1" -> "A")
		StringBuilder columnPart = new StringBuilder();
		for (char c : cellRef.toCharArray()) {
			if (Character.isLetter(c)) {
				columnPart.append(c);
			} else {
				break;
			}
		}

		String column = columnPart.toString().toUpperCase();
		int result = 0;

		for (int i = 0; i < column.length(); i++) {
			result = result * 26 + (column.charAt(i) - 'A' + 1);
		}

		return result - 1;
	}
}
