package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.EntityService;

public class Insert<T> extends EntityControllor<T> {

	public Insert(EntityService<T> service) {
		super(service);
	}

	public String[] ones;

	public String[] manys;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		T obj = this.getObject(request);
		service.insert(obj);
		service.dao().insertOne(obj, ones);
		service.dao().insertMany(obj, manys);
		return obj;
	}

}
