package net.mlorenzo.encuestabackend.models.responses;

import java.util.List;

import lombok.Data;
import net.mlorenzo.encuestabackend.enums.QuestionType;

@Data
public class QuestionRest {
	private Long id;
	private String content;
	private Integer questionOrder;
	private QuestionType type;
	private List<AnswerRest> answers;
}
