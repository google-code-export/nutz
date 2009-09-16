package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;

public class ComboSql implements Sql {

	public ComboSql() {
		sqls = new LinkedList<Sql>();
		varss = new ComboVarSet();
		holderss = new ComboVarSet();
	}

	private List<Sql> sqls;
	private ComboVarSet varss;
	private ComboVarSet holderss;

	public ComboSql add(Sql sql) {
		sqls.add(sql);
		varss.add(sql.vars());
		holderss.add(sql.holders());
		return this;
	}

	public ComboSql clear() {
		sqls.clear();
		varss.clear();
		holderss.clear();
		return this;
	}

	public Sql duplicate() {
		ComboSql re = new ComboSql();
		for (Sql sql : sqls)
			re.add(sql.duplicate());
		return re;
	}

	public void execute(Connection conn) throws SQLException {
		if (sqls.isEmpty())
			return;
		boolean old = conn.getAutoCommit();
		Savepoint savepoint = conn.setSavepoint();
		try {
			conn.setAutoCommit(false);
			for (Sql sql : sqls)
				sql.execute(conn);
			conn.commit();
		} catch (Exception e) {
			conn.rollback(savepoint);
		} finally {
			conn.setAutoCommit(old);
		}
	}

	public SqlContext getContext() {
		if (sqls.isEmpty())
			return null;
		return sqls.get(0).getContext();
	}

	public Object getResult() {
		List<Object> list = new ArrayList<Object>(sqls.size());
		for (Sql sql : sqls)
			list.add(sql.getResult());
		return list;
	}

	public int getUpdateCount() {
		int re = -1;
		for (Sql sql : sqls) {
			if (sql.getUpdateCount() != -1) {
				if (re == -1)
					re = 0;
				re += sql.getUpdateCount();
			}
		}
		return re;
	}

	public VarSet holders() {
		return holderss;
	}

	public Sql setAdapter(FieldTypeAdapter adapter) {
		for (Sql sql : sqls)
			sql.setAdapter(adapter);
		return this;
	}

	public Sql setCallback(SqlCallback callback) {
		for (Sql sql : sqls)
			sql.setCallback(callback);
		return this;
	}

	public Sql setCondition(Condition condition) {
		for (Sql sql : sqls)
			sql.setCondition(condition);
		return this;
	}

	public Sql setEntity(Entity<?> entity) {
		for (Sql sql : sqls)
			sql.setEntity(entity);
		return this;
	}

	public VarSet vars() {
		return null;
	}

	public Entity<?> getEntity() {
		if (sqls.isEmpty())
			return null;
		return sqls.get(0).getEntity();
	}

	public Sql setResult(Object result) {
		for (Sql sql : sqls)
			sql.setResult(result);
		return this;
	}
}
