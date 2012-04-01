package org.nutz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.zip.GZIPOutputStream;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * Prepare a database with URL: jdbc:mysql://localhost:3306/zzhtest support user
 * root@123456
 * 
 * @author zozoh
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AllWithDB.class, AllWithoutDB.class})
public class TestAll {
	
	private static final String name = "nutz";
	
	public static void main(String[] args) throws Exception {
		Field field = TestAll.class.getDeclaredField("name");
		field.setAccessible(true);
		field.set(null, "wendal");
	}
}
