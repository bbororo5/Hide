package com.example.backend.util.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	//    CORS 설정 필터
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws
		IOException,
		ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String[] allowedOrigins = {"http://localhost:3000", "https://front-end-omega-topaz-47.vercel.app"};
		String originHeader = request.getHeader("Origin");
		if (Arrays.asList(allowedOrigins).contains(originHeader)) {
			response.setHeader("Access-Control-Allow-Origin", originHeader);
		}
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Expose-Headers", "Content-Type, Accept, X-Requested-With,Authorization");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With ,Authorization");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {
	}
}