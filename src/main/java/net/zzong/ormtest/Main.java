package net.zzong.ormtest;

import net.zzong.ormtest.model.User;
import net.zzong.ormtest.persist.EntityManager;
import net.zzong.ormtest.persist.EntityManagerFactory;

import java.util.*;

/**
 * Created by 김종인 on 2017-02-20.
 */
public class Main {
    public static void main(String... args){
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory("db.properties", Arrays.asList(User.class));
        EntityManager entityManager = entityManagerFactory.getEntityManager();

        for (User user : entityManager.findAll(User.class)) {
            System.out.println(user);
        }
        entityManager.close();
        entityManagerFactory.close();
    }

}
