package com.zzh.castor;

import java.text.SimpleDateFormat;

import com.zzh.castor.castor.DateTimeCastor;

public class CastorSetting {

	public static void setup(DateTimeCastor<?, ?> c) {
		c.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		c.setTimeFormat(new SimpleDateFormat("HH:mm:ss"));
		c.setDateTimeFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

}
