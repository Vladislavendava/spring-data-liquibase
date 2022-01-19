package com.endava.practice.service;

import com.endava.practice.domain.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    List<User> saveAll(Collection<User> users);

    List<User> findFirst2ByOrderByLastnameAsc();

    void deleteAllUsers();
}
