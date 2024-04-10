package com.boefcity.wishlistv1;

import com.boefcity.wishlistv1.entity.User;

import java.util.Optional;

public interface UserService {
    User findByUserName(String userName);
    User saveUser(User user);
    boolean checkLogin(String userName, String password);
    Optional<User> findById(int userId);
}
