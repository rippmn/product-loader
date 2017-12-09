package com.rippmn.product.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.KeyFactory;

public class TestGCDS {

	public static void main(String[] args) {
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
	    
		String kind = "taggedprod";
	    
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for(ParsedProduct prod: products) {
			
			Entity.Builder builder = Entity.newBuilder(keyFactory.newKey(prod.getSku()));
			
		    builder.set("name", prod.getName());
		    
		    //parse the product name into two word phrases
		    addSearchPhrases(builder, prod.getName());
		            
		    entities.add(builder.build());
			
		}
		
		datastore.put(entities.toArray(new Entity[entities.size()]));
		
	}
	
	private static void addSearchPhrases(Builder builder, String productName) {
		
		
		StringTokenizer st = new StringTokenizer(productName.toLowerCase());
		
		ArrayList<String> phrases = new ArrayList<String>();
		
		phrases.subList(0, 0);
		
		String lastToken = null;
		String phrase = null;
		while(st.hasMoreTokens()) {
			
			if(phrase == null) {
				phrase = st.nextToken();
				lastToken = st.nextToken();
				
			}
			
			while(lastToken.length() == 1) {
				if(!Character.isLetterOrDigit(lastToken.charAt(0))) {
					lastToken = st.nextToken();
				} else {
					break;
				}
			}
			
			phrases.add(phrase.concat(" ").concat(lastToken));
			phrase = lastToken;
			if (st.hasMoreTokens()) {
				lastToken = st.nextToken();
			}
		}
		
		if(!phrase.contains(lastToken)) {
			phrases.add(phrase.concat(" ").concat(lastToken));
			phrases.add(lastToken);
		}else if (phrase.equals(lastToken)) {
			phrases.add(lastToken);
		}
		
		if(phrases.size() == 1) {
			builder.set("tag", phrases.get(0));
		}else if(phrases.size() == 2) {
			builder.set("tag", phrases.get(0), phrases.get(1));
		}else if(phrases.size() > 2){
			builder.set("tag", phrases.get(0), phrases.get(1), phrases.subList(2, phrases.size()).toArray(new String[phrases.size()-2]));
		}
		
	}

}
