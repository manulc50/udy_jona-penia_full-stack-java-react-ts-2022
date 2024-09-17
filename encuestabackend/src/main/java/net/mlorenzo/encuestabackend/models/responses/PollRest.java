package net.mlorenzo.encuestabackend.models.responses;

import java.util.List;

import lombok.Data;

@Data
public class PollRest {
	private Long id;
	private String pollId;
	private String content;
	private Boolean opened;
	private List<QuestionRest> questions;
	
}
