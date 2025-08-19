package baekgwa.suhoserver.global.config;

import static baekgwa.suhoserver.model.user.entity.UserRole.*;
import static org.springframework.http.HttpMethod.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import baekgwa.suhoserver.global.environment.UrlProperties;
import baekgwa.suhoserver.global.security.entrypoint.CustomAccessDeniedHandler;
import baekgwa.suhoserver.global.security.entrypoint.CustomAuthenticationEntryPoint;
import baekgwa.suhoserver.global.security.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.suhoserver.global.config
 * FileName    : SecurityConfig
 * Author      : Baekgwa
 * Date        : 2025-08-02
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-08-02     Baekgwa               Initial creation
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UrlProperties urlProperties;
	private final AuthenticationFilter authenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
			.role(ADMIN.name()).implies(STAFF.name())
			.role(STAFF.name()).implies(USER.name())
			.build();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// ✅ 보안 관련 설정 (CSRF, CORS, 세션)
			.csrf(AbstractHttpConfigurer::disable)

			// ✅ 기본 인증 방식 비활성화 (JWT 사용)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// ✅ Cors Setting
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			// ✅ End-point Setting
			.authorizeHttpRequests(authorize -> authorize
				// 프론트엔드에서 적용될 예외 포인트 설정
				.requestMatchers("/error", "/favicon.ico").permitAll()
				// Swagger 문서 접근 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
					"/swagger-ui.html").permitAll()

				//사용자 Role 별 계층형 권한 확인용 api
				.requestMatchers(GET, "/auth/admin").hasRole(ADMIN.name())
				.requestMatchers(GET, "/auth/staff").hasRole(STAFF.name())
				.requestMatchers(GET, "/auth/login").authenticated()

				// authentication
				.requestMatchers(POST, "/auth/login").permitAll()
				.requestMatchers(POST, "/auth/logout").permitAll()

				// user
				.requestMatchers(POST, "/user/signup").permitAll()

				// version
				.requestMatchers(POST, "/version").hasRole(STAFF.name())
				.requestMatchers(GET, "/version").permitAll()

				// Branch
				.requestMatchers(POST, "/branch/bom/upload").hasRole(STAFF.name())

				// Project
				.requestMatchers(POST, "/project/new").hasRole(STAFF.name())
				.requestMatchers(POST, "/project/{projectId}/branch").hasRole(STAFF.name())
				.requestMatchers(POST, "/project/{projectId}/normal-straight").hasRole(STAFF.name())
				.requestMatchers(POST, "/project/{projectId}/loop-straight").hasRole(STAFF.name())
				.requestMatchers(GET, "/project").permitAll()
				.requestMatchers(GET, "/project/sort-type").permitAll()

				// Straight
				.requestMatchers(POST, "/straight/type").hasRole(STAFF.name())

				.anyRequest().authenticated());

		// ❗ 인증 Filter 추가
		http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		// ❗ AuthenticationEntryPoint Custom Handler
		http.exceptionHandling(exception -> exception
			.authenticationEntryPoint(customAuthenticationEntryPoint)
			.accessDeniedHandler(customAccessDeniedHandler));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(urlProperties.getFrontend(), urlProperties.getBackend()));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(List.of("Content-Type"));
		configuration.setMaxAge(3600L);

		return request -> configuration;
	}
}
