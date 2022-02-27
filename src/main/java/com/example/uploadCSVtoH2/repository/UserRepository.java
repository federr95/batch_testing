package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}

