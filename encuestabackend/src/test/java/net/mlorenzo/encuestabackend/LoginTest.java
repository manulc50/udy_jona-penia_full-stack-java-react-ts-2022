package net.mlorenzo.encuestabackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import net.mlorenzo.encuestabackend.models.requests.UserLoginRequestModel;
import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.repositories.UserRepository;
import net.mlorenzo.encuestabackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginTest {

	private static final String API_LOGIN_URL = "/users/login";
	private final static String TOKEN_PREFIX = "Bearer ";
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@AfterEach
	public void cleanup(TestInfo info) {
		if (info.getDisplayName().equals("sinCredencialesTest()"))
		    return; // Porque el test "sinCredencialesTest" no inserta nada en la base de datos
		
		userRepository.deleteAll();
	}
	
	@Test
	public void loginSinCredencialesTest() {
		UserLoginRequestModel userLogin = new UserLoginRequestModel();
		
		ResponseEntity<Void> response = testRestTemplate.postForEntity(API_LOGIN_URL, userLogin, Void.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void loginConCredencialesIncorrectasTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userService.createUser(userRegister);
		
		UserLoginRequestModel userLogin = new UserLoginRequestModel();
		userLogin.setEmail("aaa@gmail.com");
		userLogin.setPassword("12345678");
		
		ResponseEntity<Void> response = testRestTemplate.postForEntity(API_LOGIN_URL, userLogin, Void.class);
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	public void loginConCredencialesCorrectasTest() {
		UserRegisterRequestModel userRegister = TestUtil.createValidUser();
		userService.createUser(userRegister);
		UserLoginRequestModel userLogin = new UserLoginRequestModel();
		userLogin.setEmail(userRegister.getEmail());
		userLogin.setPassword(userRegister.getPassword());
		
		ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
		
		RequestEntity<UserLoginRequestModel> request = RequestEntity.post(API_LOGIN_URL)
				.accept(MediaType.APPLICATION_JSON)
				.body(userLogin);
		
		ResponseEntity<Map<String, String>> response = testRestTemplate.exchange(request, responseType);
	    Map<String, String> body = response.getBody();
		
	    assertTrue(body.get("token").startsWith(TOKEN_PREFIX));
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}
}
