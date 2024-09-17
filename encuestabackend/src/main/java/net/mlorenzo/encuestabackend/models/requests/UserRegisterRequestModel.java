package net.mlorenzo.encuestabackend.models.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;
import net.mlorenzo.encuestabackend.annotations.UniqueEmail;

@Data
public class UserRegisterRequestModel {
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	@UniqueEmail
	@Email
	private String email;
	
	@NotEmpty
	@Size(min = 8, max = 40)
	private String password;
}
