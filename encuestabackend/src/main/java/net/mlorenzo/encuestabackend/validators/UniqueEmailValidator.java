package net.mlorenzo.encuestabackend.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import net.mlorenzo.encuestabackend.annotations.UniqueEmail;
import net.mlorenzo.encuestabackend.services.UserService;

// Nota: Por defecto, Hibernate Validator utiliza el Locale por defecto(Locale.getDefault()) para localizar y obtener los mensajes de validación
// Ésto queire decir que si, por ejemplo, el Locale por defcto es "es" y el Locale se establece a "fr" y no exite un archivo de propiedades para ese Locale(ValidationMessages_fr),
// se utiliza el archivo de propiedades para el Locale por defecto(ValidationMessages_es), y si tampoco existe, se utiliza el archivo de propiedades por defecto(ValidationMessages)

// Nota: No hace falta anotar esta clase para que se un componente o bean de Spring, y así porder realiza inyección
// de dependencias, por esta razón:
// The Spring framework automatically detects all classes which implement the ConstraintValidator interface.
// The framework instantiates them and wires all dependencies like the class was a regular Spring bean.

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
	
	@Autowired
	private UserService userService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		try {
			userService.getUserByEmail(value);
			return false;
		}
		catch(ResponseStatusException e) {
			return true;
		}
	}

}
