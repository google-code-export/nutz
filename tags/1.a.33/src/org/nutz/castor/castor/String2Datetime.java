package org.nutz.castor.castor;

import java.text.ParseException;

import org.nutz.lang.Lang;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

	@Override
	public java.util.Date cast(String src, Class<?> toType, String... args) {
		try {
			return this.dateTimeFormat.parse(src);
		}
		catch (ParseException e1) {
			try {
				return dateFormat.parse(src);
			}
			catch (ParseException e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
