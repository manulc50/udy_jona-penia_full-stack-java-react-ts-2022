package net.mlorenzo.encuestabackend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import net.mlorenzo.encuestabackend.entites.PollEntity;

public interface PollRepository extends CrudRepository<PollEntity, Long> {
	
	Optional<PollEntity> findByPollId(String pollId);
	Page<PollEntity> findAllByUserId(Long userId, Pageable pageable);
	Optional<PollEntity> findByPollIdAndUserId(String pollId, Long userId);
	
	@Query(value = "SELECT q.question_order as questionOrder, prd.question_id as questionId,"
			+ "q.content as question, prd.answer_id as answerId, a.content as answer,"
			+ "count(prd.answer_id) as result"
			+ " FROM poll_replies pr LEFT JOIN poll_reply_details prd ON prd.poll_reply_id = pr.id"
			+ " LEFT JOIN answers a ON a.id = prd.answer_id"
			+ " LEFT JOIN questions q ON q.id = prd.question_id"
			+ " WHERE pr.poll_id = :pollId"
			+ " GROUP BY prd.question_id, prd.answer_id"
			+ " ORDER BY q.question_order", nativeQuery = true)
	List<PollResult> getPollResults(@Param(value = "pollId") Long id);

}
