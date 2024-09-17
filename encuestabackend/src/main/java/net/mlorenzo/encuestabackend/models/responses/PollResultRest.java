package net.mlorenzo.encuestabackend.models.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollResultRest {
	private Long id;
	private String content;
	private List<QuestionResultRest> results;
}
