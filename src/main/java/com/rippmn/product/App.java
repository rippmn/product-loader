package com.rippmn.product;

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
				System.out.println(token.name());
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

			if (JsonToken.START_ARRAY.equals(token)) {
				//System.out.println(parser.getCurrentName());
				while (!JsonToken.END_ARRAY.equals(token)) {
					token = parser.nextToken();
				}
			} else if (token.name().startsWith("VALUE")) {
				// System.out.println(token.name() + "-" + parser.getCurrentName() + "-" +
				if (parser.getCurrentName().equals("sku")) {
					product.setSku(parser.getText());
				} else if (parser.getCurrentName().equals("name")) {
					product.setName(parser.getText());
				}

			}

			token = parser.nextToken();
		}

		return product;
	}
}
