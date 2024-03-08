package com.ls.friendmatch.service;

import com.ls.friendmatch.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 新用户ID
     */
    long userRegister(String userAccount ,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount , String userPassword, HttpServletRequest request);

    /**
     *用户安全脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /*
    * @Author LiuMiss
    * @Description //TODO 用户注销
    * @Date 7:24 2024-2-16
    * @Param request
    * @return   int
    **/
    int userLoginOut( HttpServletRequest request);


    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 获取当前登录用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);


}
