package net.zzong.ormtest;

import net.zzong.ormtest.model.User;
import net.zzong.ormtest.persist.EntityManager;
import net.zzong.ormtest.persist.EntityManagerFactory;
import net.zzong.ormtest.setting.SettingLoad;
import net.zzong.ormtest.setting.SettingVO;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.persistence.EntityTransaction;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by 김종인 on 2017-02-20.
 */
public class Main {
    public static void main(String... args){
        SettingVO settingVO = null;
        try {
            settingVO = SettingLoad.loadProperties(new FileInputStream(ClassLoader.getSystemResource("db.properties").getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BasicDataSource basicDataSource = SettingLoad.loadBasicDataSource(settingVO);

        EntityManagerFactory entityManagerFactory = new EntityManagerFactory(basicDataSource, Arrays.asList(User.class));
        EntityManager entityManager = entityManagerFactory.getEntityManager();
        EntityTransaction et = entityManager.getEntityTransaction();

        et.begin();
        for (User user : entityManager.findAll(User.class)) {
            System.out.println(user);
        }
        et.commit();

        entityManager.close();
        entityManagerFactory.close();
    }
}
