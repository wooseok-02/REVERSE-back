package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {
}
