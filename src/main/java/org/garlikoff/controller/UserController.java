package org.garlikoff.controller;

import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.User;
import org.garlikoff.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController{
    @Autowired
    UserRepository repository;
    @Autowired
    private StatefulRediSearchConnection<String, String> searchConnection;


    @GetMapping("/")
    public Iterable<User> getAllUsers(@RequestParam(defaultValue = "") String email){
        log.info("find all users.email={}", email);
        if (email.isEmpty())
            return repository.findAll();
        else {
            Optional<User> user = Optional.ofNullable(repository.findFirstByEmail(email));
            return user.map(List::of).orElse(Collections.emptyList());
        }
    }

    @GetMapping("/search")
    @Cacheable("user-search")
    public String searchUser(@RequestParam String query){
        log.info("get query {}", query);
        return "asdas" + query;
    }


}
