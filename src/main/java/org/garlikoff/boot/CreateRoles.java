package org.garlikoff.boot;

import lombok.extern.slf4j.Slf4j;
import org.garlikoff.model.Role;
import org.garlikoff.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class CreateRoles implements CommandLineRunner {

    @Autowired
    private RoleRepository repository;

    @Override
    public void run(String... args) throws Exception {
        log.info(">>>> hello from create roles");
        if (repository.count() == 0) {
            Role adminRole = Role.builder().name("admin").build();
            Role customerRole = Role.builder().name("customer").build();

            repository.save(adminRole);
            repository.save(customerRole);
            log.info(">>>created admin && customer roles...");
        } else {
        }
    }
}
