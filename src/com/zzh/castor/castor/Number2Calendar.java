package com.zzh.castor.castor;

import java.util.Calendar;

import com.zzh.castor.Castor;

public class Number2Calendar extends Castor<Number, Calendar> {

	@Override
	protected Calendar cast(Number src, Class<?> toType, String... args) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(src.longValue());
		return c;
	}

}
