package org.nutz.dao.entity;

import java.lang.reflect.Field;

import org.nutz.dao.TableName;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.CharSegment;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author Bird.Wyatt(bird.wyatt@gmail.com)
 * 
 */
public class Link {

	public static Link getLinkForOne(Mirror<?> mirror, Field field,
			Class<?> targetClass, String fieldName) throws NoSuchFieldException {
		Link link = new Link(field, targetClass); 
		link.type = LinkType.One;
		link.referField = mirror.getField(fieldName);
		if (Mirror.me(link.referField.getType()).isStringLike()) {
			link.targetField = Mirror.me(link.targetClass).getField(Name.class);
		} else {
			link.targetField = Mirror.me(link.targetClass).getField(Id.class);
		}
		return link;

	}
	private Link(Field field, Class<?> targetClass) throws NoSuchFieldException {
		this.ownField = field;
		this.targetClass = targetClass;
	}

	public static Link getLinkForMany(Mirror<?> mirror, Field field,
			Class<?> targetClass, String fieldName, String key)
			throws NoSuchFieldException {
		Link link = new Link(field, targetClass);
		link.type = LinkType.Many;
		link.mapKeyField = "".equals(key) ? null : key;
		if (!"".equals(fieldName)) {
			link.targetField = Mirror.me(link.targetClass).getField(fieldName);
			if (Mirror.me(link.targetField.getType()).isStringLike()) {
				link.referField = mirror.getField(Name.class);
			} else {
				link.referField = mirror.getField(Id.class);
			}
		}
		return link;

	}

	public static Link getLinkForManyMany(Mirror<?> mirror, Field field,
			Class<?> targetClass, String key, String from, String to,
			String relation, boolean fromName, boolean toName)
			throws NoSuchFieldException {
		Link link = new Link(field, targetClass);
		link.type = LinkType.ManyMany;
		link.mapKeyField = "".equals(key) ? null : key;
		link.from = from;
		link.to = to;
		link.relation = Relation.make(relation);
		link.referField = lookupKeyField(mirror, fromName);
		link.targetField = lookupKeyField(Mirror.me(targetClass), toName);
		if (null == link.referField || null == link.targetField) {
			throw Lang.makeThrow(
					"Fail to make ManyMany link for [%s].[%s], target: [%s]."
							+ "\n referField: [%s]" + "\n targetField: [%s]",
					mirror.getType().getName(), field.getName(), targetClass
							.getName(), link.referField, link.targetField);
		}
		return link;

	}

	private static Field lookupKeyField(Mirror<?> mirror, boolean forName) {
		if (forName)
			for (Field f : mirror.getFields()) {
				if (null != f.getAnnotation(Name.class))
					return f;
			}
		for (Field f : mirror.getFields()) {
			if (null != f.getAnnotation(Id.class))
				return f;
		}
		return null;
	}

	// private void evalMore(String dynamicBy) {
	// Cascade cc =
	// this.targetClass.getAnnotation(Cascade.class);
	// if (null != cc && cc.value() == Cascade.TYPE.ON)
	// cascade = true;
	// Table annTab =
	// this.targetClass.getAnnotation(Table.class);
	// if (null != annTab && (new
	// CharSegment(annTab.value())).keys().size() > 0)
	// this.targetDynamic = true;
	// dynamicReferField =
	// DynamicReferPicker.eval(targetClass, dynamicBy);
	// }

	private Class<?> targetClass;
	private Field targetField;
	private Field referField;
	private Field ownField;
	private LinkType type;
	private Object relation;
	private String from;
	private String to;
	private String mapKeyField;

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public String getMapKeyField() {
		return mapKeyField;
	}

	public Field getTargetField() {
		return targetField;
	}

	public Field getReferField() {
		return referField;
	}

	public boolean isMany() {
		return type == LinkType.Many;
	}

	public boolean isOne() {
		return type == LinkType.One;
	}

	public boolean isManyMany() {
		return type == LinkType.ManyMany;
	}

	public Field getOwnField() {
		return ownField;
	}

	public static class Relation {

		static Object make(String s) {
			CharSegment cs = new CharSegment(s);
			if (cs.keys().size() == 0)
				return s;
			Relation r = new Relation();
			r.cs = cs;
			return r;
		}

		CharSegment cs;

		@Override
		public String toString() {
			return TableName.render(cs);
		}

		public String getOrginalString() {
			return cs.getOrginalString();
		}

	}

	public String getRelation() {
		return relation.toString();
	}

	public String getRelationOrignalString() {
		if (relation instanceof Relation)
			return ((Relation) relation).getOrginalString();
		return relation.toString();
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
