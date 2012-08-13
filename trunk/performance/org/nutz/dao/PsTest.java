package org.nutz.dao;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Maths;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

/**
 *
 * @author wendal
 */
public class PsTest {

    @Test
    public void testIssue277() throws Throwable {
        Context context = Lang.context();
        context.set("math", Maths.class);
        assertEquals(2, El.eval(context, "math.max(1, 2)"));
    }

}
