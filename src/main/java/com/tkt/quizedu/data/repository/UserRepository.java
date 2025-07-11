package com.tkt.quizedu.data.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.User;

@Repository
public interface UserRepository extends BaseRepository<User, String> {
  boolean existsUserByEmail(String email);

  Optional<User> findByEmail(String email);
}
