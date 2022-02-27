package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.TextMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextMessageRepository extends JpaRepository<TextMessage, Long> {

}
