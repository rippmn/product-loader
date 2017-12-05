package com.rippmn.product_parser;

public class Product {
	private String sku;
	private String name;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Product [sku=" + sku + ", name=" + name + "]";
	}
	
	
	
}
