package net.mlorenzo.encuestabackend.services;

import net.mlorenzo.encuestabackend.models.requests.PollCreationRequestModel;
import net.mlorenzo.encuestabackend.models.requests.PollReplyRequestModel;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollReplyRest;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PaginatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PollRest;
import net.mlorenzo.encuestabackend.models.responses.PollResultRest;

public interface PollService {

	CreatedPollRest createPoll(PollCreationRequestModel model, String email);
	PollRest getPoll(String pollId);
	PaginatedPollRest getPolls(int page, int limit, String email);
	CreatedPollReplyRest createPollReply(PollReplyRequestModel model);
	void togglePollOpened(String pollId, String email);
	void deletePoll(String pollId, String email);
	PollResultRest getResults(String pollId, String email);
}
