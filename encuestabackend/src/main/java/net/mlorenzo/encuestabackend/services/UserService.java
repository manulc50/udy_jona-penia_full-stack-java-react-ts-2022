package net.mlorenzo.encuestabackend.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.models.responses.UserRest;

public interface UserService extends UserDetailsService {

	UserRest createUser(UserRegisterRequestModel user);
	UserRest getUserByEmail(String email);
}
