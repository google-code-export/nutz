package com.zzh.castor.castor;

import java.lang.reflect.Array;
import java.util.Collection;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Array2Collection extends Castor<Object, Collection> {

	public Array2Collection() {
		this.fromClass = Array.class;
		this.toClass = Collection.class;
	}

	@Override
	protected Collection cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		Collection coll = createCollection(src, toType);
		for (int i = 0; i < Array.getLength(src); i++)
			coll.add(Array.get(src, i));
		return (Collection<?>) coll;

	}
}
