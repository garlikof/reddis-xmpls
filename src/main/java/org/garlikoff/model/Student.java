package org.garlikoff.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Student")
@Data
public class Student implements Serializable {
    public enum Gender {
        MALE, FEMALE
    };

    private String id;
    private String name;
    private Gender gender;
    private int grade;
}

