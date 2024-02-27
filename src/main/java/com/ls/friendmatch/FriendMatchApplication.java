package com.ls.friendmatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ls.friendmatch.mapper")
public class FriendMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendMatchApplication.class, args);
    }

}
