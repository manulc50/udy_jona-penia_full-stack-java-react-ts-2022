package net.mlorenzo.encuestabackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.ActiveProfiles;

import net.mlorenzo.encuestabackend.entites.UserEntity;
import net.mlorenzo.encuestabackend.models.requests.UserLoginRequestModel;
import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.models.responses.UserRest;
import net.mlorenzo.encuestabackend.models.responses.ValidationErrors;
import net.mlorenzo.encuestabackend.repositories.UserRepository;
import net.mlorenzo.encuestabackend.services.UserService;

// Note: TestRestTemplate is only auto-configured when @SpringBootTest has been configured with a webEnvironment that means it starts the web container and listens for HTTP requests
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
	
	private static final String API_REGISTER_URL = "/users";
	private static final String API_PRINCIPAL_URL = "/users/principal";
	private static final String API_LOGIN_URL = "/users/login";
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	

	@Test
	public void registroSinDatosTest() {
		UserRegisterRequestModel userRegister = new UserRegisterRequestModel();
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, ValidationErrors.class);
		
		Map<String, String> errors = response.getBody().getErrors();
		
		assertEquals(errors.size(), 3);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void registroSinNombreTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userRegister.setName(null);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, ValidationErrors.class);
		
		Map<String, String> errors = response.getBody().getErrors();
		
		assertEquals(errors.size(), 1);
		assertTrue(errors.containsKey("name"));
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void registroSinPasswordTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userRegister.setPassword(null);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, ValidationErrors.class);
		
		Map<String, String> errors = response.getBody().getErrors();
		
		assertEquals(errors.size(), 1);
		assertTrue(errors.containsKey("password"));
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void registroSinEmailTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userRegister.setEmail(null);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, ValidationErrors.class);
		
		Map<String, String> errors = response.getBody().getErrors();
		
		assertEquals(errors.size(), 1);
		assertTrue(errors.containsKey("email"));
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void registroValidoTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		
		ResponseEntity<UserRest> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, UserRest.class);
		
		UserRest user = response.getBody();
		
		assertEquals(user.getName(), userRegister.getName());
		assertEquals(user.getEmail(), userRegister.getEmail());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	@Test
	public void registroValidoEnBDTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		
		ResponseEntity<UserRest> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, UserRest.class);
		
		Optional<UserEntity> oUserEntity = userRepository.findById(response.getBody().getId());
		
		assertNotNull(oUserEntity.get());
	}
	
	@Test
	public void registroValidoEncryptedPasswordBDTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		
		ResponseEntity<UserRest> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, UserRest.class);
		
		Optional<UserEntity> oUserEntity = userRepository.findById(response.getBody().getId());
		UserEntity userEntity = oUserEntity.get();
		
		assertNotNull(userEntity);
		assertNotEquals(userEntity.getEncryptedPassword(), userRegister.getPassword());
	}
	
	@Test
	public void registroValidoCorreoExistenteTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		
		testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, UserRest.class);
		
		ResponseEntity<ValidationErrors> response = testRestTemplate.postForEntity(API_REGISTER_URL, userRegister, ValidationErrors.class);
		
		Map<String, String> errors = response.getBody().getErrors();
		
		assertTrue(errors.containsKey("email"));
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void obtenerUsuarioPrincipalSinToken() {
		ResponseEntity<Void> response = testRestTemplate.getForEntity(API_PRINCIPAL_URL, Void.class);
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void obtenerUsuarioPrincipalConToken() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userService.createUser(userRegister);
		UserLoginRequestModel userLogin = new UserLoginRequestModel();
		userLogin.setEmail(userRegister.getEmail());
		userLogin.setPassword(userRegister.getPassword());
		
		ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
		
		RequestEntity<UserLoginRequestModel> requestLogin = RequestEntity.post(API_LOGIN_URL)
				.accept(MediaType.APPLICATION_JSON)
				.body(userLogin);
		
		ResponseEntity<Map<String, String>> responseLogin = testRestTemplate.exchange(requestLogin, responseType);
	    Map<String, String> body = responseLogin.getBody();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, body.get("token"));
	    
	    ResponseEntity<UserRest> response = testRestTemplate.exchange(API_PRINCIPAL_URL, HttpMethod.GET, new HttpEntity<>(headers), UserRest.class);
	
	    assertEquals(response.getStatusCode(), HttpStatus.OK);
	    assertEquals(userRegister.getName(), response.getBody().getName());
	}

}
