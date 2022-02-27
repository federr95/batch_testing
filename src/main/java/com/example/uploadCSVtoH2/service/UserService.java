package com.example.uploadCSVtoH2.service;


import com.example.uploadCSVtoH2.entity.User;
import com.example.uploadCSVtoH2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void addEvidence(User user){
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }


}
