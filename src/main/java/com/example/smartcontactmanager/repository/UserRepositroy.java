package com.example.smartcontactmanager.repository;

import com.example.smartcontactmanager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepositroy extends JpaRepository<User, Integer> {

    //@Query is used because we are getting data from database and field name is email
    @Query("select u from User u where u.email = :email")
    //@Param is used to get dynamic value eg. email from user
    public User getUserByEmail(@Param("email") String email);
}
