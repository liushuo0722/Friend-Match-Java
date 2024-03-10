package com.ls.friendmatch.service.impl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 编写一个Job类
 *
 * 用来编写定时任务做什么
 */
public class QuartzTest implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //输出当前时间
        Date date = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat() ;

        String dateString = simpleDateFormat.format(date);

        System.out.println("执行定时任务，时间是"+dateString);
    }
}
