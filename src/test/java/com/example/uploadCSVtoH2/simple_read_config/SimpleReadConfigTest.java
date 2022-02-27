package com.example.uploadCSVtoH2.simple_read_config;

import com.example.uploadCSVtoH2.UploadCsVtoH2Application;
//import com.example.uploadCSVtoH2.simple_read.SimpleRead;
import com.example.uploadCSVtoH2.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SimpleReadConfig.class, UploadCsVtoH2Application.class})
public class SimpleReadConfigTest {

    @Autowired
    SimpleRead simpleRead;

    @Autowired
    UserRepository userRepository;

    @Test
    public void simpleRead() {
        this.simpleRead.readFile(userRepository);
        long evidenceTotal = 3000;
        assertEquals(userRepository.findAll().size(), evidenceTotal);
    }

}*/