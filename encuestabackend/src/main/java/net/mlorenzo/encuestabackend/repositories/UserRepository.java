package net.mlorenzo.encuestabackend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.mlorenzo.encuestabackend.entites.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	
	Optional<UserEntity> findByEmail(String email);

}
