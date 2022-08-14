package com.example.zipzip.Repo;

import com.example.zipzip.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);
}
