package net.mlorenzo.encuestabackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import net.mlorenzo.encuestabackend.entites.PollEntity;
import net.mlorenzo.encuestabackend.entites.PollReplyEntity;
import net.mlorenzo.encuestabackend.entites.UserEntity;
import net.mlorenzo.encuestabackend.models.requests.PollCreationRequestModel;
import net.mlorenzo.encuestabackend.models.requests.PollReplyRequestModel;
import net.mlorenzo.encuestabackend.models.requests.UserLoginRequestModel;
import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollReplyRest;
import net.mlorenzo.encuestabackend.models.responses.CreatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.ErrorMessage;
import net.mlorenzo.encuestabackend.models.responses.PaginatedPollRest;
import net.mlorenzo.encuestabackend.models.responses.PollRest;
import net.mlorenzo.encuestabackend.models.responses.PollResultRest;
import net.mlorenzo.encuestabackend.models.responses.UserRest;
import net.mlorenzo.encuestabackend.models.responses.ValidationErrors;
import net.mlorenzo.encuestabackend.repositories.PollReplyRepository;
import net.mlorenzo.encuestabackend.repositories.PollRepository;
import net.mlorenzo.encuestabackend.repositories.UserRepository;
import net.mlorenzo.encuestabackend.services.PollService;
import net.mlorenzo.encuestabackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS) // Para poder usar la anotación "@BeforeAll"
@ActiveProfiles("test")
public class PollControllerTest {
	
	private static final String API_POLLS_URL = "/polls";
	private static final String API_POLLS_REPLY_URL = "/polls/reply";
	private static final String API_POLLS_TOGGLE_OPENED_URL = "/polls/{id}/opened";
	private static final String API_POLLS_DELETE_URL = "/polls/{id}";
	private static final String API_POLLS_RESULT_URL = "/polls/{id}/results";
	private static final String API_LOGIN_URL = "/users/login";
	
	private UserEntity user;
	private PollEntity poll;
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PollService pollService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PollRepository pollRepository;
	
	@Autowired
	PollReplyRepository pollReplyRepository;
	
	private String token = "";
	
	@BeforeAll
	public void initialize() {
		// Configuración de TestRestTemplate para que soporte el método Http PATCH(por defecto no lo soporta) mediante el uso del cliente Http de Apache
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		UserRest userRest = userService.createUser(userRegister);
		UserLoginRequestModel userLogin = new UserLoginRequestModel();
		userLogin.setEmail(userRegister.getEmail());
		userLogin.setPassword(userRegister.getPassword());
		
		ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
		
		RequestEntity<UserLoginRequestModel> request = RequestEntity.post(API_LOGIN_URL)
				.accept(MediaType.APPLICATION_JSON)
				.body(userLogin);
		
		ResponseEntity<Map<String, String>> response = testRestTemplate.exchange(request, responseType);
	    Map<String, String> body = response.getBody();
		
	    this.token = body.get("token");
	    
	    // Para las pruebas de respuestas a las encuestas
	    this.user = userRepository.findById(userRest.getId()).get();
	    this.poll = pollRepository.save(TestUtil.createValidPollEntity(this.user));
	    // Para las pruebas de paginación
	    pollRepository.save(TestUtil.createValidPollEntity(this.user));
	    pollRepository.save(TestUtil.createValidPollEntity(this.user));
	}
	
	@Test
	public void createPollSinAutenticacionTest() {
		ResponseEntity<Void> response = testRestTemplate.postForEntity(API_POLLS_URL, new PollCreationRequestModel(), Void.class);
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void createPollSinDatosTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(new PollCreationRequestModel(), headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void createPollConAuthSinContenidoTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.setContent("");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("content"));
	}
	
	@Test
	public void createPollConAuthSinPreguntasTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.setQuestions(null);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions"));
	}
	
	@Test
	public void createPollConAuthConPreguntaSinContenidoTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.getQuestions().get(0).setContent("");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions[0].content"));
	}
	
	@Test
	public void createPollConAuthConPreguntaConOrdenIncorrectoTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.getQuestions().get(0).setQuestionOrder(0);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions[0].questionOrder"));
	}
	
	@Test
	public void createPollConAuthConPreguntaConTipoIncorrectoTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.getQuestions().get(0).setType("xxxx");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions[0].type"));
	}
	
	@Test
	public void createPollConAuthConPreguntaValidaSinRespuestaTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.getQuestions().get(0).setAnswers(null);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions[0].answers"));
	}
	
	@Test
	public void createPollConAuthConPreguntaValidaConRespuestaSinContenidoTest() {
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		poll.getQuestions().get(0).getAnswers().get(0).setContent("");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<ValidationErrors> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , ValidationErrors.class);
		
	    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	    assertTrue(response.getBody().getErrors().containsKey("questions[0].answers[0].content"));
	}
	
	@Test
	public void createPollConAuthValidaTest() {	
		PollCreationRequestModel poll = TestUtil.createValidPoll();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    ResponseEntity<CreatedPollRest> response = testRestTemplate.exchange(API_POLLS_URL, HttpMethod.POST, new HttpEntity<>(poll, headers) , CreatedPollRest.class);
	    
	    assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	    assertTrue(!response.getBody().getPollId().equals(""));
	    
	    Optional<PollEntity> oPollEntity = pollRepository.findByPollId(response.getBody().getPollId());
	    
	    PollEntity pollEntity = oPollEntity.get();
	    
	    assertNotNull(pollEntity);
	    
	    pollRepository.deleteById(pollEntity.getId());
	}
	
	@Test
	public void getPollWithQuestionsSinPollBDTest() {
		ResponseEntity<ErrorMessage> response = testRestTemplate.getForEntity(String.format("%s/uuid/questions", API_POLLS_URL) , ErrorMessage.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void getPollWithQuestionsConPollBDTest() {		
		ResponseEntity<PollRest> response = testRestTemplate.getForEntity(String.format("%s/%s/questions", API_POLLS_URL , poll.getPollId()), PollRest.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(poll.getPollId(), response.getBody().getPollId());
	}
	
	@Test
	public void replyPollSinUsuarioTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		pollReplyModel.setUser(null);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, ValidationErrors.class);
	
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getErrors().containsKey("user"));
	}
	
	@Test
	public void replyPollConPollIdInvalidoTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		pollReplyModel.setPoll(0L);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, ValidationErrors.class);
	
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getErrors().containsKey("poll"));
	}
	
	@Test
	public void replyPollSinRespuestasTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		pollReplyModel.setPollReplies(null);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, ValidationErrors.class);
	
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getErrors().containsKey("pollReplies"));
	}
	
	@Test
	public void replyPollConQuestionIdInvalidoTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		pollReplyModel.getPollReplies().get(0).setQuestionId(0L);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, ValidationErrors.class);
	
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].questionId"));
	}
	
	@Test
	public void replyPollConAnswerIdInvalidoTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		pollReplyModel.getPollReplies().get(0).setAnswerId(0L);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, ValidationErrors.class);
	
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
		assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].answerId"));
	}
	
	@Test
	public void replyPollConDatosValidosTest() {
		PollReplyRequestModel pollReplyModel = TestUtil.createValidPollReply(poll);
		
		ResponseEntity<CreatedPollReplyRest> response = testRestTemplate.postForEntity(API_POLLS_REPLY_URL, pollReplyModel, CreatedPollReplyRest.class);
	
		Optional<PollReplyEntity> oPollReplies = pollReplyRepository.findById(response.getBody().getId());
		
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		assertNotNull(oPollReplies.get());
	}
	
	@Test
	public void getPollsSinAuthTest() {
		ResponseEntity<Void> response = testRestTemplate.getForEntity(API_POLLS_URL, Void.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void getPollsConAuthSinParamsTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
		
		ResponseEntity<PaginatedPollRest> response =  testRestTemplate.exchange(API_POLLS_URL, HttpMethod.GET, new HttpEntity<>(headers), PaginatedPollRest.class);	
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getPolls().size(), 3);
		assertEquals(response.getBody().getCurrentPage(), 1);
		assertEquals(response.getBody().getCurrentPageRecords(), 3);
		assertEquals(response.getBody().getTotalPages(), 1);
		assertEquals(response.getBody().getTotalRecords(), 3);
	}
	
	@Test
	public void getPollsConAuthConParamLimitTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    String url = String.format("%s?limit=2", API_POLLS_URL);
		
		ResponseEntity<PaginatedPollRest> response =  testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PaginatedPollRest.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getPolls().size(), 2);
		assertEquals(response.getBody().getCurrentPage(), 1);
		assertEquals(response.getBody().getCurrentPageRecords(), 2);
		assertEquals(response.getBody().getTotalPages(), 2);
		assertEquals(response.getBody().getTotalRecords(), 3);
	}
	
	@Test
	public void getPollsConAuthConParamLimitYPageTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
	    String url = String.format("%s?limit=2&page=1", API_POLLS_URL);
		
		ResponseEntity<PaginatedPollRest> response =  testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PaginatedPollRest.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getPolls().size(), 1);
		assertEquals(response.getBody().getCurrentPage(), 2);
		assertEquals(response.getBody().getCurrentPageRecords(), 1);
		assertEquals(response.getBody().getTotalPages(), 2);
		assertEquals(response.getBody().getTotalRecords(), 3);
	}
	
	@Test
	public void togglePollOpenedSinAuthTest() {
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_TOGGLE_OPENED_URL, HttpMethod.PATCH, HttpEntity.EMPTY, Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void togglePollOpenedConAuthConPollIdInvalidaTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_TOGGLE_OPENED_URL, HttpMethod.PATCH, new HttpEntity<>(headers), Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void togglePollOpenedConAuthConPollIdValidaTest() {
		PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_TOGGLE_OPENED_URL, HttpMethod.PATCH, new HttpEntity<>(headers), Void.class, Map.of("id", poll.getPollId()));
		
		Optional<PollEntity> oUpdatedPoll = pollRepository.findById(poll.getId());
		
		assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
		assertNotNull(oUpdatedPoll.get());
		assertEquals(oUpdatedPoll.get().getOpened(), false);
		
		pollRepository.deleteById(poll.getId());
	}
	
	@Test
	public void deletePollSinAuthTest() {
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_DELETE_URL, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void deletePollConAuthConPollIdInvalidaTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_DELETE_URL, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void deletePollConAuthConPollIdValidaTest() {
		PollEntity poll = pollRepository.save(TestUtil.createValidPollEntity(user));
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_DELETE_URL, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class, Map.of("id", poll.getPollId()));
		
		Optional<PollEntity> odeletedPoll = pollRepository.findById(poll.getId());
		
		assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
		assertTrue(odeletedPoll.isEmpty());
	}
	
	@Test
	public void getResultsSinAuthTest() {
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_RESULT_URL, HttpMethod.GET, HttpEntity.EMPTY, Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void getResultsConAuthConPollIdInvalidaTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<Void> response = testRestTemplate.exchange(API_POLLS_RESULT_URL, HttpMethod.GET, new HttpEntity<>(headers), Void.class, Map.of("id", "abc"));
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void getResultsConAuthConPollIdValidaTest() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, token);
	    
		ResponseEntity<PollResultRest> response = testRestTemplate.exchange(API_POLLS_RESULT_URL, HttpMethod.GET, new HttpEntity<>(headers), PollResultRest.class, Map.of("id", poll.getPollId()));
		
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(poll.getContent(), response.getBody().getContent());
	}

}
