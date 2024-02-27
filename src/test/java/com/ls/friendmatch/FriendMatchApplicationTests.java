package com.ls.friendmatch;

import com.ls.friendmatch.model.domain.User;
import com.ls.friendmatch.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class FriendMatchApplicationTests {

    @Resource
    private UserService userService ;

    @Test
    void contextLoads() {
    }

    @Test
    void testAdd(){
        User user = new User();

        user.setUserAccount("ls");
        user.setAvatarUrl("");
        user.setGender((byte)0);
        user.setUserPassword("123456");
        user.setPhone("12565425874");
        user.setEmail("132541257@qq.com");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete((byte)0);

        boolean result =  userService.save(user);

        System.out.println(user.getId());

        Assertions.assertTrue(result);


    }


}
