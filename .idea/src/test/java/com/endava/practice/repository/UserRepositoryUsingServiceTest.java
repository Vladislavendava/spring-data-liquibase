package com.endava.practice.repository;

import com.endava.practice.domain.User;
import com.endava.practice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryUsingServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void findFirst2ByOrderByLastnameAsc() {

        var user0 = new User();
        user0.setLastname("lastname-0");

        var user1 = new User();
        user1.setLastname("lastname-1");

        var user2 = new User();
        user2.setLastname("lastname-2");

        // we deliberately save the items in reverse
        userService.saveAll(Arrays.asList(user2, user1, user0));

        var result = userService.findFirst2ByOrderByLastnameAsc();

        assertThat(result).containsExactly(user0, user1);
    }

    @AfterEach
    void deleteAll() {
        userService.deleteAllUsers();
    }
}
