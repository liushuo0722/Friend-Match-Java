package com.ls.friendmatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ls.friendmatch.mapper.UserMapper;
import com.ls.friendmatch.model.domain.User;
import com.ls.friendmatch.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService ;
    
    @Resource
    private UserMapper userMapper ; 


    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode = "1";
        //校验非空
        long result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //校验账号长度超过4个字符
        userAccount = "yu";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //校验密码长度不超过8个字符
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //校验 密码和校验密码是否一致
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //校验是否有特殊字符
        userAccount = "yu pi";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //校验是否有重复数据
        userAccount = "dogyupi";
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        //这条可以插进数据库
        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword,
                checkPassword, planetCode);
        QueryWrapper queryWrapper = new QueryWrapper() ; 
        queryWrapper.eq("userAccount",userAccount) ;
        List list = userMapper.selectList(queryWrapper);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void testSearchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assert.assertNotNull(userList);
    }
}