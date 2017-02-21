package net.zzong.ormtest.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "USERS")
public class User {
	@Id @GeneratedValue
	private long id;
	private String name;
	private String email;
	private Date regDt;
}
