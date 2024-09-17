package net.mlorenzo.encuestabackend.entites;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import net.mlorenzo.encuestabackend.enums.QuestionType;

@Entity
@Table(name = "questions")
@Data
public class QuestionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 255)
	private String content;
	
	private Integer questionOrder;
	
	private QuestionType type;
	
	@ManyToOne
	@JoinColumn(name = "poll_id")
	private PollEntity poll;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "question")
	private List<AnswerEntity> answers;
	
	public QuestionEntity() {
		this.answers = new ArrayList<>();
	}
}
