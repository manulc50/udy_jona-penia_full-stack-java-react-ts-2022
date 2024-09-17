package net.mlorenzo.encuestabackend.models.requests;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import lombok.Data;
import net.mlorenzo.encuestabackend.annotations.ValueOfEnum;
import net.mlorenzo.encuestabackend.enums.QuestionType;

@Data
public class QuestionCreationRequestModel {
	
	@NotEmpty
	private String content;
	
	@NotNull
	@Range(min = 1, max= 30)
	private Integer questionOrder;
	
	@NotEmpty
	@ValueOfEnum(enumClass = QuestionType.class)
	private String type;
	
	@Valid
	@NotNull
	@Size(min = 1, max = 10)
	private List<AnswerCreationRequestModel> answers;

}
