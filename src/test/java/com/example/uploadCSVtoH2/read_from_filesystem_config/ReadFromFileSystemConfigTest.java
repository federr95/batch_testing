package com.example.uploadCSVtoH2.read_from_filesystem_config;

import com.example.uploadCSVtoH2.UploadCsVtoH2Application;
import com.example.uploadCSVtoH2.read_from_filesystem.ReadFromFileSystem;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ReadFromFileSystemConfig.class, UploadCsVtoH2Application.class})
public class ReadFromFileSystemConfigTest {

    @Autowired
    ReadFromFileSystem readFromFileSystem;

    @Autowired
    EvidenceRepository evidenceRepository;

    @Test
    public void readFromFileSystem() {
        this.readFromFileSystem.readFile(evidenceRepository);
        long evidenceTotal = 3000;
        assertEquals(evidenceRepository.findAll().size(), evidenceTotal);
    }

}