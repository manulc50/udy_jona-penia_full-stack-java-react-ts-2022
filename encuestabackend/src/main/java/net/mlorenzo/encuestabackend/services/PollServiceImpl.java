package net.mlorenzo.encuestabackend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import net.mlorenzo.encuestabackend.entites.PollEntity;
import net.mlorenzo.encuestabackend.entites.PollReplyDetailEntity;
import net.mlorenzo.encuestabackend.entites.PollReplyEntity;
import net.mlorenzo.encuestabackend.entites.UserEntity;
import net.mlorenzo.encuestabackend.models.requests.PollCreationRequestModel;
import net.mlorenzo.encuestabackend.models.requests.PollReplyRequestModel;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollReplyRest;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.DetailResultRest;
import net.mlorenzo.encuestabackend.models.responses.PaginatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PollRest;
import net.mlorenzo.encuestabackend.models.responses.PollResultRest;
import net.mlorenzo.encuestabackend.models.responses.QuestionResultRest;
import net.mlorenzo.encuestabackend.repositories.PollReplyRepository;
import net.mlorenzo.encuestabackend.repositories.PollRepository;
import net.mlorenzo.encuestabackend.repositories.PollResult;
import net.mlorenzo.encuestabackend.repositories.UserRepository;


@RequiredArgsConstructor
@Service
public class PollServiceImpl implements PollService {
	
	private final PollRepository pollRepository;
	private final PollReplyRepository pollReplyRepository;
	private final UserRepository userRepository;

	@Override
	public CreatedPollRest createPoll(PollCreationRequestModel model, String email) {
		// En este caso, el email pertenece a un usuario autenticado en el sistema y, por lo tanto, existe sí o sí
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		ModelMapper mapper = new ModelMapper();
		PollEntity pollEntity = mapper.map(model, PollEntity.class);
		pollEntity.setUser(userEntity);
		pollEntity.setPollId(UUID.randomUUID().toString());
		
		// Establecemos manualmente las relaciones bidireccionales ya que ModelMapper no lo hace
		pollEntity.getQuestions().forEach(question -> {
			question.setPoll(pollEntity);
			question.getAnswers().forEach(answer -> answer.setQuestion(question));
		});
		
		pollRepository.save(pollEntity);
		
		return new CreatedPollRest(pollEntity.getPollId());
		
	}

	@Override
	public PollRest getPoll(String pollId) {
		PollEntity pollEntity = pollRepository.findByPollId(pollId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Poll with pollId %s not Found", pollId)));
		
		if(!pollEntity.getOpened())
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Poll with pollId %s does not accept more replies", pollId));
		
		ModelMapper mapper = new ModelMapper();
		
		return mapper.map(pollEntity, PollRest.class);
	}
	
	@Override
	public PaginatedPollRest getPolls(int page, int limit, String email) {
		// En este caso, el email pertenece a un usuario autenticado en el sistema y, por lo tanto, existe sí o sí
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		Pageable pageable = PageRequest.of(page, limit);
		
		Page<PollEntity> paginatedPollEntities = pollRepository.findAllByUserId(userEntity.getId(), pageable);
		
		ModelMapper mapper = new ModelMapper();
		// Configuración para que no incluya las preguntas de las encuestas
		mapper.typeMap(PollEntity.class, PollRest.class).addMappings(m -> m.skip(PollRest::setQuestions));
		
		List<PollRest> listPollRest = paginatedPollEntities.getContent().stream()
				.map(pollEntity -> mapper.map(pollEntity, PollRest.class))
				.collect(Collectors.toList());
		
		return new PaginatedPollRest(listPollRest, paginatedPollEntities.getTotalPages(),
				paginatedPollEntities.getTotalElements(), paginatedPollEntities.getNumberOfElements(),
				paginatedPollEntities.getPageable().getPageNumber() + 1);
	}

	@Override
	public CreatedPollReplyRest createPollReply(PollReplyRequestModel model) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setAmbiguityIgnored(true);
		
		PollReplyEntity pollReplyEntity = mapper.map(model, PollReplyEntity.class);
		
		PollEntity pollEntity = pollRepository.findById(model.getPoll())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Poll with id %d not Found", model.getPoll())));
		
		pollReplyEntity.setPoll(pollEntity);
		
		// Para poder determinar si el usuario respondió a todas las preguntas
		// Esto lo hacemos con un HashSet porque no permiten datos repetidos y hay preguntas de tipo "CHECKBOX" que tienen múltiples respuestas y se tienen que contar como preguntas únicas
		Set<Long> uniqueReplies = new HashSet<>();
		
		for(PollReplyDetailEntity pollReplyDetailEntity: pollReplyEntity.getPollReplies()) {
			pollReplyDetailEntity.setPollReply(pollReplyEntity); // Establecemos manualmente las relaciones bidireccionales ya que ModelMapper no lo hace 
			uniqueReplies.add(pollReplyDetailEntity.getQuestionId()); // Sólo se almacenará un dato para aquellas preguntas de tipo "CHECKBOX" con múltiples respuestas
		}
		
		// Validamos si el usuario respondió a todas las preguntas de la encuesta
		if(uniqueReplies.size() != pollEntity.getQuestions().size())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must answer all the questions");
		
		pollReplyRepository.save(pollReplyEntity);
		
		return new CreatedPollReplyRest(pollReplyEntity.getId());
	}

	@Override
	public void togglePollOpened(String pollId, String email) {
		// En este caso, el email pertenece a un usuario autenticado en el sistema y, por lo tanto, existe sí o sí
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		PollEntity pollEntity = pollRepository.findByPollIdAndUserId(pollId, userEntity.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Poll with pollId %s not Found", pollId)));
		
		pollEntity.setOpened(!pollEntity.getOpened());
		
		pollRepository.save(pollEntity);
		
	}

	@Override
	public void deletePoll(String pollId, String email) {
		// En este caso, el email pertenece a un usuario autenticado en el sistema y, por lo tanto, existe sí o sí
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		PollEntity pollEntity = pollRepository.findByPollIdAndUserId(pollId, userEntity.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Poll with pollId %s not Found", pollId)));
		
		pollRepository.delete(pollEntity);
	
	}

	@Override
	public PollResultRest getResults(String pollId, String email) {
		// En este caso, el email pertenece a un usuario autenticado en el sistema y, por lo tanto, existe sí o sí
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		PollEntity pollEntity = pollRepository.findByPollIdAndUserId(pollId, userEntity.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Poll with pollId %s not Found", pollId)));
		
		List<PollResult> pollResults = pollRepository.getPollResults(pollEntity.getId());
		
		return new PollResultRest(pollEntity.getId(), pollEntity.getContent(), transformData(pollResults));
	}
	
	private List<QuestionResultRest> transformData(List<PollResult> pollResults) {
		Map<String, QuestionResultRest> transformedData = new HashMap<>();
		
		pollResults.forEach(result -> {
			DetailResultRest detailResultRest = new DetailResultRest(result.getAnswer(), result.getResult());
			String key = Long.toString(result.getQuestionId());
			QuestionResultRest pollResultRest;
			if(!transformedData.containsKey(key)) {
				List<DetailResultRest> details = new ArrayList<>();
				details.add(detailResultRest);
				pollResultRest = new QuestionResultRest(result.getQuestion(), details);
				transformedData.put(key, pollResultRest);
			}
			else {
				pollResultRest = transformedData.get(key);
				pollResultRest.getDetails().add(detailResultRest);
			}
		});
		
		return new ArrayList<>(transformedData.values());
		
	}

}
