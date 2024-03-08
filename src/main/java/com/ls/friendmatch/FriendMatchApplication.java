package com.ls.friendmatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ls.friendmatch.mapper")
@EnableScheduling //开启定时任务
public class FriendMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendMatchApplication.class, args);
    }

}
