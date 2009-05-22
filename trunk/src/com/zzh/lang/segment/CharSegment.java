package com.zzh.lang.segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzh.lang.Lang;
import com.zzh.lang.stream.StringInputStream;

public class CharSegment implements Segment, Cloneable {

	public CharSegment() {}

	public CharSegment(String str) {
		valueOf(str);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Segment add(String key, Object v) {
		List<Integer> indexes = pps.get(key);
		if (null != indexes) {
			for (Iterator<Integer> it = indexes.iterator(); it.hasNext();) {
				int index = it.next().intValue();
				Object vs = values.get(index);
				if (vs instanceof List)
					((List<Object>) vs).add(v);
				else {
					List<Object> vl = new LinkedList<Object>();
					vl.add(v);
					values.set(index, vl);
				}
			}
		}
		return this;
	}

	@Override
	public void clearAll() {
		for (Iterator<List<Integer>> i = pps.values().iterator(); i.hasNext();)
			for (Iterator<Integer> ii = i.next().iterator(); ii.hasNext();)
				values.set(ii.next().intValue(), null);
	}

	@Override
	public boolean contains(String key) {
		return pps.containsKey(key);
	}

	@Override
	public Segment born() {
		return new CharSegment(this.toOrginalString());
	}

	private String orgString;

	@Override
	public String toOrginalString() {
		return orgString;
	}

	@Override
	public Segment clone() {
		Segment cs = this.born();
		for (Iterator<String> it = keys().iterator(); it.hasNext();) {
			String key = it.next();
			Object v = values.get(pps.get(key).get(0));
			internalSetValue((CharSegment) cs, key, v);
		}
		return cs;
	}

	@Override
	public List<Integer> getIndex(String key) {
		return this.ppIndexes.get(key);
	}

	@Override
	public Set<String> keys() {
		return this.pps.keySet();
	}

	@Override
	public List<Object> values() {
		return this.values;
	}

	private static Segment internalSetValue(CharSegment seg, String key, Object v) {
		List<Integer> indexes = seg.pps.get(key);
		if (null != indexes)
			for (Iterator<Integer> it = indexes.iterator(); it.hasNext();)
				seg.values.set(it.next().intValue(), v);
		return seg;
	}

	@Override
	public Segment setAll(Object v) {
		for (Iterator<String> it = keys().iterator(); it.hasNext();)
			internalSetValue(this, it.next(), v);
		return this;
	}

	@Override
	public Segment set(String key, boolean v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, int v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, double v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, float v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, long v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, byte v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, short v) {
		return internalSetValue(this, key, v);
	}

	@Override
	public Segment set(String key, Object v) {
		return internalSetValue(this, key, v);
	}

	private Map<String, List<Integer>> pps;
	private List<Object> values;
	private Map<String, List<Integer>> ppIndexes;

	@Override
	public void parse(InputStream ins) {
		pps = new HashMap<String, List<Integer>>();
		values = new ArrayList<Object>();
		ppIndexes = new HashMap<String, List<Integer>>();
		StringBuilder org = new StringBuilder();
		int IID = 0;
		try {
			int c;
			StringBuilder sb = new StringBuilder();
			boolean isInPP = false;
			boolean lastIsString = false;
			while ((c = ins.read()) != -1) {
				// store org
				org.append((char) c);
				if (isInPP && c == '}') { // In PlugPoint, and find }
					String key = sb.toString();
					values.add(null);
					lastIsString = false;
					List<Integer> indx = pps.get(key);
					List<Integer> ppIndx = ppIndexes.get(key);
					if (null == indx) {
						indx = new ArrayList<Integer>();
						pps.put(key, indx);
						ppIndx = new ArrayList<Integer>();
						ppIndexes.put(key, ppIndx);
					}
					indx.add(values.size() - 1);
					ppIndx.add(IID++);
					isInPP = false;
					sb = new StringBuilder();
				} else if (c == '$') { // Out of PlugPoint, and find $
					int cc = ins.read();
					org.append((char) cc);
					switch (cc) {
					case '$':
						sb.append((char) c);
						break;
					case '{':
						if (sb.length() > 0) {
							values.add(sb);
							sb = new StringBuilder();
							lastIsString = true;
						}
						isInPP = true;
						break;
					case -1:
						break;
					default:
						sb.append((char) c).append((char) cc);
					}
				} else { // Normal char, just append it
					sb.append((char) c);
				}
			}
			if (sb.length() > 0) {
				if (isInPP) {
					sb.insert(0, "${");
					if ((pps.size() == 0 && values.size() > 0) || lastIsString)
						((StringBuilder) values.get(0)).append(sb);
					else
						values.add(sb);
				} else
					values.add(sb);
			}
			orgString = org.toString();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Override
	public Segment valueOf(String str) {
		parse(new StringInputStream(str));
		return this;
	}

	@Override
	public CharSequence render() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it = values.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o == null)
				continue;
			if (o instanceof List) {
				for (Iterator<?> ii = ((List<?>) o).iterator(); ii.hasNext();) {
					sb.append(ii.next());
				}
			} else
				sb.append(o);
		}
		return sb;
	}

	@Override
	public String toString() {
		return render().toString();
	}

}
