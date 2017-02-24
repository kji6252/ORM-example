package net.zzong.ormtest.setting;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by 김종인 on 2017-02-24.
 */
public class SettingLoad {


    public static SettingVO loadProperties(InputStream InputStream){
            Properties properties = new Properties();
            try {
                properties.load(InputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SettingVO settingVO = new SettingVO(properties.getProperty("driver")
                    ,properties.getProperty("url")
                    ,properties.getProperty("user")
                    ,properties.getProperty("password")
            );
            return settingVO;
    }

    public static BasicDataSource loadBasicDataSource(SettingVO settingVO){
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(settingVO.getDriver());
            dataSource.setUrl(settingVO.getUrl());
            dataSource.setUsername(settingVO.getUser());
            dataSource.setPassword(settingVO.getPassword());
            return dataSource;
    }

}
