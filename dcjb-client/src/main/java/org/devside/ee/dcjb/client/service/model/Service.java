package org.devside.ee.dcjb.client.service.model;

public class Service {

	String ID;
	String service;
	String[] tags;
	String address;
	int port;
	boolean enableTagOverride;
	int createIndex;
	int modifyIndex;
	Instance instance;
	
	public String getID() {
		return ID;
	}
	public Service setID(String iD) {
		ID = iD;
		return this;
	}
	public String getService() {
		return service;
	}
	public Service setService(String service) {
		this.service = service;
		return this;
	}
	public String[] getTags() {
		return tags;
	}
	public Service setTags(String[] tags) {
		this.tags = tags;
		return this;
	}
	public String getAddress() {
		return address;
	}
	public Service setAddress(String address) {
		this.address = address;
		return this;
	}
	public int getPort() {
		return port;
	}
	public Service setPort(int port) {
		this.port = port;
		return this;
	}
	public boolean isEnableTagOverride() {
		return enableTagOverride;
	}
	public Service setEnableTagOverride(boolean enableTagOverride) {
		this.enableTagOverride = enableTagOverride;
		return this;
	}
	public int getCreateIndex() {
		return createIndex;
	}
	public Service setCreateIndex(int createIndex) {
		this.createIndex = createIndex;
		return this;
	}
	public int getModifyIndex() {
		return modifyIndex;
	}
	public Service setModifyIndex(int modifyIndex) {
		this.modifyIndex = modifyIndex;
		return this;
	}
	
	public Instance getInstance() {
		if (instance == null)
			instance = new Instance(address,port);
		return instance;
	}
	
	@Override
	public String toString() {
		return service + "[" + address + ":"+ port + "]";
	}
	
	
		
	
}
