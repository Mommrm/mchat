package com.mtalk.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryUtil {

    SqlSessionFactory sqlSessionFactory;
    SqlSession sqlSession;

    public void SqlSessionFactoryUtil() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        sqlSession = sqlSessionFactory.openSession();
    }

    public SqlSession getSqlSession(){
        return sqlSession;
    }

    public Object getObjectMapper(Class c){
        return sqlSession.getMapper(c);
    }
}