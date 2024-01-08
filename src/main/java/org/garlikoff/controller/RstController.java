package org.garlikoff.controller;

import com.redislabs.lettusearch.SearchResults;
import org.garlikoff.model.Student;
import org.garlikoff.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RstController {

    Logger logger = LoggerFactory.getLogger(RstController.class);
    @Autowired
    StudentRepository repository;
//----------------------Repository

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Student> add(@RequestBody Student inbound){
        logger.info("we get student: {}", inbound.getName());
        Student outbound =  repository.save(inbound);
        ResponseEntity<Student> ret = new ResponseEntity<>(outbound, HttpStatus.OK);
        return ret;
    }

    @GetMapping(value = "/getall")
    public Iterable<Student> getAll(){
        Iterable<Student> ret = new ArrayList<>();
        ret = repository.findAll();
        return ret;
    }
//------------------template
    @Autowired
    private RedisTemplate<String, String> template;
    private final static String KEY_PREFIX = "redi2read:strings:";

    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    public Map.Entry<String, String> setStrings(@RequestBody Map.Entry<String, String> stringEntry){
        template.opsForValue().set(KEY_PREFIX + stringEntry.getKey(), stringEntry.getValue());
        return stringEntry;
    }

    @GetMapping("/strings/{key}")
    public Map.Entry<String, String> getString(@PathVariable("key") String key){
        String value = template.opsForValue().get(KEY_PREFIX + key);
        if (value == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "key not found");
        return new SimpleEntry<>(key, value);
    }

//    @PostMapping("/add2")
//    public ResponseEntity<Student> save2(@RequestBody Student inbound){
//        template.opsForValue().set(inbound.getId(), inbound);
//        return new ResponseEntity<>(template.opsForValue().get(inbound.getId()), HttpStatus.OK);
//    }

//    @GetMapping(value = "/search")
//    public SearchResults<>
}
