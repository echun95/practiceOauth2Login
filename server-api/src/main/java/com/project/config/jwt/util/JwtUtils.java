package com.project.config.jwt.util;

import com.project.config.common.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
	private final String jwtCookie;
	private final String jwtCookiePath;
	private final String jwtSecretKey;
	private final Long accessTokenExpirationMs;
	private final Long refreshTokenExpirationMs;

	public JwtUtils(@Value("${spring.security.jwt.token.cookie.name}") String jwtCookie
		, @Value("${spring.security.jwt.token.cookie.path}") String jwtCookiePath
		, @Value("${spring.security.jwt.token.secret-key}") String jwtSecretKey
		, @Value("${spring.security.jwt.token.access.expire-length}") Long accessTokenExpirationMs
		, @Value("${spring.security.jwt.token.refresh.expire-length}") Long refreshTokenExpirationMs) {
		this.jwtCookie = jwtCookie;
		this.jwtCookiePath = jwtCookiePath;
		this.jwtSecretKey = jwtSecretKey;
		this.accessTokenExpirationMs = accessTokenExpirationMs;
		this.refreshTokenExpirationMs = refreshTokenExpirationMs;
	}

	// Token에서 ID 추출
	public String getUserIdFromJwtToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key()).build()
				.parseClaimsJws(token).getBody().getSubject();
		} catch (ExpiredJwtException e) {
			return e.getClaims().getSubject();
		}
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
	}

	public boolean validateJwtToken(String authToken) throws ExpiredJwtException {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	// 액세스 토큰 발급
	public String generateAccessTokenFromUserId(Long userId) {
		return generateToken(userId, accessTokenExpirationMs);
	}

	// 리프레쉬 토큰 발급
	public String generateRefreshTokenFromUserId(Long userId) {
		return generateToken(userId, refreshTokenExpirationMs);
	}

	// 사용자 이름, 날짜, 만료, 시크릿 키 + 사인 알고리즘 통해 암호화
	private String generateToken(Long userId, long tokenExpirationMs) {
		return Jwts.builder()
			.setHeaderParam("type", "JWT")
			.setSubject(String.valueOf(userId))
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + tokenExpirationMs))
			.signWith(key(), SignatureAlgorithm.HS256)
			.compact();
	}

	// 쿠키 값 가져옴
	public String getJwtFromCookies(HttpServletRequest request) {
		return CookieUtils.getCookie(request, jwtCookie)
			.map(Cookie::getValue)
			.orElseGet(() -> null);
	}

	// 쿠키 생성
	public void generateJwtCookieByUserId(HttpServletResponse response, Long id) {
		CookieUtils.addCookie(response, jwtCookie, generateAccessTokenFromUserId(id), jwtCookiePath, (24 * 60 * 60));
	}

	// cookie의 값 null로 변경, 로그아웃 처리를 위함
	public void getCleanJwtCookie(HttpServletRequest request, HttpServletResponse response) {
		CookieUtils.deleteCookie(request, response, jwtCookie, jwtCookiePath);
	}

}
