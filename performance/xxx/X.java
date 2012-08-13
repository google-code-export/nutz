package xxx;

import java.util.List;

import org.nutz.resource.Scans;

public class X {

	public static void main(String[] args) {
		List<Class<?>> klasses = Scans.me().scanPackage("org.nutz.castor.castor");
		System.out.println(klasses.size());
		for (Class<?> class1 : klasses) {
			System.out.printf("        defaultCastorList.add(%s.class);\n", class1.getName());
		}
		
	}

}
