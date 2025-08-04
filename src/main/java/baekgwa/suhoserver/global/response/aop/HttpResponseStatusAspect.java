package baekgwa.suhoserver.global.response.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import baekgwa.suhoserver.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.response.aop
 * FileName    : HttpResponseStatusAspect
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Aspect
@Component
@RequiredArgsConstructor
public class HttpResponseStatusAspect {

	private final HttpServletResponse response;

	@Around("""
			(
			    within
			    (
			        @org.springframework.web.bind.annotation.RestController *
			    )
			    &&
			    (
			        @annotation(org.springframework.web.bind.annotation.GetMapping)
			        ||
			        @annotation(org.springframework.web.bind.annotation.PostMapping)
			        ||
			        @annotation(org.springframework.web.bind.annotation.PutMapping)
			        ||
			        @annotation(org.springframework.web.bind.annotation.PatchMapping)
			        ||
			        @annotation(org.springframework.web.bind.annotation.DeleteMapping)
			        ||
			        @annotation(org.springframework.web.bind.annotation.RequestMapping)
			    )
			)
			||
			@annotation(org.springframework.web.bind.annotation.ResponseBody)
			""")
	public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
		Object proceed = joinPoint.proceed();

		if (proceed instanceof BaseResponse<?> baseResponse) {
			response.setStatus(baseResponse.getHttpStatus().value());
		}

		return proceed;
	}
}

