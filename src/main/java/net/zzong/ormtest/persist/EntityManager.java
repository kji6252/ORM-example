package net.zzong.ormtest.persist;

import lombok.Getter;

import javax.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by 김종인 on 2017-02-21.
 */
public class EntityManager {
    Connection connection;

    @Getter
    private Map<Class, EntityInformation<Class>> entityInfomationMap;

    public EntityManager(String propertyName, List<Class> entytiList) {
        this.connection = getConnection(propertyName);
        this.entityInfomationMap = setEntityInfomationMap(entytiList);
        init();
    }
    private Map<Class,EntityInformation<Class>> setEntityInfomationMap (List<Class> entytiList){
        Map<Class,EntityInformation<Class>> entityInfomationMap = new HashMap<>();
        if(entytiList==null || entytiList.size() <= 0) throw new RuntimeException("entytiList 값이 없다");
        for (Class aClass : entytiList) {
                entityInfomationMap.put(aClass, new EntityInformation(aClass));
        }
        return entityInfomationMap;
    }

    private Connection getConnection(String propertyName) {
        Properties prop = new Properties();
        Connection connection = null;
        try {
            prop.load(new FileInputStream(ClassLoader.getSystemResource(propertyName).getFile()));
            Class.forName(prop.getProperty("driver"));
            connection = DriverManager.getConnection(prop.getProperty("url")
                    , prop.getProperty("user")
                    , prop.getProperty("passwd"));
            DriverManager.getConnection(prop.getProperty("url")
                    ,prop.getProperty("user")
                    ,prop.getProperty("passwd"));
        } catch (IOException e ) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void init(){
        //@Eneity의 이름에 있는 테이블명과 select문을 조합하여 쿼리를 보냄
        try(Statement statement = connection.createStatement();) {
            statement.execute("DROP TABLE USERS IF EXISTS");
            statement.execute("CREATE TABLE USERS(id SERIAL, name VARCHAR(255), email VARCHAR(255), regdt DATE )");
            statement.execute("INSERT INTO USERS (name, email, regdt) VALUES ('철수','cccc@naver.com',SYSDATE)");
            statement.execute("INSERT INTO USERS (name, email, regdt) VALUES ('영희','cccc@naver.com',SYSDATE)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public <T> List<T> findAll(Class<T> entityClass){

        List<T> entityObjects = new ArrayList<T>();
        EntityInformation<T> entityInformation = (EntityInformation<T>) entityInfomationMap.get(entityClass);


        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("select * from " + entityInformation.getNativeTableName())) {
                System.out.println("select * from " + entityInformation.getNativeTableName());
                while (rs.next()) {

                    T entityObject= entityInformation.getEntityClass().newInstance();

                    for (PropertyDescriptor propertyDescriptor : entityInformation.getPropertyDescriptor()) {
                        //entity Property정보엔 Class도 포함이므로 빼야함
                        if(!(propertyDescriptor.getPropertyType().getName().equals("java.lang.Class"))) {
                            propertyDescriptor.getWriteMethod()
                                    .invoke(entityObject ,rs.getObject(propertyDescriptor.getName()));
                        }
                    }
                    entityObjects.add(entityObject);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entityObjects;
    }


    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
