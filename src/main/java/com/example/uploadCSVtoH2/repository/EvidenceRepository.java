package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

}

