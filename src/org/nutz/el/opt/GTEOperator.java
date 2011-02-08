package org.nutz.el.opt;

import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.el.ann.Opt;
import org.nutz.el.ann.Weight;
import org.nutz.lang.util.Context;

@Opt(">=")
@Weight(5)
public class GTEOperator extends AbstractOperator {

	public ElValue execute(Context context, ElObj left, ElObj right) {
		ElValue l = left.eval(context);
		ElValue r = right.eval(context);
		return l.isGTE(r);
	}

}
