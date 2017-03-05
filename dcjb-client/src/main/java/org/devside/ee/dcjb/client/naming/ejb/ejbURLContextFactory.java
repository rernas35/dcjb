package org.devside.ee.dcjb.client.naming.ejb;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.devside.ee.dcjb.client.DcjbNamingContext;
import org.devside.ee.dcjb.client.service.discovery.IServiceDiscoveryHandler;
import org.devside.ee.dcjb.client.service.discovery.consul.ConsulHandler;
import org.devside.ee.dcjb.client.service.model.Instance;

public class ejbURLContextFactory implements ObjectFactory{

	
	
	
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
			throws Exception {
		
		IServiceDiscoveryHandler sdHandler = new ConsulHandler("http://localhost:8500");
		Map<Instance,List<String>> serverServiceMap = sdHandler.getServerMap();
		return new DcjbNamingContext(serverServiceMap,environment);
	}
	
	
	

}
