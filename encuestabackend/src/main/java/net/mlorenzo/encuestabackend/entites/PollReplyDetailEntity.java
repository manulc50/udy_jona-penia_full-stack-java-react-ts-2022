package net.mlorenzo.encuestabackend.entites;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "poll_reply_details")
@Data
public class PollReplyDetailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long questionId;
	
	private Long answerId;
	
	@ManyToOne
	@JoinColumn(name = "poll_reply_id")
	private PollReplyEntity pollReply;
}
