package org.garlikoff.boot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Role;
import org.garlikoff.model.User;
import org.garlikoff.repository.RoleRepository;
import org.garlikoff.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
@Order(2)
public class CreateUsers implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0){
            Role admin = roleRepository.findFirstByName("name");
            Role customer = roleRepository.findFirstByName("customer");

            try{
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<List<User>> typeReference = new TypeReference<List<User>>(){

                };
                InputStream inputStream = getClass().getResourceAsStream("/data/users/users.json");
                List<User> users = mapper.readValue(inputStream, typeReference);
                users.stream().forEach(user -> {
                    user.setPassword(encoder.encode(user.getPassword()));
                    user.addRole(customer);
                    userRepository.save(user);
                    log.info(">>> added user {}", user);
                });


            } catch (IOException e){
                log.error("unable to import users:" + e);
            }
        }
    }
}
