package com.boefcity.wishlistv1.services;

import com.boefcity.wishlistv1.UserService;
import com.boefcity.wishlistv1.entity.User;
import com.boefcity.wishlistv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User saveUser(User user) {
        if(userRepository.findByUserName(user.getUserName()) != null) {
            throw new RuntimeException("Username already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public boolean checkLogin(String userName, String password) {
        User user = userRepository.findByUserName(userName);
        if(user != null) {
            //Dette tjek returnerer brugerens password - for at opretholde korrekt sikkerhedsstandard, burde tjekket returnere en hashcode.
            return user.getUserPassword().equals(password);
        }
        return false;
    }
    @Override
    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }
}
