package com.endava.practice.service.impl;

import com.endava.practice.domain.User;
import com.endava.practice.repository.UserRepository;
import com.endava.practice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public List<User> saveAll(Collection<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public List<User> findFirst2ByOrderByLastnameAsc() {
        return userRepository.findFirst2ByOrderByLastnameAsc();
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
