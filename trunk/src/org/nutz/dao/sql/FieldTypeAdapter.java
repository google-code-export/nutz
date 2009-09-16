package org.nutz.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;

public interface FieldTypeAdapter {

	void process(PreparedStatement stat, SqlLiteral sql, Entity<?> entity) throws SQLException;

}
