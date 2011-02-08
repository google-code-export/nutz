package org.nutz.el.opt;

import org.nutz.el.ElOperator;

public abstract class AbstractOperator implements ElOperator {

	protected int weight;

	protected String str;

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setString(String str) {
		this.str = str;
	}

	public boolean isHigherThan(ElOperator opt) {
		return weight > opt.getWeight();
	}

	public String toString() {
		return str;
	}

}
