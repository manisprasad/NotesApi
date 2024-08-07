package org.manis.notes.repo;

import org.bson.types.ObjectId;
import org.manis.notes.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
