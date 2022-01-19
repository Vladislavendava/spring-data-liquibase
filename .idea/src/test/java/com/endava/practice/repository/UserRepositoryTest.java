package com.endava.practice.repository;

import com.endava.practice.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("endava");
        user.setFirstname("firstname");
        user.setLastname("lastname");
    }

    @Test
    void findSavedUserById() {
        user = repository.saveAndFlush(user);
        assertThat(repository.findById(user.getId())).hasValue(user);
    }

    @Test
    void findSavedUserByLastname() {
        user = repository.saveAndFlush(user);
        assertThat(repository.findByLastname("lastname")).contains(user);
    }

    @Test
    void findByFirstnameOrLastname() {
        user = repository.saveAndFlush(user);
        assertThat(repository.findByFirstnameOrLastname("lastname")).contains(user);
    }

    @Test
    void useOptionalAsReturnAndParameterType() {
        assertThat(repository.findByUsername(Optional.of("endava"))).isEmpty();

        repository.save(user);

        assertThat(repository.findByUsername(Optional.of("endava"))).isPresent();
    }

    @Test
    void removeByLastname() {

        // create a 2nd user with the same lastname as user
        var user2 = new User();
        user2.setLastname(user.getLastname());

        // create a 3rd user as control group
        var user3 = new User();
        user3.setLastname("no-positive-match");

        repository.saveAllAndFlush(Arrays.asList(user, user2, user3));

        assertThat(repository.removeByLastname(user.getLastname())).isEqualTo(2L); // delete users 1 & 2
        assertThat(repository.existsById(user3.getId())).isTrue(); // validate user 3 exists
    }

    @Test
    void useSliceToLoadContent() {

        repository.deleteAll();

        // int repository with some values that can be ordered
        var totalNumberUsers = 11;
        List<User> source = new ArrayList<>(totalNumberUsers);

        for (var i = 1; i <= totalNumberUsers; i++) {

            var user = new User();
            user.setLastname(this.user.getLastname());
            user.setUsername(user.getLastname() + "-" + String.format("%03d", i));
            source.add(user);
        }

        repository.saveAll(source);

        Slice<User> users = repository.findByLastnameOrderByUsernameAsc(this.user.getLastname(), PageRequest.of(0, 5));

        assertThat(users).containsAll(source.subList(0, 5));
    }

    @Test
    void findFirst2ByOrderByLastnameAsc() {

        var user0 = new User();
        user0.setLastname("lastname-0");

        var user1 = new User();
        user1.setLastname("lastname-1");

        var user2 = new User();
        user2.setLastname("lastname-2");

        // we deliberately save the items in reverse
        repository.saveAll(Arrays.asList(user2, user1, user0));

        var result = repository.findFirst2ByOrderByLastnameAsc();

        assertThat(result).containsExactly(user0, user1);
    }

    @Test
    void findTop2ByWithSort() {

        var user0 = new User();
        user0.setLastname("lastname-0");

        var user1 = new User();
        user1.setLastname("lastname-1");

        var user2 = new User();
        user2.setLastname("lastname-2");

        // we deliberately save the items in reverse
        repository.saveAll(Arrays.asList(user2, user1, user0));

        var resultAsc = repository.findTop2By(Sort.by(ASC, "lastname"));

        assertThat(resultAsc).containsExactly(user0, user1);

        var resultDesc = repository.findTop2By(Sort.by(DESC, "lastname"));

        assertThat(resultDesc).containsExactly(user2, user1);
    }

    @Test
    void findByFirstnameOrLastnameUsingSpEL() {

        var first = new User();
        first.setLastname("lastname");

        var second = new User();
        second.setFirstname("firstname");

        var third = new User();

        repository.saveAll(Arrays.asList(first, second, third));

        var reference = new User();
        reference.setFirstname("firstname");
        reference.setLastname("lastname");

        var users = repository.findByFirstnameOrLastname(reference);

        assertThat(users).containsExactly(first, second);
    }

    /**
     * Streaming data from the store by using a repository method that returns a {@link Stream}. Note, that since the
     * resulting {@link Stream} contains state it needs to be closed explicitly after use!
     */
    @Test
    void useJava8StreamsWithCustomQuery() {

        var user1 = repository.save(new User("Customer1", "Foo"));
        var user2 = repository.save(new User("Customer2", "Bar"));

        try (var stream = repository.streamAllCustomers()) { // try-with-resources
            assertThat(stream.collect(Collectors.toList())).contains(user1, user2);
        }
    }

    /**
     * Streaming data from the store by using a repository method that returns a {@link Stream} with a derived query.
     * Note, that since the resulting {@link Stream} contains state it needs to be closed explicitly after use!
     */
    @Test
    void useJava8StreamsWithDerivedQuery() {

        var user1 = repository.save(new User("Customer1", "Foo"));
        var user2 = repository.save(new User("Customer2", "Bar"));
        var user3 = repository.save(new User("Customer3", null));

        try (var stream = repository.findAllByLastnameIsNotNull()) {
            assertThat(stream.collect(Collectors.toList())).contains(user1, user2);
        }
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }
}
