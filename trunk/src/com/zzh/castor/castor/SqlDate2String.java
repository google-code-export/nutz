package com.zzh.castor.castor;

public class SqlDate2String extends DateTimeCastor<java.sql.Date, String> {

	@Override
	protected String cast(java.sql.Date src, Class<?> toType, String... args) {
		return dateFormat.format(new java.util.Date(src.getTime()));
	}

}
