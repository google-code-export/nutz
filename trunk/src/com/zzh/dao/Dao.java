package com.zzh.dao;

import java.sql.ResultSet;
import java.util.List;

import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.entity.Entity;

public interface Dao {

	SqlManager sqls();

	SqlMaker maker();

	void execute(Sql<?>... sqls);

	void execute(ConnCallback callback);

	void executeBySqlKey(String... keys);

	<T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm);

	<T> T insert(T obj);

	<T> T insertMany(T obj, String... fieldNames);

	<T> T insertOne(T obj, String... fieldNames);

	<T> T insertManyMany(T obj, String... fieldNames);

	<T> T update(T obj);

	<T> T update(T obj, boolean ignoreNull);

	<T> List<T> query(Class<T> classOfT, Condition condition, Pager pager);

	<T> void delete(T obj);

	<T> void delete(Class<T> classOfT, long id);

	<T> void delete(Class<T> classOfT, String name);

	<T> void deleteOne(T obj, String... fieldNames);

	<T> T fetch(Class<T> classOfT, long id);

	<T> T fetch(Class<T> classOfT, String name);

	<T> T fetch(Class<T> classOfT, Condition condition);

	<T> T fetchOne(T obj, String... fieldNames);

	<T> T fetchMany(T obj, String... fieldNames);

	<T> T fetchManyMany(T obj, String... fieldNames);

	<T> void clear(Class<T> classOfT, Condition condition);

	void clear(String tableName, Condition condition);

	<T> void clearMany(T obj, String... fieldNames);

	<T> void clearManyMany(T obj, String... fieldNames);

	<T> Entity<T> getEntity(Class<T> classOfT);

	int count(Class<?> classOfT, Condition condition);

	int count(Class<?> classOfT);

	int count(String tableName, Condition condition);

	int count(String tableName);

	int getMaxId(Class<?> classOfT);

	Pager createPager(int pageNumber, int pageSize);

}
