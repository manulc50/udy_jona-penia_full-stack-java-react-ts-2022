package net.mlorenzo.encuestabackend.models.requests;

import lombok.Data;

@Data
public class UserLoginRequestModel {
	private String email;
	private String password;
}
