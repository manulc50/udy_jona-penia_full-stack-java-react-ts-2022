package net.mlorenzo.encuestabackend.security;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import lombok.RequiredArgsConstructor;
import net.mlorenzo.encuestabackend.services.UserService;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private final UserService userService;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
			.and()
			.csrf().disable()
			.headers().frameOptions().disable() // Para que se vea correctamente la consola H2
			.and()
			.authorizeRequests()
			.antMatchers("/h2-console/**").permitAll() // Acceso a la consola H2
			.antMatchers(HttpMethod.POST, "/users").permitAll()
			.antMatchers(HttpMethod.GET, "/polls/*/questions").permitAll()
			.antMatchers(HttpMethod.POST, "/polls/reply").permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilter(getAuthenticationFilter())
			.addFilter(new AuthorizationFilter(authenticationManager(), getApplicationContext()))
		    .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 401 en vez de 403(por defecto)
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Sin estado porque vamos a usar tokens y no necesitamos cookies para almacenar la sesión 
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), getApplicationContext());
		authenticationFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
		return authenticationFilter;
	}

}
