package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.entity.Entity;

public class EntityTableNamePItem extends NoParamsPItem {

	public void joinSql(Entity<?> en, StringBuilder sb) {
		sb.append(_en(en).getTableName());
	}

}
