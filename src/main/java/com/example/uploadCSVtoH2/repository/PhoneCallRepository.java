package com.example.uploadCSVtoH2.repository;

import com.example.uploadCSVtoH2.entity.PhoneCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneCallRepository extends JpaRepository<PhoneCall, Long> {

}
