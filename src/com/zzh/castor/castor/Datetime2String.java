package com.zzh.castor.castor;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {

	@Override
	protected String cast(java.util.Date src, Class<?> toType) {
		return dateTimeFormat.format(src);
	}

}
