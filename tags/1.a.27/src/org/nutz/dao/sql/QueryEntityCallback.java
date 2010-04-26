package org.nutz.dao.sql;

import java.sql.ResultSet;

import java.sql.SQLException;

import org.nutz.dao.entity.Entity;

public class QueryEntityCallback extends EntityCallback {

	@Override
	protected Object process(final ResultSet rs, final Entity<?> entity, final SqlContext context)
			throws SQLException {
		return new ResultSetLooping() {
			protected Object createObject(ResultSet rs, SqlContext context) {
				return entity.getObject(rs, context.getMatcher());
			}
		}.doLoop(rs, context);
	}

}
