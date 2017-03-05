package org.devside.ee.dcjb.client.service.discovery;

import java.util.List;
import java.util.Map;

import org.devside.ee.dcjb.client.service.model.Instance;
import org.devside.ee.dcjb.client.service.model.Service;

public interface IServiceDiscoveryHandler {
	
	List<Service> getServices();
	
	Map<Instance,List<String>> getServerMap();
	

}
