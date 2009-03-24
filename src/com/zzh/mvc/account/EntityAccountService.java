package com.zzh.mvc.account;

import com.zzh.dao.Dao;
import com.zzh.lang.Strings;
import com.zzh.service.EntityService;

public class EntityAccountService<T extends Account> extends EntityService<T> implements
		AccountService<T> {

	public EntityAccountService() {
		super();
	}

	public EntityAccountService(Dao dao) {
		super(dao);
	}

	@Override
	public Class<T> getAccountType() {
		return this.getEntityClass();
	}

	@Override
	public T verify(T account) {
		T dba = dao().fetch(getAccountType(), account.getName());
		if (null != dba)
			if (Strings.equalsIgnoreCase(account.getPassword(), dba.getPassword())) {
				return dba;
			}
		return null;
	}

}
