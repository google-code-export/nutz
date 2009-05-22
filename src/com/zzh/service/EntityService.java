package com.zzh.service;

import java.util.List;

import com.zzh.dao.Chain;
import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.entity.Entity;
import com.zzh.lang.Mirror;
import com.zzh.dao.Pager;

public abstract class EntityService<T> extends Service {

	private Mirror<T> mirror;

	@SuppressWarnings("unchecked")
	protected EntityService() {
		try {
			mirror = Mirror.me((Class<T>) Mirror.getTypeParams(getClass())[0]);
		} catch (Exception e) {}
	}

	protected EntityService(Dao dao) {
		this();
		this.setDao(dao);
	}

	public Mirror<T> mirror() {
		return mirror;
	}

	@SuppressWarnings("unchecked")
	public <C extends T> void setEntityType(Class<C> classOfT) {
		mirror = (Mirror<T>) Mirror.me(classOfT);
	}

	public Entity<T> getEntity() {
		return dao().getEntity(mirror.getType());
	}

	public Class<T> getEntityClass() {
		return mirror.getType();
	}

	public void clear(Condition condition) {
		dao().clear(getEntityClass(), condition);
	}

	public void clear() {
		dao().clear(getEntityClass(), null);
	}

	public List<T> query(Condition condition, Pager pager) {
		return (List<T>) dao().query(getEntityClass(), condition, pager);
	}

	public int count(Condition condition) {
		return dao().count(getEntityClass(), condition);
	}

	public int count() {
		return dao().count(getEntityClass());
	}

	public T fetch(Condition condition) {
		return dao().fetch(getEntityClass(), condition);
	}

	public void update(Chain chain, Condition condition) {
		dao().update(getEntityClass(), chain, condition);
	}

	public void updateRelation(String regex, Chain chain, Condition condition) {
		dao().updateRelation(getEntityClass(), regex, chain, condition);
	}
}
