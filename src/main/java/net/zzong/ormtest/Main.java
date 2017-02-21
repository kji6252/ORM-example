package net.zzong.ormtest;

import net.zzong.ormtest.model.User;
import net.zzong.ormtest.persist.EntityManager;

import java.util.*;

/**
 * Created by 김종인 on 2017-02-20.
 */
public class Main {
    public static void main(String... args){
        EntityManager entityManager = new EntityManager("db.properties", Arrays.asList(User.class));

        for (User user : entityManager.findAll(User.class)) {
            System.out.println(user);
        }
        System.out.println("sdfsdf");

        entityManager.close();
    }
}
