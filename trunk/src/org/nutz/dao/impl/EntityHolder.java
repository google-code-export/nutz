package org.nutz.dao.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.EjectFromMap;
import org.nutz.lang.inject.InjectToMap;

/**
 * 封装一些获取实体对象的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class EntityHolder {

	// DaoSupport 会设置这个值
	EntityMaker maker;

	private DaoSupport support;

	private Map<Class<?>, Entity<?>> map;

	public EntityHolder(DaoSupport support) {
		this.support = support;
		this.map = new HashMap<Class<?>, Entity<?>>();
	}

	public void set(Entity<?> en) {
		this.map.put(en.getType(), en);
	}

	/**
	 * 根据类型获取实体
	 * 
	 * @param classOfT
	 *            实体类型
	 * @return 实体
	 */
	@SuppressWarnings("unchecked")
	public <T> Entity<T> getEntity(Class<T> classOfT) {
		Entity<?> re = map.get(classOfT);
		if (null == re) {
			synchronized (map) {
				re = map.get(classOfT);
				if (null == re) {
					re = maker.make(classOfT);
					map.put(classOfT, re);
				}
			}
		}
		return (Entity<T>) re;
	}

	/**
	 * @param <T>
	 * @param tableName
	 * @param map
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public <T extends Map<String, ?>> Entity<T> makeEntity(String tableName, T map) {
		final NutEntity<T> en = new NutEntity(map.getClass());
		en.setTableName(tableName);
		en.setViewName(tableName);
		for (String key : map.keySet()) {
			// 是实体补充描述吗？
			if (key.startsWith("#")) {
				en.getMetas().put(key.substring(1), map.get(key).toString());
				continue;
			}
			// 以 "." 开头的字段，不是实体字段
			else if (key.startsWith(".")) {
				continue;
			}

			// 是实体字段
			Mirror<?> mirror = Mirror.me(map.get(key));
			Object value = map.get(key);
			NutMappingField ef = new NutMappingField(en);

			if (key.startsWith("+")) {
				ef.setAsAutoIncreasement();
				key = key.substring(1);
			}
			if (key.startsWith("!")) {
				ef.setAsNotNull();
				key = key.substring(1);
			}
			if (key.startsWith("*")) {
				key = key.substring(1);
				if (mirror != null && mirror.isIntLike())
					ef.setAsId();
				else
					ef.setAsName();
			}
			ef.setName(key);

			ef.setType(null == value ? Object.class : value.getClass());
			ef.setColumnName(key);

			// 猜测一下数据库类型
			Jdbcs.guessEntityFieldColumnType(ef);
			ef.setAdaptor(support.expert.getAdaptor(ef));
			ef.setType(mirror.getType());
			ef.setInjecting(new InjectToMap(key));
			ef.setEjecting(new EjectFromMap(key));

			en.addMappingField(ef);
		}
		en.checkCompositeFields(null);

		// 最后在数据库中验证一下实体各个字段
		support.run(new ConnCallback() {
			public void invoke(Connection conn) throws Exception {
				support.expert.setupEntityField(conn, en);
			}
		});

		// 搞定返回
		return en;
	}

	/**
	 * 根据一个对象获取实体
	 * <p>
	 * 对象如果是集合或者数组，则取其第一个元素进行判断
	 * 
	 * @param obj
	 *            对象
	 * @return 实体
	 */
	@SuppressWarnings("unchecked")
	public Entity<?> getEntityBy(Object obj) {
		// 这是一个 Map,试图构建一个 entity
		if (obj instanceof Map<?, ?>) {
			Object tableName = ((Map<String, ?>) obj).get(".table");
			if (null == tableName)
				throw Lang.makeThrow(	"Can not insert map without key '.table' : \n%s",
										Json.toJson(obj, JsonFormat.forLook()));
			return makeEntity(tableName.toString(), (Map<String, ?>) obj);
		}

		// 正常的构建一个 Entity
		Object first = Lang.first(obj);
		if (first == null)
			throw Lang.makeThrow("Can not evaluate entity for empty object");

		return null == first ? null : getEntity(first.getClass());
	}

}
