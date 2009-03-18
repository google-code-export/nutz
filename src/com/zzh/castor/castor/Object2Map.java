package com.zzh.castor.castor;

import java.util.Map;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;
import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public class Object2Map extends Castor<Object, Map> {

	@Override
	protected Map cast(Object src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		StringBuilder sb = new StringBuilder(Json.toJson(src));
		Map map = (Map) Json.fromJson(Lang.inr(sb));
		return map;
	}

}
