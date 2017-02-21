package net.zzong.ormtest.persist;

import lombok.Data;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * Created by 김종인 on 2017-02-21.
 */
@Data
public class  EntityInformation<T> {
    Class<T> entityClass;
    String nativeTableName;
    BeanInfo beanInfo;
    PropertyDescriptor[] propertyDescriptor;

    public EntityInformation(Class<T> entityClass) {
        this.entityClass = entityClass;
        if(null == entityClass.getAnnotation(javax.persistence.Entity.class)) {
            throw new RuntimeException("Entity가 아닙니다.");
        }
        try {
            nativeTableName = entityClass.getAnnotation(javax.persistence.Entity.class).name();
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        propertyDescriptor = beanInfo.getPropertyDescriptors();
    }
}
