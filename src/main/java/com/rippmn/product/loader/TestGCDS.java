package com.rippmn.product.loader;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;

public class TestGCDS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello GCDS");
		
		ArrayList<ParsedProduct> prods = new ArrayList<ParsedProduct>();
		
		
		for(int i = 0; i < 10; i++) {
			prods.add(new ParsedProduct());
			prods.get(i).setSku(Integer.toString(i));
			prods.get(i).setName("test "+i);
					
		}
		
		
		persistProducts(prods);
		System.out.println("successful batch insert");
	    
		prods = new ArrayList<ParsedProduct>();
		
		prods.add(new ParsedProduct());
		prods.get(0).setSku(Integer.toString(11));
		prods.get(0).setName("test 11");
		
	    
	    persistProducts(prods);
	    
	    System.out.println("successful insert");
	    
	}
	
	public static void persistProducts(List<ParsedProduct> products) {
		
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	    
		ArrayList<FullEntity<IncompleteKey>> entities = new ArrayList<FullEntity<IncompleteKey>>();
		
		String kind = "test";
	    
		IncompleteKey testKey = null; 		
		for(ParsedProduct prod: products) {
			
			//TODO convert this to key based upon sku (avoids duplicates)
			testKey = datastore.newKeyFactory().setKind(kind).newKey();
		    
		    FullEntity<IncompleteKey> productEntity = Entity.newBuilder(testKey)
		            .set("sku", prod.getSku())
		            .set("name", prod.getName())
		            .build();
		    
		    entities.add(productEntity);
			
		}
		
		if(entities.size()<10) {
			for(FullEntity<IncompleteKey> entity:entities) {
				datastore.put(entity);
			}
		}else {
			datastore.put(entities.get(0), entities.get(1), entities.get(2),
				entities.get(3),entities.get(4),entities.get(5),entities.get(6),entities.get(7),entities.get(8),entities.get(9));
		}
	}

}
