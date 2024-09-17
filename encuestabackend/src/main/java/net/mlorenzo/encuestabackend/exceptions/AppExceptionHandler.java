package net.mlorenzo.encuestabackend.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import net.mlorenzo.encuestabackend.models.responses.ErrorMessage;
import net.mlorenzo.encuestabackend.models.responses.ValidationErrors;

@ControllerAdvice
public class AppExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrors> handleValidationErrorException(MethodArgumentNotValidException ex) {
		
		Map<String, String> errors = new HashMap<>();
		
		ex.getBindingResult().getAllErrors().forEach(err -> {
			String fieldName = ((FieldError)err).getField();
			String errorMessage = err.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		ValidationErrors validationErrors = new ValidationErrors(errors, new Date());
		
		return ResponseEntity.badRequest().body(validationErrors);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorMessage> handleException(ResponseStatusException ex) {
		ErrorMessage errorMessage = new ErrorMessage(ex.getReason(), new Date(), ex.getStatus().value());
		return new ResponseEntity<ErrorMessage>(errorMessage, ex.getStatus());
	}

}
