package net.mlorenzo.encuestabackend.security;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.mlorenzo.encuestabackend.models.requests.UserLoginRequestModel;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private AuthenticationManager authenticationManager;
	private Environment env;
	private ObjectMapper objectMapper;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
		this.authenticationManager = authenticationManager;
		this.env = ctx.getEnvironment();
		this.objectMapper = (ObjectMapper)ctx.getBean("jacksonObjectMapper");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
			/*if ( request.getContentType() == null
	            || !MediaType.APPLICATION_JSON.isCompatibleWith( MediaType.parseMediaType(request.getContentType()))) {
	            response.setStatus( HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE );
	            throw new RuntimeException("Media Type not supported: " + request.getContentType());
	        }*/
		
			try {
				UserLoginRequestModel user = objectMapper.readValue(request.getInputStream(), UserLoginRequestModel.class);
			
				return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			}
			catch(IOException ex) {
				throw new RuntimeException(ex);
			}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String email = ((User)authResult.getPrincipal()).getUsername(); // En nuestro caso, el username es el email porque en el método anterior "attemptAuthentication" se creó la instancia "UsernamePasswordAuthenticationToken" a partir del email como objeto "Principal"
	
		String jwtToken = Jwts.builder()
				.setSubject(email)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
				.signWith(SignatureAlgorithm.HS512, env.getProperty(SecurityConstants.TOKEN_SECRET_PROP))
				.compact();
		
		String data = objectMapper.writeValueAsString(Map.of("token", SecurityConstants.TOKEN_PREFIX + jwtToken));
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().print(data);
		response.flushBuffer();
	}
	
	

}
