package net.zzong.ormtest.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EntityTransaction;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by 김종인 on 2017-02-23.
 */
@AllArgsConstructor
public class DefaultEntityTransaction implements EntityTransaction {
    @Getter
    private Connection connection;
    private EntityManager entityManager;

    @Override
    public void begin() {
        try {
            if(connection==null||connection.isClosed()){
                this.connection = this.entityManager.getDataSource().getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRollbackOnly() {
    }

    @Override
    public boolean getRollbackOnly() {
        return false;
    }

    @Override
    public boolean isActive() {
        try {
            return !this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
