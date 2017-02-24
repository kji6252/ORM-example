package net.zzong.ormtest.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 김종인 on 2017-02-24.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SettingVO {
    String driver;
    String url;
    String user;
    String password;
}
