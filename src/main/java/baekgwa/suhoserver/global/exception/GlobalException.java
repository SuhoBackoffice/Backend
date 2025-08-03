package baekgwa.suhoserver.global.exception;

import baekgwa.suhoserver.global.response.ErrorCode;
import lombok.Getter;

/**
 * PackageName : baekgwa.suhoserver.global.exception
 * FileName    : GlobalException
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Getter
public class GlobalException extends RuntimeException {

	private final ErrorCode errorCode;

	public GlobalException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
