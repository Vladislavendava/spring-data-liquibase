/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.endava.practice.repository;

import com.endava.practice.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find the user with the given username. This method will be translated into a query using the
     * {@link javax.persistence.NamedQuery} annotation at the {@link User} class.
     */
//    User findByTheUsersName(String username);

    /**
     * Uses {@link Optional} as return and parameter type.
     */
    Optional<User> findByUsername(Optional<String> username);

    /**
     * Find all users with the given lastname. This method will be translated into a query by constructing it directly
     * from the method name as there is no other query declared.
     */
    List<User> findByLastname(String lastname);

    /**
     * Returns all users with the given firstname. This method will be translated into a query using the one declared in
     * the {@link Query} annotation declared one.
     */
    @Query("select u from User u where u.firstname = :firstname")
    List<User> findByFirstname(String firstname);

    /**
     * Returns all users with the given name as first- or lastname. This makes the query to method relation much more
     * refactoring-safe as the order of the method parameters is completely irrelevant.
     */
    @Query("select u from User u where u.firstname = :name or u.lastname = :name")
    List<User> findByFirstnameOrLastname(String name);

    /**
     * Returns the total number of entries deleted as their lastnames match the given one.
     */
    Long removeByLastname(String lastname);

    /**
     * Returns a {@link Slice} counting a maximum number of {@link Pageable#getPageSize()} users matching given criteria
     * starting at {@link Pageable#getOffset()} without prior count of the total number of elements available.
     */
    Slice<User> findByLastnameOrderByUsernameAsc(String lastname, Pageable page);

    /**
     * Return the first 2 users ordered by their lastname asc.
     *
     * <pre>
     * Example for findFirstK / findTopK functionality.
     * </pre>
     */
    List<User> findFirst2ByOrderByLastnameAsc();

    /**
     * Return the first 2 users ordered by the given {@code sort} definition.
     *
     * <pre>
     * This variant is very flexible because one can ask for the first K results when a ASC ordering
     * is used as well as for the last K results when a DESC ordering is used.
     * </pre>
     */
    List<User> findTop2By(Sort sort);

    /**
     * Return all the users with the given firstname or lastname. Makes use of SpEL (Spring Expression Language).
     */
    @Query("select u from User u where u.firstname = :#{#user.firstname} or u.lastname = :#{#user.lastname}")
    Iterable<User> findByFirstnameOrLastname(User user);

    /**
     * Sample default method.
     */
    default List<User> findByLastname(User user) {
        return findByLastname(user == null ? null : user.getLastname());
    }

    /**
     * Sample method to demonstrate support for {@link Stream} as a return type with a custom query. The query is executed
     * in a streaming fashion which means that the method returns as soon as the first results are ready.
     */
    @Query("select u from User u")
    Stream<User> streamAllCustomers();

    /**
     * Sample method to demonstrate support for {@link Stream} as a return type with a derived query. The query is
     * executed in a streaming fashion which means that the method returns as soon as the first results are ready.
     */
    Stream<User> findAllByLastnameIsNotNull();

}
