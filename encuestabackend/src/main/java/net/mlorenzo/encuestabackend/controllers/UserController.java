package net.mlorenzo.encuestabackend.controllers;



import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.models.responses.UserRest;
import net.mlorenzo.encuestabackend.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public UserRest createUser(@RequestBody @Valid UserRegisterRequestModel userModel) {
		return userService.createUser(userModel);
	}
	
	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@GetMapping("/principal")
	public UserRest getUser(Authentication auth) {
		return userService.getUserByEmail(auth.getPrincipal().toString());
	}
	
}
