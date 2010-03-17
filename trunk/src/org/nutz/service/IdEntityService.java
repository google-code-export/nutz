package org.nutz.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.EntityField;

public abstract class IdEntityService<T> extends EntityService<T> {

	protected IdEntityService() {
		super();
	}

	protected IdEntityService(Dao dao) {
		super(dao);
	}

	public T fetch(long id) {
		return dao().fetch(getEntityClass(), id);
	}

	public void delete(long id) {
		dao().delete(getEntityClass(), id);
	}

	public int getMaxId() {
		return dao().getMaxId(getEntityClass());
	}

	public boolean exists(long id) {
		EntityField ef = getEntity().getIdField();
		if (null == ef)
			return false;
		return dao().count(getEntityClass(), Cnd.where(ef.getFieldName(), "=", id)) > 0;
	}

}