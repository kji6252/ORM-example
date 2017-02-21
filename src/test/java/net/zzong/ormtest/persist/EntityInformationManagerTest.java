package net.zzong.ormtest.persist;

import net.zzong.ormtest.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by 김종인 on 2017-02-21.
 */
public class EntityInformationManagerTest {

    @Test
    public void 엔티티매니저확인() throws SQLException, ClassNotFoundException, IOException {
        EntityManager entityManager = new EntityManager("db.properties", Arrays.asList(User.class));

        System.out.println(entityManager.getEntityInfomationMap());
    }

    @Test(expected = RuntimeException.class)
    public void 엔티티아닌거확인() throws SQLException, ClassNotFoundException, IOException {
        new EntityManager("db.properties", Arrays.asList(List.class));
    }

    @Test(expected = RuntimeException.class)
    public void 디비정보아닐떄확인() throws SQLException, ClassNotFoundException, IOException {
        new EntityManager("dfdb.properties", Arrays.asList(User.class));
    }

    @Test
    public void 전체조회확인() throws SQLException, ClassNotFoundException, IOException {
        EntityManager entityManager = new EntityManager("db.properties", Arrays.asList(User.class));

        for (User user : entityManager.findAll(User.class)) {
            System.out.println(user);
        }
        ;
    }


}
