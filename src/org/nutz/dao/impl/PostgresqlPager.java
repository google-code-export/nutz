package org.nutz.dao.impl;

import org.nutz.dao.Pager;
import org.nutz.dao.entity.Entity;

public class PostgresqlPager extends Pager {

	@Override
	protected String getLimitString(Entity<?> entity) {
		return String.format("%%s LIMIT %d OFFSET %d", this.getPageSize(),this.getOffset());
	}

}
