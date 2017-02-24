package net.zzong.ormtest.persist;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 김종인 on 2017-02-21.
 */
public class EntityManagerFactory {

    @Getter
    private Queue<EntityManager> entityManagers;

    @Getter
    private BasicDataSource dataSource;

    @Getter
    private Map<Class, EntityInformation<Class>> entityInfomationMap;

    public EntityManagerFactory(BasicDataSource dataSource, List<Class> entytiList) {
        this.entityManagers = new ConcurrentLinkedDeque<>();
        this.dataSource = dataSource;
        this.entityInfomationMap = setEntityInfomationMap(entytiList);
    }

    private Map<Class,EntityInformation<Class>> setEntityInfomationMap (List<Class> entytiList){
        Map<Class,EntityInformation<Class>> entityInfomationMap = new ConcurrentHashMap<>();
        if(entytiList==null || entytiList.size() <= 0) throw new RuntimeException("entytiList 값이 없다");
        for (Class aClass : entytiList) {
                entityInfomationMap.put(aClass, new EntityInformation(aClass));
        }
        return entityInfomationMap;
    }

    public EntityManager getEntityManager() {
        EntityManager entityManager = null;
        try {
            entityManager = new EntityManager(this.dataSource, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        entityManagers.add(entityManager);
        return entityManager;
    }

    public void close(){
        this.entityManagers.stream().forEach(EntityManager::close);
    }
}
