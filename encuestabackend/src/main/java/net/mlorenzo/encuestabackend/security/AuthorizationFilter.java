package net.mlorenzo.encuestabackend.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
	
	private Environment env;

	public AuthorizationFilter(AuthenticationManager authenticationManager,  ApplicationContext ctx) {
		super(authenticationManager);
		this.env = ctx.getEnvironment();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken authToken = getAuthentication(authHeader);
		
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		chain.doFilter(request, response);
	}

	
	private UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
		
		if(authHeader == null)
			return null;
		
		String token = authHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
		
		// El subject del token Jwt es el email del usuario
		String subject = Jwts.parser()
				.setSigningKey(env.getProperty(SecurityConstants.TOKEN_SECRET_PROP))
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		if(subject == null)
			return null;
		
		return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
	}
	
}
