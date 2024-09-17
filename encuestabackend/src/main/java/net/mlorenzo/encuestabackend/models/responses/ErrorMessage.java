package net.mlorenzo.encuestabackend.models.responses;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
	private String message;
	private Date timestamp;
	private Integer status;
}
