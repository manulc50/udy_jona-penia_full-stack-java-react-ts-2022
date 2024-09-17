package net.mlorenzo.encuestabackend.entites;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@EntityListeners(AuditingEntityListener.class) // Para que funcione correctamente la anotación @CreatedDate
@Table(name = "poll_replies")
@Data
public class PollReplyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String user;
	
	@CreatedDate // Anotación para generar automáticamente la fecha y registrarla en la base de datos
	private Date createAt;
	
	@ManyToOne
	@JoinColumn(name = "poll_id")
	private PollEntity poll;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pollReply")
	private List<PollReplyDetailEntity> pollReplies;
	
	public PollReplyEntity() {
		this.pollReplies = new ArrayList<>();
	}
}
