package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.Decree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecreeRepository extends JpaRepository<Decree, Long> {

}
