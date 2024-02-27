package com.ls.friendmatch.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像路径
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除0否
     */
    @TableLogic
    private Byte isDelete;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户角色 0-普通用户 1-管理员 2-vip用户
     */
    private Integer userRole;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    /**
     * 星球编号
     */
    private String planetCode;


    /**
     * 标签 json 字符串
     */
    private String tags;


}