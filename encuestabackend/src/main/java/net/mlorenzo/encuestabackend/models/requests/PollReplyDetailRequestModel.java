package net.mlorenzo.encuestabackend.models.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class PollReplyDetailRequestModel {
	
	@NotNull
	@Positive
	private Long questionId;
	
	@NotNull
	@Positive
	private Long answerId;
}
