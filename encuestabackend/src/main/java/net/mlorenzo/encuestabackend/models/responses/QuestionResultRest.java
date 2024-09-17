package net.mlorenzo.encuestabackend.models.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResultRest {
	private String question;
	private List<DetailResultRest> details;
}
