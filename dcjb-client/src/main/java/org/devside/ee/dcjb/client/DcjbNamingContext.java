package org.devside.ee.dcjb.client;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.devside.ee.dcjb.client.service.model.Instance;

import com.sun.naming.internal.ResourceManager;

public class DcjbNamingContext implements Context{
	
	public static final String IMPL = ".impl";

//	private static final Logger LOG = LoggerFactory.getLogger(DcjbNamingContext.class);
	ConcurrentHashMap<String, List<Context>> contextMap = new ConcurrentHashMap<String, List<Context>>(); 
	
	
	public DcjbNamingContext(Map<Instance,List<String>> serverServiceMap,Hashtable environment) {
		String scheme = "ejb";
		String defaultPkgPrefix = "com.sun.jndi.url";
		
		for (Instance server : serverServiceMap.keySet()) {
			Hashtable clonedMap = (Hashtable) environment.clone();
			
			String host = server.getAddress();
			String port = String.valueOf(server.getPort());
			

			clonedMap.put(PROVIDER_URL, "http-remoting://" + host + ":" + port);
			clonedMap.put("remote.connection.default.host", host);
			clonedMap.put("remote.connection.default.port", port);
			clonedMap.put(URL_PKG_PREFIXES, environment.get(URL_PKG_PREFIXES + IMPL));
			
			ObjectFactory factory = null;
			try {
				factory = (ObjectFactory)ResourceManager.getFactory(
				        Context.URL_PKG_PREFIXES, clonedMap, null,
				        "." + scheme + "." + scheme + "URLContextFactory", defaultPkgPrefix);
				Context context = (Context)factory.getObjectInstance(null, null, null, clonedMap);
				List<String> services = serverServiceMap.get(server);
				for (String service : services) {
					List<Context> list = contextMap.get(service);
					if (list == null){
						list = new LinkedList<Context>();
						contextMap.put(service, list);
					}
					list.add(context);
				}
				
				
				
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		
//		Object resolvedproxy = remotingContext.lookup(ejbUrl);
//		return (T) resolvedproxy;
		
		
		
	}
	
	@Override
	public Object lookup(Name name) throws NamingException {
//		LOG.debug("lookup for " + name);
		Context context = contextMap.get(name).get(0);
		return context != null ? context.lookup(name) : null;
	}

	@Override
	public Object lookup(String name) throws NamingException {
//		LOG.debug("lookup for " + name);
		Context context = contextMap.get(name).get(0);
		return context != null ? context.lookup(name) : null;
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbind(Name name) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbind(String name) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	    

}
