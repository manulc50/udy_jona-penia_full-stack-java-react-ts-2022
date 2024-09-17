package net.mlorenzo.encuestabackend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailResultRest {
	private String answer;
	private Long result;
}
