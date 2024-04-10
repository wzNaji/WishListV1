package com.boefcity.wishlistv1.repository;

import com.boefcity.wishlistv1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserName(String userName);
}
