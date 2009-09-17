package org.nutz.dao.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.nutz.castor.Castors;
import org.nutz.dao.Condition;
import org.nutz.dao.ConnectionHolder;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.Database;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Pager;
import org.nutz.dao.sql.SQLs;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlMaker;
import org.nutz.dao.SqlManager;
import org.nutz.dao.Sqls;
import org.nutz.dao.Chain;
import org.nutz.dao.callback.ConnCallback;
import org.nutz.dao.callback.QueryCallback;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityHolder;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class NutDao implements Dao {

	private static <T> EntityField checkIdField(Entity<T> en) {
		EntityField idField = en.getIdField();
		if (idField == null) {
			throw Lang.makeThrow("Entity [%] need @Id field", en.getMirror().getType().getName());
		}
		return idField;
	}

	private static <T> EntityField checkNameField(Entity<T> en) {
		EntityField nameField = en.getNameField();
		if (nameField == null) {
			throw Lang.makeThrow("Entity [%] need @Name field", en.getMirror().getType().getName());
		}
		return nameField;
	}

	public static final QueryCallback<Integer> evalResultSetAsInt = new QueryCallback<Integer>() {

		public Integer invoke(ResultSet rs) throws SQLException {
			return rs.getInt(1);
		}
	};

	private DataSource dataSource;
	private SqlMaker maker;
	private SqlManager sqls;
	private EntityHolder entities;
	private Database database;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setMaker(SqlMaker sqlMaker) {
		this.maker = sqlMaker;
	}

	/**
	 * 'databaseProductName' | 'driverName'
	 * 
	 * <pre>
	 * psql:	'PostgreSQL'	|'PostgreSQL Native Driver'
	 * MySQL:	'MySQL'			|'MySQL-AB JDBC Driver'
	 * Oracle:	'Oracle'		|'Oracle JDBC driver'
	 * db2:		'DB2/NT'		|'IBM DB2 JDBC Universal Driver Architecture'
	 * SQLServer:	'Microsoft SQL Serve'	|'SQL Serve'
	 * </pre>
	 */
	public void setDataSource(DataSource dataSource) {
		entities = new EntityHolder();
		this.dataSource = dataSource;
	}

	private synchronized void checkDatabase() {
		final Database[] holder = new Database[1];
		this.run(new ConnCallback() {
			public Object invoke(Connection conn) throws Exception {
				DatabaseMetaData dmd = conn.getMetaData();
				String proName = dmd.getDatabaseProductName().toLowerCase();
				if (proName.startsWith("postgresql")) {
					holder[0] = new Database.Postgresql();
				} else if (proName.startsWith("mysql")) {
					holder[0] = new Database.Mysql();
				} else if (proName.startsWith("oracle")) {
					holder[0] = new Database.Oracle();
				} else if (proName.startsWith("db2")) {
					holder[0] = new Database.DB2();
				} else if (proName.startsWith("microsoft sql")) {
					holder[0] = new Database.SQLServer();
				} else {
					holder[0] = new Database.Unknwon();
				}
				return null;
			}
		});
		database = holder[0];
	}

	public Database database() {
		if (null == database) {
			checkDatabase();
		}
		return database;
	}

	public Pager createPager(int pageNumber, int pageSize) {
		return database().createPager(pageNumber, pageSize);
	}

	public Class<? extends Pager> getPagerType() {
		return database().getPagerType();
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqls = sqlManager;
	}

	public SqlMaker getMaker() {
		return maker;
	}

	public SqlManager getSqls() {
		return sqls;
	}

	public SqlMaker maker() {
		return maker;
	}

	public SqlManager sqls() {
		return this.sqls;
	}

	public NutDao() {
		this.maker = new SqlMaker();
	}

	public NutDao(DataSource dataSource) {
		this();
		this.setDataSource(dataSource);
	}

	public NutDao(DataSource dataSource, SqlManager sqlManager) {
		this();
		this.setDataSource(dataSource);
		this.setSqlManager(sqlManager);
	}

	public void execute(final Sql... sqls) {
		run(new ConnCallback() {
			public Object invoke(Connection conn) throws Exception {
				for (int i = 0; i < sqls.length; i++) {
					if (null != sqls[i])
						sqls[i].execute(conn);
				}
				return null;
			}
		});
	}

	public void run(ConnCallback callback) {
		ConnectionHolder ch = Sqls.getConnection(getDataSource());
		try {
			ch.invoke(callback);
		} catch (Throwable e) {
			try {
				ch.rollback();
			} catch (SQLException e1) {}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		} finally {
			Sqls.releaseConnection(ch);
		}

	}

	public int count(Class<?> classOfT) {
		return count(classOfT, null);
	}

	public int count(Class<?> classOfT, Condition condition) {
		Entity<?> entity = getEntity(classOfT);
		Sql sql = maker.create(maker.ptn.COUNT, entity.getTableName()).setEntity(entity);
		sql.setCallback(SQLs.callback.integer()).setCondition(condition);
		execute(sql);
		return (Integer) sql.getResult();
	}

	public int count(String tableName) {
		return count(tableName, null);
	}

	public int count(String tableName, Condition condition) {
		Sql sql = maker.create(maker.ptn.COUNT, tableName);
		sql.setCallback(SQLs.callback.integer()).setCondition(condition);
		execute(sql);
		return (Integer) sql.getResult();
	}

	public <T> void clear(Class<T> classOfT) {
		this.clear(classOfT, null);
	}

	public void clear(String tableName) {
		this.clear(tableName, null);
	}

	public <T> void clear(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		Sql sql;
		if (null == condition) {
			sql = maker.create(maker.ptn.RESET, entity.getTableName());
		} else {
			sql = maker.create(maker.ptn.CLEAR, entity.getTableName());
			sql.setCondition(condition).setEntity(entity);
		}
		execute(sql);
	}

	public void clear(String tableName, Condition condition) {
		Sql sql;
		if (null == condition) {
			sql = maker.create(maker.ptn.RESET, tableName);
		} else {
			sql = maker.create(maker.ptn.CLEAR, tableName);
			sql.setCondition(condition);
		}
		execute(sql);
	}

	public <T> T clearLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.walkManys(new LinkWalker() {
						void walk(Link link) {
							if (link.getReferField() == null) {
								dao.clear(link.getTargetClass(), null);
							} else {
								Object value = entity.getMirror().getValue(obj,
										link.getReferField());
								Entity<?> ta = dao.getEntity(link.getTargetClass());
								Sql sql = dao.maker().create(dao.maker().ptn.CLEARS_LINKS,
										ta.getTableName());
								sql.vars().set("field",
										ta.getField(link.getTargetField().getName()));
								sql.holders().set("value", value);
								dao.execute(sql);
							}
						}
					});
					lns.walkManyManys(new LinkWalker() {
						void walk(Link link) {
							Object value = entity.getMirror().getValue(obj, link.getReferField());
							Sql sql = dao.maker().create(dao.maker().ptn.CLEARS_LINKS,
									link.getRelation());
							sql.vars().set("field", link.getFrom());
							sql.holders().set("value", value);
							dao.execute(sql);
						}
					});
					lns.walkOnes(new LinkWalker() {
						void walk(Link link) {
							Object value = entity.getMirror().getValue(obj, link.getReferField());
							Entity<?> ta = dao.getEntity(link.getTargetClass());
							Sql sql = dao.maker().create(dao.maker().ptn.CLEARS_LINKS,
									ta.getTableName());
							sql.vars().set("field", ta.getField(link.getTargetField().getName()));
							sql.holders().set("value", value);
							dao.execute(sql);
						}
					});
				}
			});
		}
		return obj;
	}

	public <T> void delete(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		EntityField ef = checkIdField(entity);
		Sql sql = maker.create(maker.ptn.DELETE, entity.getTableName());
		sql.vars().set("field", ef.getColumnName());
		sql.holders().set("value", id);
		execute(sql);
	}

	public <T> void delete(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		EntityField ef = checkNameField(entity);
		Sql sql = maker.create(maker.ptn.DELETE, entity.getTableName());
		sql.vars().set("field", ef.getColumnName());
		sql.holders().set("value", name);
		execute(sql);
	}

	void _deleteSelf(Entity<?> entity, Object obj) {
		if (null != obj) {
			EntityField idnf = entity.getIdentifiedField();
			if (null == idnf)
				throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)", null);
			if (idnf.isId()) {
				int id = Castors.me().castTo(idnf.getValue(obj), Integer.class);
				delete(obj.getClass(), id);
			} else if (idnf.isName()) {
				String name = idnf.getValue(obj).toString();
				delete(obj.getClass(), name);
			} else {
				throw DaoException.create(obj, "$IdentifiedField", "delete(Object obj)",
						new Exception("Wrong identified field"));
			}
		}
	}

	public void delete(Object obj) {
		if (null != obj) {
			Entity<?> entity = getEntity(obj.getClass());
			_deleteSelf(entity, obj);
		}
	}

	public <T> void deleteWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeManys(new DeleteManyInvoker(dao));
					lns.invokeManyManys(new DeleteManyManyInvoker(dao));
					_deleteSelf(entity, obj);
					lns.invokeOnes(new DeleteOneInvoker(dao));
				}
			});
		}
	}

	public <T> void deleteLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<? extends Object> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final NutDao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeManys(new DeleteManyInvoker(dao));
					lns.invokeManyManys(new DeleteManyManyInvoker(dao));
					lns.invokeOnes(new DeleteOneInvoker(dao));
				}
			});
		}
	}

	public <T> T fetch(Class<T> classOfT, long id) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, id);
	}

	public <T> T fetch(Entity<T> entity, long id) {
		EntityField idField = checkIdField(entity);
		FetchSql<T> sql = maker.makeFetchSQL(entity, idField);
		sql.set(idField.getField().getName(), id);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return sql.getResult();
	}

	public <T> T fetch(Class<T> classOfT, String name) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, name);
	}

	public <T> T fetch(Entity<T> entity, String name) {
		EntityField nameField = checkNameField(entity);
		FetchSql<T> sql = maker.makeFetchSQL(entity, nameField);
		sql.set(nameField.getField().getName(), name);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return sql.getResult();
	}

	public <T> T fetch(Class<T> classOfT, Condition condition) {
		Entity<T> entity = getEntity(classOfT);
		return fetch(entity, condition);
	}

	public <T> T fetch(Entity<T> entity, Condition condition) {
		List<T> list = this.query(entity, condition, this.createPager(1, 1));
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public <T> T fetch(Class<T> classOfT) {
		return fetch(classOfT, (Condition) null);
	}

	@SuppressWarnings("unchecked")
	public <T> T fetch(T obj) {
		if (null != obj) {
			Entity<T> entity = (Entity<T>) getEntity(obj.getClass());
			long id = entity.getId(obj);
			if (id != 0)
				return fetch(entity, id);
			String name = entity.getName(obj);
			if (null != name)
				return fetch(entity, name);
		}
		return null;
	}

	public <T> T fetchLinks(final T obj, String regex) {
		if (null != obj && null != regex) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			// Many
			lns.walkManys(new LinkWalker() {
				void walk(Link link) {
					Condition c = null;
					if (link.getReferField() != null) {
						Object value = mirror.getValue(obj, link.getReferField());
						c = new ManyCondition(link, value);
					}
					List<?> list = query(link.getTargetClass(), c, null);
					mirror.setValue(obj, link.getOwnField(), Castors.me().cast(list,
							list.getClass(), link.getOwnField().getType(), link.getMapKeyField()));
				}
			});
			// ManyMany
			lns.walkManyManys(new LinkWalker() {
				void walk(Link link) {
					ManyManyCondition mmc = new ManyManyCondition(dao, link, obj);
					List<?> list = query(link.getTargetClass(), mmc, null);
					mirror.setValue(obj, link.getOwnField(), Castors.me().cast(list,
							list.getClass(), link.getOwnField().getType(), link.getMapKeyField()));
				}
			});
			// one
			lns.walkOnes(new LinkWalker() {
				void walk(Link link) {
					Object one;
					Field ownField = link.getReferField();
					Mirror<?> ownType = Mirror.me(ownField.getType());
					if (ownType.isStringLike()) {
						String name = mirror.getValue(obj, ownField).toString();
						one = fetch(link.getTargetClass(), name);
					} else {
						long id = ((Number) mirror.getValue(obj, ownField)).longValue();
						one = fetch(link.getTargetClass(), id);
					}
					mirror.setValue(obj, link.getOwnField(), one);
				}
			});
		}
		return obj;
	}

	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return entities.getEntity(classOfT, database());
	}

	public int getMaxId(Class<?> classOfT) {
		Entity<?> entity = getEntity(classOfT);
		return evalInt(maker.makeFetchMaxSQL(entity, checkIdField(entity)));
	}

	public <T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm) {
		return getEntity(classOfT).getObject(rs, fm);
	}

	private <T> T _insertSelf(Entity<?> entity, T obj) {
		// prepare insert SQL
		ExecutableSql insertSql = maker.makeInsertSQL(entity, obj);
		insertSql.setValue(obj);
		// Evaluate fetchId SQL
		Sql<Integer> fetchIdSql = null;
		if (null != entity.getIdField() && entity.getIdField().isAutoIncrement()) {
			fetchIdSql = entity.getIdField().getFetchSql();
			if (null == fetchIdSql)
				fetchIdSql = maker.makeFetchMaxSQL(entity, checkIdField(entity)).setCallback(
						evalResultSetAsInt);
		}
		// Execute SQL
		execute(insertSql, fetchIdSql);
		// Update Id field if need
		// @ TODO update all entity ai fields
		if (null != fetchIdSql)
			try {
				entity.getIdField().setValue(obj, fetchIdSql.getResult());
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		return obj;
	}

	public <T> T insert(T obj) {
		if (null != obj) {
			Entity<?> entity = getEntity(obj.getClass());
			return _insertSelf(entity, obj);
		}
		return null;
	}

	/*-------------------------------------------------------------------*/
	private static abstract class InsertInvoker extends LinkInvoker {

		Dao dao;
		Object mainObj;
		Mirror<?> mirror;

		public InsertInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
			this.dao = dao;
			this.mainObj = mainObj;
			this.mirror = mirror;
		}

	}

	/*-------------------------------------------------------------------*/
	private static class InsertOneInvoker extends InsertInvoker {

		public InsertOneInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
			super(dao, mainObj, mirror);
		}

		void invoke(Link link, Object one) {
			one = this.dao.insert(one);
			Mirror<?> ta = Mirror.me(one.getClass());
			Object value = ta.getValue(one, link.getTargetField());
			mirror.setValue(mainObj, link.getReferField(), value);
		}
	};

	/*-------------------------------------------------------------------*/
	private static class InsertManyInvoker extends InsertInvoker {

		public InsertManyInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
			super(dao, mainObj, mirror);
		}

		void invoke(final Link link, Object many) {
			Object first = Lang.first(many);
			if (null != first) {
				Field refer = link.getReferField();
				if (null == refer) {
					Lang.each(many, new Each<Object>() {
						public void invoke(int index, Object ta, int size) throws ExitLoop {
							dao.insert(ta);
						}
					});
				} else {
					final Object value = mirror.getValue(mainObj, refer);
					final Mirror<?> mta = Mirror.me(first.getClass());
					Lang.each(many, new Each<Object>() {
						public void invoke(int index, Object ta, int size) throws ExitLoop {
							mta.setValue(ta, link.getTargetField(), value);
							dao.insert(ta);
						}
					});
				}
			}
		}
	};

	/*-------------------------------------------------------------------*/
	private static class InsertManyManyInvoker extends InsertInvoker {

		public InsertManyManyInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
			super(dao, mainObj, mirror);
		}

		void invoke(final Link link, Object mm) {
			Object first = Lang.first(mm);
			if (null != first) {
				final Object fromValue = mirror.getValue(mainObj, link.getReferField());
				final Mirror<?> mta = Mirror.me(first.getClass());
				Lang.each(mm, new Each<Object>() {
					public void invoke(int i, Object ta, int length) {
						try {
							dao.insert(ta);
						} catch (DaoException e) {
							ta = dao.fetch(ta);
						}
						Object toValue = mta.getValue(ta, link.getTargetField());
						Sql<?> sql = dao.maker().makeInsertManyManySql(link, fromValue, toValue);
						dao.execute(sql);
					}
				});
			}
		}
	};

	/*-------------------------------------------------------------------*/

	public <T> T insertWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeOnes(new InsertOneInvoker(dao, obj, mirror));
					_insertSelf(entity, obj);
					lns.invokeManys(new InsertManyInvoker(dao, obj, mirror));
					lns.invokeManyManys(new InsertManyManyInvoker(dao, obj, mirror));
				}
			});
		}
		return obj;
	}

	public <T> T insertLinks(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Mirror<?> mirror = Mirror.me(obj.getClass());
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeOnes(new InsertOneInvoker(dao, obj, mirror));
					lns.invokeManys(new InsertManyInvoker(dao, obj, mirror));
					lns.invokeManyManys(new InsertManyManyInvoker(dao, obj, mirror));
				}
			});
		}
		return obj;
	}

	public <T> List<T> query(Class<T> classOfT, Condition condition, Pager pager) {
		return query(getEntity(classOfT), condition, pager);
	}

	public <T> List<T> query(Entity<T> entity, Condition condition, Pager pager) {
		QuerySql<T> sql = maker.makeQuerySQL(entity, pager);
		sql.setCondition(condition);
		sql.setCallback(new FetchCallback<T>(entity));
		execute(sql);
		return (List<T>) sql.getResult();
	}

	public <T> T update(T obj) {
		Sql<?> sql = maker.makeUpdateSQL(getEntity(obj.getClass()), obj);
		execute(sql.setValue(obj));
		return obj;
	}

	public void update(Class<?> classOfT, Chain chain, Condition condition) {
		Entity<?> en = getEntity(classOfT);
		ExecutableSql sql = maker.makeBatchUpdateSQL(en, chain);
		execute(sql.setCondition(condition));
	}

	public void updateRelation(Class<?> classOfT, String regex, final Chain chain,
			final Condition condition) {
		final Links lns = new Links(null, getEntity(classOfT), regex);
		Trans.exec(new Atom() {
			public void run() {
				lns.walkManyManys(new LinkWalker() {
					void walk(Link link) {
						ExecutableSql sql = maker.makeBatchUpdateRelationSQL(link, chain);
						sql.setCondition(condition);
						execute(sql);
					}
				});
			}
		});
	}

	/*-------------------------------------------------------------------*/
	private static class UpdateInvokder extends LinkInvoker {

		private Dao dao;

		private UpdateInvokder(Dao dao) {
			this.dao = dao;
		}

		void invoke(Link link, Object objSet) {
			Lang.each(objSet, new Each<Object>() {
				public void invoke(int i, Object obj, int length) throws ExitLoop, LoopException {
					dao.update(obj);
				}
			});
		}
	}

	/*-------------------------------------------------------------------*/
	public <T> T updateWith(final T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					update(obj);
					lns.invokeAll(new UpdateInvokder(dao));
				}
			});
		}
		return obj;
	}

	public <T> T updateLinks(T obj, String regex) {
		if (null != obj) {
			final Entity<?> entity = getEntity(obj.getClass());
			final Links lns = new Links(obj, entity, regex);
			final Dao dao = this;
			Trans.exec(new Atom() {
				public void run() {
					lns.invokeAll(new UpdateInvokder(dao));
				}
			});
		}
		return obj;
	}

}
