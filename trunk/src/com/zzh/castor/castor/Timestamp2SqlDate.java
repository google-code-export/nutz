package com.zzh.castor.castor;

import java.sql.Date;
import java.sql.Timestamp;

import com.zzh.castor.Castor;
import com.zzh.castor.FailToCastObjectException;

public class Timestamp2SqlDate extends Castor<Timestamp, Date> {

	@Override
	protected Date cast(Timestamp src, Class<?> toType, String... args)
			throws FailToCastObjectException {
		return new Date(src.getTime());
	}

}
