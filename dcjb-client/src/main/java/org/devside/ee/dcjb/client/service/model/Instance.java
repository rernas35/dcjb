package org.devside.ee.dcjb.client.service.model;

import java.util.ArrayList;
import java.util.Collections;

public class Instance implements Comparable<Instance> {

	String address;
	
	int port;
	
	int failureCount=0;
	
	public Instance(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		return address + ":" + port;
	}

	@Override
	public int compareTo(Instance o) {
		return failureCount - o.failureCount;
	}
	
	public static void main(String[] args) {
		Instance i1 = new Instance("1", 7079);
		i1.failureCount=1;
		Instance i2 = new Instance("2",8080);
		i2.failureCount=3;
		java.util.List<Instance> list = new ArrayList<Instance>();
		list.add(i1);
		list.add(i2);
		list.forEach(t -> {
			System.out.println(t);
		});
		Collections.sort(list);
		list.forEach(t -> {
			System.out.println(t);
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		Instance instance = (Instance)obj;
		if  (!(instance instanceof Instance)){
			return false;
		}
		return address.equals(instance.getAddress()) && port == instance.getPort();
	}
	
}
