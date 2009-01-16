package com.zzh.lang.types.castor;

import com.zzh.lang.meta.Email;
import com.zzh.lang.types.Castor;

public class String2Email extends Castor<String,Email> {

	@Override
	protected Object cast(Object src) {
		return new Email(src.toString());
	}

}
