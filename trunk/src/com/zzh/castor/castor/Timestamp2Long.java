package com.zzh.castor.castor;

import java.sql.Timestamp;

import com.zzh.castor.Castor;

public class Timestamp2Long extends Castor<Timestamp, Long> {

	@Override
	protected Long cast(Timestamp src, Class<?> toType, String... args) {
		return src.getTime();
	}

}
