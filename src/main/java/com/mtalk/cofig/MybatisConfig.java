package com.mtalk.cofig;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.mtalk.mapper")
public class MybatisConfig {

}
