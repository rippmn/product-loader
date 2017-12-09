package com.rippmn.product.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
		// URL url = new URL("file:///home/rippmn/Downloads/products.json");
		// URL url = new
		// URL("https://raw.githubusercontent.com/BestBuyAPIs/open-data-set/master/products.json");

		// get an instance of the json parser from the json factory
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(url);

		JsonToken token;
		int products = 0;

		ArrayList<ParsedProduct> parsedProds = new ArrayList<ParsedProduct>();

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
				parsedProds.add(createProduct(parser));
				System.out.println(parsedProds.get(parsedProds.size() - 1));
			}

		}

		System.out.println(products);

		// StringBuilder sb = new StringBuilder();

		// sb.append("{");

		// Iterator<ParsedProduct> prodIter = parsedProds.iterator();

		for (ParsedProduct p : parsedProds) {

			StringTokenizer st = new StringTokenizer(p.getName());

			String lastToken = null;
			String phrase = null;
			while (st.hasMoreTokens()) {

				if (phrase == null) {
					phrase = st.nextToken();
					lastToken = st.nextToken();

				}

				while (lastToken.length() == 1) {
					System.out.println("****" + lastToken.charAt(0));
					if (!Character.isLetterOrDigit(lastToken.charAt(0))) {
						lastToken = st.nextToken();
						System.out.println("****" + lastToken);
					} else {
						break;
					}
				}

				System.out.println(phrase.concat(" ").concat(lastToken));
				phrase = lastToken;
				if (st.hasMoreTokens()) {
					lastToken = st.nextToken();
				}
			}

			if (!phrase.contains(lastToken)) {
				System.out.println(phrase.concat(" ").concat(lastToken));
				System.out.println(lastToken);
			}else if (phrase.equals(lastToken)) {
				System.out.println(lastToken);
			}
		}

		// while(prodIter.hasNext()) {
		// ParsedProduct prod = prodIter.next();
		// sb.append("{\"").append(prod.getSku()).append("\",\"").append(prod.getName()).append("\"}");
		// if(prodIter.hasNext()) {
		// sb.append(",");
		// }
		// }
		//
		// sb.append("}");
		//
		// System.out.println(sb);

		// ArrayList<String> strings = new ArrayList<String>();
		//
		// for(int i=1;i<10;i++) {
		// strings.add(Integer.toString(i));
		// }
		//
		// varargs(strings.get(0), strings.get(1), strings.subList(2,
		// strings.size()).toArray(new String[strings.size()-2]));

	}

	private static ParsedProduct createProduct(JsonParser parser) throws IOException {

		JsonToken token = parser.nextToken();

		ParsedProduct parsedProduct = new ParsedProduct();

		// now loop thru tokens to find product info
		while (!JsonToken.END_OBJECT.equals(token)) {

			if (JsonToken.START_ARRAY.equals(token) && parser.getCurrentName().equals("category")) {
				Category category = null;
				while (!JsonToken.END_ARRAY.equals(token)) {

					if (JsonToken.START_OBJECT.equals(token)) {
						category = new Category();
						parsedProduct.getCategories().add(category);
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
					parsedProduct.setSku(text);
					break;
				case "name":
					parsedProduct.setName(text);
					break;
				case "price":
					parsedProduct.setPrice(parser.getDoubleValue());
					break;
				case "shipping":
					if (text != null && text.length() > 0)
						parsedProduct.setShipping(parser.getDoubleValue());
					break;
				case "upc":
					parsedProduct.setUpc(text);
					break;
				case "description":
					parsedProduct.setDescription(text);
					break;
				case "manufacturer":
					parsedProduct.setManufacturer(text);
					break;
				case "model":
					parsedProduct.setModel(text);
					break;
				case "url":
					parsedProduct.setUrl(text);
					break;
				case "image":
					parsedProduct.setImage(text);
					break;
				case "type":
					parsedProduct.setType(text);
					break;
				default:
					break;
				}

			}

			token = parser.nextToken();
		}

		return parsedProduct;
	}

	private static void varargs(String s1, String s2, String... strings) {
		for (String s : strings) {
			System.err.println(s);
		}
	}
}
