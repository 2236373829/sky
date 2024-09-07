package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：用于表示某个方法需要进行公共字段的填充
 *
 * @author xyzZero3
 * @date 2024/9/6 16:10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFIll {

    //数据库操作类型：INSERT UPDATE
    OperationType value();

}
