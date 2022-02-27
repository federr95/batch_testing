package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

}
