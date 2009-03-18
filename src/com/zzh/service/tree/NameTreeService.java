package com.zzh.service.tree;

import com.zzh.dao.Dao;

public class NameTreeService<T> extends TreeService<T> {

	protected NameTreeService() {
		super();
	}

	protected NameTreeService(Dao dao) {
		super(dao);
	}

	public void delete(String name) {
		dao().delete(getEntityClass(), name);
	}

	public T fetch(String name) {
		return dao().fetch(getEntityClass(), name);
	}

}
