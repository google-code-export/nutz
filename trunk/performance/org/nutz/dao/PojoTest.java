package org.nutz.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.test.meta.Pet;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Lang;

public class PojoTest {

	public static void main(String[] args) {
		IocLoader loader = new JsonLoader("ioc.js");
		Ioc ioc = new NutIoc(loader);
		Pet pet = ioc.get(Pet.class, "pet");
		System.out.println(pet.getName());
	}

}

class VoCallback implements SqlCallback {

	private Class<?> klass;
	public VoCallback(Class<?> klass) {
		this.klass = klass;
	}
	
	public Object invoke(Connection conn, ResultSet rs, Sql sql)
			throws SQLException {
		List list = new ArrayList(); 
		while(rs.next()) {
			try {
				Object obj = klass.newInstance();
				Field[] fields = klass.getDeclaredFields();
				for (Field field : fields) {
					Object f = rs.getObject(field.getName());
					if (f != null)
						field.set(obj, Castors.me().castTo(f, field.getType()));
				}
				list.add(obj);
			} catch (Throwable e) {
				throw Lang.wrapThrow(e);
			}
		}
		return list;
	}
	
}
