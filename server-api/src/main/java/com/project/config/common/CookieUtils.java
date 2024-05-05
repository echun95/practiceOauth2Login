package com.project.config.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtils {
	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie cookie = WebUtils.getCookie(request, name);
		if (cookie != null) {
			return Optional.of(cookie);
		}

		return Optional.empty();
	}

	public static void addCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		// path(): 쿠키가 저장될 url path를 지정
		cookie.setPath(path);
		// httpOnly(): http방식으로만 Cookie접근 허용
		cookie.setHttpOnly(true);
		// maxAge(): 쿠키 만료기간 설정
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name,
		String path) {
		Optional.ofNullable(WebUtils.getCookie(request, name)).ifPresent(cookie -> {
			cookie.setValue("");
			cookie.setPath(path);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		});
	}

	// Base64로 URL 직렬화
	// 직렬화? 복잡한 객체나 데이터 구조를 저장, 전송 또는 영구 저장을 용이하게 하기위해 형식화된 형태로 변환
	// Ex) Dto -> Json
	public static String serialize(Object object) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
	}

	// Base64 URL 역직렬화
	// 역직렬화? 직렬화된 데이터를 해석하여 직렬화 되기 전의 원래 객체나 데이터 구조를 재생성, 직렬화의 반대
	// Ex) Dto -> Json
	public static <T> T deserialize(Cookie cookie, Class<T> cls) {
		return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
	}
}
