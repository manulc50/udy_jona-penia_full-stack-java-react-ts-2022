package net.mlorenzo.encuestabackend.services;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import net.mlorenzo.encuestabackend.entites.UserEntity;
import net.mlorenzo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.mlorenzo.encuestabackend.models.responses.UserRest;
import net.mlorenzo.encuestabackend.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserRest createUser(UserRegisterRequestModel user) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity); // Copia las propiedades en común
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		UserRest userRest = new UserRest();
		BeanUtils.copyProperties(userRepository.save(userEntity), userRest); // Copia las propiedades en común
		
		return userRest;
	}
	
	@Override
	public UserRest getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(userEntity -> new UserRest(userEntity.getId(), userEntity.getName(), userEntity.getEmail()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with email %s not Found", email)));
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.map(userEntity -> new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>()))
				.orElseThrow(() -> new UsernameNotFoundException(email));
	}

}
