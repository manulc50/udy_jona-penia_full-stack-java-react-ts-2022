package net.mlorenzo.encuestabackend.entites;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "polls", indexes = @Index(columnList = "pollId", name = "index_pollid", unique = true))
@Data
public class PollEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String pollId;
	
	@Column(nullable = false, length = 255)
	private String content;
	
	private Boolean opened;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "poll")
	private List<QuestionEntity> questions;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "poll")
	private List<PollReplyEntity> replies;
	
	
	public PollEntity() {
		this.questions = new ArrayList<>();
		this.replies = new ArrayList<>();
	}

}
