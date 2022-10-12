package wasdev.ejb.sample;

import javax.naming.InitialContext;
import java.util.Hashtable;

public class SampleLibClass {
	public String doSomething() throws Exception {
		Hashtable ht = new Hashtable();

		ht.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
		ht.put("java.naming.provider.url", "corbaloc:iiop:localhost:2809");

		InitialContext ctx = new InitialContext(ht);

		return "";
	}
}
