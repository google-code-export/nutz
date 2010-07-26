package org.nutz.dao.sql;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.Daos;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;

public class SqlImpl implements Sql, Cloneable {

	public SqlImpl(SqlLiteral sql, StatementAdapter adapter) {
		this.sql = sql;
		this.adapter = adapter;
		this.context = new SqlContext();
		updateCount = -1;
	}

	private SqlLiteral sql;
	private SqlContext context;
	private SqlCallback callback;
	private Condition condition;
	private StatementAdapter adapter;
	private Entity<?> entity;
	private int updateCount;

	public String toString() {
		mergeCondition();
		return sql.toString();
	}

	public void execute(Connection conn) throws DaoException {
		mergeCondition();
		updateCount = -1;
		boolean statIsClosed = false;
		try {
			// SELECT ...
			if (sql.isSELECT()) {
				// If without callback, the query do NOT make sense.
				if (null != callback) {
					// Create ResultSet type upon the page. default is
					// TYPE_FORWARD_ONLY
					Pager pager = context.getPager();
					int rsType = null == pager	? ResultSet.TYPE_FORWARD_ONLY
												: pager.getResultSetType();
					PreparedStatement stat = null;
					ResultSet rs = null;
					try {
						// Prepare statment for query
						String sqlStr = sql.toPreparedStatementString();
						stat = conn.prepareStatement(sqlStr, rsType, ResultSet.CONCUR_READ_ONLY);

						// Put all parameters to PreparedStatement and get
						// ResultSet
						adapter.process(stat, sql, entity);
						rs = stat.executeQuery();

						// Get result from ResultSet by callback
						context.setResult(callback.invoke(conn, rs, this));
					}
					// Closing...
					finally {
						Daos.safeClose(stat, rs);
					}
				}
			}
			// UPDATE | INSERT | DELETE | TRUNCATE ...
			else if (sql.isUPDATE() || sql.isINSERT() || sql.isDELETE() || sql.isTRUNCATE()) {
				PreparedStatement stat = null;
				try {
					String sqlStr = sql.toPreparedStatementString();
					stat = conn.prepareStatement(sqlStr);
					adapter.process(stat, sql, entity);
					stat.execute();
					updateCount = stat.getUpdateCount();
					stat.close();
					statIsClosed = true;
					if (null != callback)
						context.setResult(callback.invoke(conn, null, this));
				}
				// Closing...
				finally {
					if (!statIsClosed)
						Daos.safeClose(stat);
				}
			}
			// CREATE | DROP
			else {
				Statement stat = null;
				try {
					stat = conn.createStatement();
					stat.execute(sql.toString());
					stat.close();
					if (null != callback)
						context.setResult(callback.invoke(conn, null, this));
				}// Closing...
				finally {
					if (!statIsClosed)
						Daos.safeClose(stat);
				}
			}
		}
		// If any SQLException happend, throw out the SQL string
		catch (SQLException e) {
			throw new DaoException(format(	"!Nuz SQL Error: '%s'\nPreparedStatement: \n'%s'",
											sql.toString(),
											sql.toPreparedStatementString()), e);
		}

	}

	private void mergeCondition() {
		if (null != condition) {
			String cnd = Sqls.getConditionString(entity, condition);
			if (null != cnd)
				sql.getVars().set("condition", cnd);
		}
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public Sql setAdapter(StatementAdapter adapter) {
		this.adapter = adapter;
		return this;
	}

	public Sql setEntity(Entity<?> entity) {
		this.entity = entity;
		return this;
	}

	public VarSet params() {
		return sql.getParams();
	}

	public VarSet vars() {
		return sql.getVars();
	}

	public VarIndex paramIndex() {
		return sql.getParamIndexes();
	}

	public VarIndex varIndex() {
		return sql.getVarIndexes();
	}

	public SqlContext getContext() {
		return context;
	}

	public SqlCallback getCallback() {
		return callback;
	}

	public Sql setCallback(SqlCallback callback) {
		this.callback = callback;
		return this;
	}

	public Condition getCondition() {
		return condition;
	}

	public Sql setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public Object getResult() {
		return context.getResult();
	}

	public Entity<?> getEntity() {
		return entity;
	}

	public SqlLiteral getLiteral() {
		return sql;
	}

	public Sql duplicate() {
		Sql newSql = new SqlImpl(sql, DefaultStatementAdapter.ME);
		newSql.setCallback(callback).setCondition(condition);
		newSql.getContext().setPager(context.getPager()).setMatcher(context.getMatcher());
		return newSql;
	}

	@Override
	public Object clone() {
		return duplicate();
	}

	public int getInt() {
		return Castors.me().castTo(context.getResult(), int.class);
	}

	public String getString() {
		return Castors.me().castToString(context.getResult());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> List<T> getList(Class<T> classOfT) {
		Object result = context.getResult();
		if (null == result)
			return null;
		if (result instanceof List) {

			// Empty list
			if (((List<T>) result).isEmpty())
				return (List<T>) result;
			// The list item same type with classOfT
			if (classOfT.isAssignableFrom(((List) result).get(0).getClass()))
				return (List<T>) result;

			// Convert each element in the list
			ArrayList list = new ArrayList(((List) result).size());
			Iterator it = ((List) result).iterator();
			while (it.hasNext()) {
				list.add(Castors.me().castTo(it.next(), classOfT));
			}
			return list;
		}
		return Castors.me().cast(result, result.getClass(), List.class, classOfT.getName());
	}

	public <T> T getObject(Class<T> classOfT) {
		return Castors.me().castTo(context.getResult(), classOfT);
	}

}
