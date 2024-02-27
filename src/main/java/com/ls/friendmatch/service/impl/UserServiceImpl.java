package com.ls.friendmatch.service.impl;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ls.friendmatch.Exception.BusinessException;
import com.ls.friendmatch.common.ErrorCode;
import com.ls.friendmatch.model.domain.User;
import com.ls.friendmatch.service.UserService;
import com.ls.friendmatch.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ls.friendmatch.constant.UserConstant.SALT;
import static com.ls.friendmatch.constant.UserConstant.USER_LOGIN_STATE;

/**
 *用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Resource
    private  UserMapper userMapper ;
    /**
     * 1. 用户在前端输入账户和密码、以及校验码（todo）
     * 2. 校验用户的账户、密码、校验密码，是否符合要求
     *    1. 非空
     *    2. 账户长度 **不小于** 4 位
     *    3. 密码就 **不小于** 8 位吧
     *    4. 账户不能重复
     *    5. 账户不包含特殊字符
     *    6. 密码和校验密码相同
     * 3. 对密码进行加密（密码千万不要直接以明文存储到数据库中）
     * 4. 向数据库插入用户数据
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {

        //校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        //校验账号长度不小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"账号长度过短") ;
        }
        //校验星球编号长度不大于5
        if (planetCode.length() > 5 ) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"星球编号过长") ;
        }
        //校验密码长度不小于8
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"密码长度过短") ;
        }
        //校验密码与校验密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"码与校验密码不一致") ;
        }
        //校验账号是否包含特殊字符
        String regex = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"账号包含特殊字符") ;
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"账号重复") ;
        }
        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        long countPlanetCode = this.count(queryWrapper);
        if (countPlanetCode > 0) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"星球编号重复") ;
        }
        //对密码 进行加密
        final String SALT = "yupi";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);

        if (!saveResult) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"未保存成功") ;
        }

        return user.getId();
    }
   
   /**
   * Description: 用户注销
   * date: 2024-2-14 22:36
   * @param userAccount
   * @param userPassword
   * @param request
   * @author: ls
   * @since JDK 1.8
   */
    public User userLogin(String userAccount, String userPassword,HttpServletRequest request) {

        //a.校验非空
        if (StringUtils.isAnyBlank(userAccount, userPassword )) {
            throw new BusinessException(ErrorCode.NULL_ERROR) ;
        }
        //b.校验账号长度不小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"账号长度大于4") ;
        }
        //c.校验密码长度不小于8
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"密码长度过短") ;
        }
        //d.校验账号是否包含特殊字符
        String regex = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"账号包含特殊字符") ;
        }
        //对密码 进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("userAccount",userAccount) ;
        queryWrapper.eq("userPassword",encryptPassword) ;
        //Mybatis-plus框架可提供只查询非逻辑删除的值，但是需要在实体类搭配注解，以及yml写配置文件
        User user = userMapper.selectOne(queryWrapper);

        //用户不存在
        if(user == null ){
            log.info("user login failed,userAccount Cannot match userPassword");
            throw new BusinessException(ErrorCode.PRAMS_ERROR,"用户不存在") ;
        }
        //用户脱敏
        User safetyUser = getSafetyUser(user) ;

        //记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return safetyUser;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求用户不存在") ;
        }
        User safetyUser = new User() ;
        safetyUser.setId(originUser.getId());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());

        return safetyUser ;
    }


    @Override
    public int userLoginOut(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR);
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户（SQL 查询版）
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PRAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }



}




