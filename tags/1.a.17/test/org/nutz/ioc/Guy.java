package org.nutz.ioc;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.nutz.lang.meta.Email;

public class Guy {
	public String name;
	public boolean dead;
	public Email[] emails;
	public int age;
	public float health;
	public long ms;
	public Timestamp birthday;
	public Date workSince;
	public Time sleepWhen;
	public Guy father;
	public String tomcatHome;
	public String fromJava;
	
	public static Email getStaticTestEmailObject(){
		return new Email("zzh","gmail.com");
	}
}