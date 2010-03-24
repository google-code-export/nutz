package org.nutz.lang.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;

/**
 * 可以用来存储无序名值对
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Context {

	private HashMap<String, Object> map;

	public Context() {
		this.map = new HashMap<String, Object>();
	}

	public Context set(String name, Object value) {
		map.put(name, value);
		return this;
	}

	public Set<String> keys() {
		return map.keySet();
	}

	public Context putAll(Map<String, Object> map) {
		if (map != null) {
			this.map.putAll(map);
		}
		return this;
	}

	public Object get(String name) {
		return map.get(name);
	}

	public <T> T getAs(Class<T> type, String name) {
		return Castors.me().castTo(get(name), type);
	}

	public int getInt(String name) {
		return getAs(int.class, name);
	}

	public String getString(String name) {
		return getAs(String.class, name);
	}

	public boolean getBoolean(String name) {
		return getAs(boolean.class, name);
	}

	public float getFloat(String name) {
		return getAs(float.class, name);
	}
}
