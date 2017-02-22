package net.zzong.ormtest.persist;

import lombok.Getter;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 김종인 on 2017-02-21.
 */
public class EntityManagerFactory {

    public static final String DBCP_DRIVER = "org.apache.commons.dbcp2.PoolingDriver";
    public static Class dirverClass;
    private static PoolingDriver driver;


    private String entityManagerFactoryName;
    @Getter
    private Map<Class, EntityInformation<Class>> entityInfomationMap;

    @Getter
    private Queue<EntityManager> entityManagers;

    public EntityManagerFactory(String propertyName, List<Class> entytiList) {
        initConnectionPool(propertyName);
        entityManagers = new ConcurrentLinkedDeque<>();
        this.entityInfomationMap = setEntityInfomationMap(entytiList);
        try {
            init(DriverManager.getConnection("jdbc:apache:commons:dbcp:"+this.entityManagerFactoryName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param driver
     */
    public void registerJDBCDriver(String driver) {
        try {
            dirverClass = Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("There was not able to find the driver class");
        }
    }

        /**
         * Get a Connection Factory, the default implementation is a
         * DriverManagerConnectionFactory
         *
         * @param connectionURI
         * @param user
         * @param password
         * @return The Factory
         */
    public ConnectionFactory getConnFactory(String connectionURI,
                                                   String user, String password) {
        ConnectionFactory driverManagerConnectionFactory = new DriverManagerConnectionFactory(
                connectionURI, user, password);
        return driverManagerConnectionFactory;
    }

        /**
         *
         * @return the DBCP Driver
         */
    public PoolingDriver getDBCPDriver() {
        try {
            Class.forName(DBCP_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("There was not able to find the driver class");
        }
        try {
            driver = (PoolingDriver) DriverManager
                    .getDriver("jdbc:apache:commons:dbcp:");
        } catch (SQLException e) {
            System.err.println("There was an error: " + e.getMessage());
        }
        return driver;
    }


    private Map<Class,EntityInformation<Class>> setEntityInfomationMap (List<Class> entytiList){
        Map<Class,EntityInformation<Class>> entityInfomationMap = new ConcurrentHashMap<>();
        if(entytiList==null || entytiList.size() <= 0) throw new RuntimeException("entytiList 값이 없다");
        for (Class aClass : entytiList) {
                entityInfomationMap.put(aClass, new EntityInformation(aClass));
        }
        return entityInfomationMap;
    }

    private void initConnectionPool(String propertyName) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(ClassLoader.getSystemResource(propertyName).getFile()));

            // 1. Register the Driver to the jbdc.driver java property
            registerJDBCDriver(prop.getProperty("driver"));
            // 2. Create the Connection Factory (DriverManagerConnectionFactory)
            ConnectionFactory connectionFactory = getConnFactory(prop.getProperty("url")
                                                    ,prop.getProperty("user")
                                                    ,prop.getProperty("passwd"));
            // 3. Instantiate the Factory of Pooled Objects
            PoolableConnectionFactory poolfactory = new PoolableConnectionFactory(
                    connectionFactory, null);
            // 4. Create the Pool with the PoolableConnection objects
            ObjectPool connectionPool = new GenericObjectPool(
                    poolfactory);
            // 5. Set the objectPool to enforces the association (prevent bugs)
            poolfactory.setPool(connectionPool);

            // 6. Get the Driver of the pool and register them
            PoolingDriver dbcpDriver = getDBCPDriver();
            dbcpDriver.registerPool(prop.getProperty("entity-manager-factory-name"), connectionPool);

            this.entityManagerFactoryName=prop.getProperty("entity-manager-factory-name");
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    private void init(Connection connection) throws SQLException {
        //@Eneity의 이름에 있는 테이블명과 select문을 조합하여 쿼리를 보냄
        try(Statement statement = connection.createStatement();) {
            statement.execute("DROP TABLE USERS IF EXISTS");
            statement.execute("CREATE TABLE USERS(id SERIAL, name VARCHAR(255), email VARCHAR(255), regdt DATE )");
            statement.execute("INSERT INTO USERS (name, email, regdt) VALUES ('철수','cccc@naver.com',SYSDATE)");
            statement.execute("INSERT INTO USERS (name, email, regdt) VALUES ('영희','cccc@naver.com',SYSDATE)");
        } catch (SQLException e) {
            connection.close();
            e.printStackTrace();
        } finally {
            connection.close();
        }

    }


    public EntityManager getEntityManager() {
        EntityManager entityManager = null;
        try {
            entityManager = new EntityManager(DriverManager.getConnection("jdbc:apache:commons:dbcp:"+this.entityManagerFactoryName), this);
            entityManagers.add(entityManager);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entityManager;
    }


    public void close(){
        try {
            entityManagers.stream().forEach(EntityManager::close);
            driver.closePool(this.entityManagerFactoryName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
