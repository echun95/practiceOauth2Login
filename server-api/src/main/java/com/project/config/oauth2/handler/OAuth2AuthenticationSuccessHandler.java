package com.project.config.oauth2.handler;

import com.project.auth.repository.MemberRepository;
import com.project.config.jwt.util.JwtUtils;
import com.project.config.oauth2.dto.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

/**
 * OAuth2 인증 성공시 접근하는 핸들러
 * 액세스, 리프레쉬 토큰을 재발급 함.
 * */
@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtUtils jwtUtils;
	private final MemberRepository memberRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

		// 사용자 액세스 및 리프레쉬 토큰 발급
		generateTokenByUserId(response, principal);

		String targetUrl = UriComponentsBuilder.fromUriString("/main")
			.build().toUriString();
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	private void generateTokenByUserId(HttpServletResponse response, PrincipalDetails principalDetails) {
		Long userId = principalDetails.getMember().getMemberId();
		// 회원 DB에 사용자 저장되어 있으면, 액세스 토큰 쿠키 발급 및 리프레쉬 토큰 업데이트
		memberRepository.findById(userId).ifPresent(user -> {
			jwtUtils.generateJwtCookieByUserId(response, userId);
			user.updateRefreshToken(jwtUtils.generateRefreshTokenFromUserId(userId));
		});
	}

	private void generateAuthentication(HttpServletRequest request, PrincipalDetails principalDetails) {
		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(
					principalDetails,
				null,
					principalDetails.getAuthorities());

		auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
