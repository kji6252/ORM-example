package net.zzong.ormtest.persist;


import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.persistence.EntityTransaction;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by 김종인 on 2017-02-21.
 */
public class EntityManager {
    @Getter
    BasicDataSource dataSource;
    EntityManagerFactory entityManagerFactory;

    EntityTransaction entityTransaction;

    public EntityManager(BasicDataSource dataSource, EntityManagerFactory entityManagerFactory) {
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
    }

    public <T> List<T> findAll(Class<T> entityClass){

        List<T> entityObjects = new ArrayList<T>();
        EntityInformation<T> entityInformation = (EntityInformation<T>) this.entityManagerFactory.getEntityInfomationMap().get(entityClass);
        //connection.


        try (Statement statement = dataSource.getConnection().createStatement()) {
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

    public EntityTransaction getEntityTransaction() {
            try {
                if(entityTransaction==null)
                    this.entityTransaction= new DefaultEntityTransaction(this.dataSource.getConnection(),this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        return this.entityTransaction;
    }


    public void close(){
        entityManagerFactory.getEntityManagers().remove(this);
    }
}
