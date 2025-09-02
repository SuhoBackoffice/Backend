package baekgwa.suhoserver.infra.excel.util;

import java.awt.*;

import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.infra.excel.util
 * FileName    : ExcelPalette
 * Author      : Baekgwa
 * Date        : 2025-09-01
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-09-01     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelPalette {
	public static final Color HEADER_YELLOW = new Color(255, 242, 204);
	public static final Color DATA_BLUE = new Color(217, 225, 242);

	private static final DefaultIndexedColorMap MAP = new DefaultIndexedColorMap();

	public static XSSFColor xcolor(Color rgb) {
		return new XSSFColor(rgb, MAP);
	}
}
