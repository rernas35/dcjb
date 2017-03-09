package org.devside.ee.dcjb.client;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.naming.Context;

import org.devside.ee.dcjb.client.error.NoAvailableEJBNamingContextForGivenService;
import org.devside.ee.dcjb.client.error.NodeWithTransactionFailureException;
import org.devside.ee.dcjb.client.service.model.Instance;

public class DCJBClientHandler {
	
	public static class DCJBCall{
		
		Instance instance;
		String serviceName;
		boolean transactional;
		
		public Instance getInstance() {
			return instance;
		}
		
		public void setInstance(Instance instance) {
			this.instance = instance;
		}
		
		public String getServiceName() {
			return serviceName;
		}
		
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		
		public void setTransactional(boolean transactional) {
			this.transactional = transactional;
		}
		
		public boolean isTransactional() {
			return transactional;
		}
		
	}
	
	public static class DCJBContext {
		
		List<DCJBCall> callList = new LinkedList<DCJBCall>();
		DCJBCall latest;
		
		public void add(DCJBCall call){
			callList.add(call);
		}
		
		public DCJBCall getLatest(){
			return latest;
		}
		
	}
	
	static ThreadLocal<DCJBContext> context;
	
	public static Context getEJBContext(String serviceName) {
		List<Context> contextList = DcjbNamingContext.contextMap.get(serviceName);
		if (contextList.isEmpty())
			throw new NoAvailableEJBNamingContextForGivenService();
		return contextList.get(0);
	}
	
	public static void handleError(){
		DCJBContext dcjbContext = context.get();
		dcjbContext.callList.forEach( c -> {
			if (dcjbContext.getLatest().getInstance().equals(c.getInstance()) 
					&& c.isTransactional()){
				throw new NodeWithTransactionFailureException();
			}
		});
		
		dcjbContext.callList.remove(dcjbContext.getLatest());
	}
	

}
