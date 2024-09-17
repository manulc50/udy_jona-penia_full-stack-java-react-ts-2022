package net.mlorenzo.encuestabackend.security;


public class SecurityConstants {
	
	public final static String LOGIN_URL = "/users/login";
	public final static String TOKEN_SECRET_PROP = "tokenSecret";
	public final static String TOKEN_PREFIX = "Bearer ";
	public final static long EXPIRATION_DATE = 864000000; // 10 días

}
