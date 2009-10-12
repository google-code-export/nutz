package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface FieldType {
	public static enum ENUM {
		AUTO, INT, CHAR
	}

	ENUM value() default ENUM.AUTO;
}
