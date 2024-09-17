package net.mlorenzo.encuestabackend.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import net.mlorenzo.encuestabackend.validators.ValueOfEnumValidator;

@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueOfEnum {
	
	Class<? extends Enum<?>> enumClass(); 

	String message() default "{encuesta.constraints.enum.messages}";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
