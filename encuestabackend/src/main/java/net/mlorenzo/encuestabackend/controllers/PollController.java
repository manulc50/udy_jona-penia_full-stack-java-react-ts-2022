package net.mlorenzo.encuestabackend.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.mlorenzo.encuestabackend.models.requests.PollCreationRequestModel;
import net.mlorenzo.encuestabackend.models.requests.PollReplyRequestModel;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollReplyRest;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PaginatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PollRest;
import net.mlorenzo.encuestabackend.models.responses.PollResultRest;
import net.mlorenzo.encuestabackend.services.PollService;



@RequiredArgsConstructor
@RestController
@RequestMapping("/polls")
public class PollController {
	
	private final PollService pollService;

	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public CreatedPollRest createPoll(@RequestBody @Valid PollCreationRequestModel model, Authentication auth) {
		return pollService.createPoll(model, auth.getPrincipal().toString());
	}
	
	@GetMapping("/{id}/questions")
	public PollRest getPollWithQuestions(@PathVariable(value = "id") String pollId) {
		return pollService.getPoll(pollId);
	}
	
	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@GetMapping
	public PaginatedPollRest getPolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit, Authentication auth) {
		return pollService.getPolls(page, limit, auth.getPrincipal().toString());
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/reply")
	public CreatedPollReplyRest replyPoll(@RequestBody @Valid PollReplyRequestModel model) {
		return pollService.createPollReply(model);
	}
	
	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("{id}/opened")
	public void togglePollOpened(@PathVariable(name = "id") String pollId, Authentication auth) {
		pollService.togglePollOpened(pollId, auth.getPrincipal().toString());
	}
	
	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("{pollId}")
	public void deletePoll(@PathVariable String pollId, Authentication auth) {
		pollService.deletePoll(pollId, auth.getPrincipal().toString());
	}
	
	// Nota: En nuestro caso, "Principal" es el email del usuario autenticado
	@GetMapping("{id}/results")
	public PollResultRest getResuls(@PathVariable String id, Authentication auth) {
		return pollService.getResults(id, auth.getPrincipal().toString());
	}
	
}
