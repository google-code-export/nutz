package com.zzh.castor.castor;

import java.util.Collection;

import com.zzh.castor.Castor;
import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;

@SuppressWarnings("unchecked")
public class Collection2Object extends Castor<Collection, Object> {

	@Override
	protected Object cast(Collection src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		if (src.size() == 0)
			return null;
		return Castors.me().castTo(src.iterator().next(), toType);
	}

}
