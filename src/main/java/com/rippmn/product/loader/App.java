package com.rippmn.product.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * load products json and create product objects
 *
 */
public class App {
	public static void main(String[] args) throws JsonParseException, MalformedURLException, IOException {

		URL url = new URL("file:///home/rippmn/Downloads/products-batteries.json");
		//URL url = new URL("file:///home/rippmn/Downloads/products.json");
		//URL url = new URL("https://raw.githubusercontent.com/BestBuyAPIs/open-data-set/master/products.json");

		// get an instance of the json parser from the json factory
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(url);

		JsonToken token;
		int products = 0;

		while (!parser.isClosed()) {
			token = parser.nextToken();

			// if its the last token then we are done
			if (token == null)
				break;

			if (JsonToken.START_ARRAY.equals(token)) {
				// for now we are skipping nested object arrays
				continue;
			} else if (JsonToken.START_OBJECT.equals(token)) {
				// create the product
				products++;
				System.out.println(createProduct(parser));
			}

		}

		System.out.println(products);

	}

	private static Product createProduct(JsonParser parser) throws IOException {

		JsonToken token = parser.nextToken();

		Product product = new Product();

		// now loop thru tokens to find product info
		while (!JsonToken.END_OBJECT.equals(token)) {

			if (JsonToken.START_ARRAY.equals(token) && parser.getCurrentName().equals("category") ) {
				Category category = null;
				while (!JsonToken.END_ARRAY.equals(token)) {
					
					if(JsonToken.START_OBJECT.equals(token)) {
						category = new Category();
						product.getCategories().add(category);
					}
					
					if (token.name().startsWith("VALUE")) {
						String text = parser.getText();
						switch (parser.getCurrentName()) {
						case "id":
							category.setId(text);
							break;
						case "name":
							category.setName(text);
						default:
							break;
						}
					}
					token = parser.nextToken();
				}
			} else if (token.name().startsWith("VALUE")) {
				String text = parser.getText();
				
				switch (parser.getCurrentName()) {
				case "sku":
					product.setSku(text);
					break;
				case "name":
					product.setName(text);
					break;
				case "price":
					product.setPrice(parser.getDoubleValue());
					break;
				case "shipping":
					if(text != null && text.length() >0)
						product.setShipping(parser.getDoubleValue());
					break;
				case "upc":
					product.setUpc(text);
					break;
				case "description":
					product.setDescription(text);
					break;
				case "manufacturer":
					product.setManufacturer(text);
					break;
				case "model":
					product.setModel(text);
					break;
				case "url":
					product.setUrl(text);
					break;
				case "image":
					product.setImage(text);
					break;
				case "type":
					product.setType(text);
					break;
				default:
					break;
				}
				
			}

			token = parser.nextToken();
		}
				
		return product;
	}
}
