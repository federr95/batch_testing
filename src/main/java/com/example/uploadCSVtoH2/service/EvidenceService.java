package com.example.uploadCSVtoH2.service;


import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenceService {

    @Autowired
    EvidenceRepository evidenceRepository;

    public void addEvidence(Evidence evidence){
        evidenceRepository.save(evidence);
    }

    public List<Evidence> getEvidence() {
        return evidenceRepository.findAll();
    }


}
