package com.ls.friendmatch.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.friendmatch.Exception.BusinessException;
import com.ls.friendmatch.common.BaseResponse;
import com.ls.friendmatch.common.ErrorCode;
import com.ls.friendmatch.common.ResultUtils;
import com.ls.friendmatch.model.domain.User;
import com.ls.friendmatch.model.domain.request.UserLoginRequest;
import com.ls.friendmatch.model.domain.request.UserRegisterRequest;
import com.ls.friendmatch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ls.friendmatch.constant.UserConstant.ADMIN_ROLE;
import static com.ls.friendmatch.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode() ;
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
         Long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);

        return ResultUtils.success(result);

    }


    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        User result =  userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(result);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public BaseResponse<Integer> userLoginOut(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        Integer result = userService.userLoginOut(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser (HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null ){
            throw new BusinessException(ErrorCode.NOT_LOGIN) ;
        }
        Long id = user.getId() ;
        User currenUser = userService.getById(id) ;
        User result = userService.getSafetyUser(currenUser);
        return ResultUtils.success(result);
    }

    /**
     * 查询用户
     *
     * @param userName
     * @return
     */
    @GetMapping("/search")
    public BaseResponse <List<User> > searchUser(String userName, HttpServletRequest request) {

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName", userName);
        }
        List<User> userList = userService.list(queryWrapper) ;

        List<User> userResultList =  userList.stream().map(user-> userService.getSafetyUser(user)).collect(Collectors.toList());

        return ResultUtils.success(userResultList);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        Boolean result  = userService.removeById(id);
        return ResultUtils.success(result) ;
    }

    /**
     * 鉴权 是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 查推荐接口
     *
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse <Page<User> > recommendUsers( long pageSize,long pageNum ,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("friendmatch:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtils.success(userPage);

    }




}
