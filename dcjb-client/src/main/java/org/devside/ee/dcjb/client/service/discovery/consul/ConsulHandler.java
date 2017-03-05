package org.devside.ee.dcjb.client.service.discovery.consul;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.http.impl.execchain.MainClientExec;
import org.devside.ee.dcjb.client.annotation.JumpNextOne;
import org.devside.ee.dcjb.client.service.discovery.IServiceDiscoveryHandler;
import org.devside.ee.dcjb.client.service.model.Instance;
import org.devside.ee.dcjb.client.service.model.Service;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class ConsulHandler implements IServiceDiscoveryHandler{
	
	String restURL;
	
	public ConsulHandler(String restURL) {
		this.restURL = restURL;
	}

	@Override
	public List<Service> getServices() {
		List<Service> serviceList = new LinkedList<Service>();
		try {
			 HttpResponse<JsonNode> asJson = Unirest.get(restURL +  "/v1/agent/services").asJson();
			System.out.println();
			asJson.getBody()
					.getObject()
					.keys()
					.forEachRemaining(t -> {
												System.out.println(asJson.getBody().getObject().getJSONObject(t));
												JSONObject srvJSON = asJson.getBody().getObject().getJSONObject(t);;
												Service service = new Service();
												service.setService(srvJSON.getString("Service"))
														.setAddress(srvJSON.getString("Address"))
														.setID(srvJSON.getString("ID"))
														.setPort(srvJSON.getInt("Port"));
												System.out.println(service);
												serviceList.add(service);
												
											} );
		}catch(UnirestException urException){
			urException.printStackTrace();
		}
		
		
		
		return serviceList;
	}
	
	public Map<Instance,List<String>> getServerMap(){
		List<Service> serviceList = getServices();
		Map<Instance,List<String>> serverMap = new HashMap<Instance,List<String>>(); 
		for (Service service : serviceList) {
			List<String> sList = serverMap.get(service.getInstance());
			if (sList == null){
				sList = new LinkedList<String>();
				serverMap.put(service.getInstance(), sList);
			}
			sList.add(service.getService());
		}
		return serverMap;
	}
	
	public static void main(String[] args) {
		new ConsulHandler("http://localhost:8500").getServices();
	}

}
