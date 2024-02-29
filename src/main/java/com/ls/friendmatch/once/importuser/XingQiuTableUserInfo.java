package com.ls.friendmatch.once.importuser;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 星球表格用户信息 导入Excel的第二步 写与表格字段相同的实体类
 */
public class XingQiuTableUserInfo {

    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}
