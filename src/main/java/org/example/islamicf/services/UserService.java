package org.example.islamicf.services;

import org.example.islamicf.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    User saveUser(User user);

    User getUserById(Long id);
    User getUserByEmail(String email);

    void deleteUserById(Long id);
    User updateUser(User user);

    List<User> getAllUsers();
}
