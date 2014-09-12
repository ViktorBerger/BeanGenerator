package hr.bergear;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

	public static void parse(String text) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;

		node = mapper.readTree(text);

		JsonNode properties = node.get("properties");
		System.out.println(node.get("name").asText());
		Iterator<String> props = properties.fieldNames();

		while (props.hasNext()) {
			String currentName = props.next();
			System.out.println(currentName.toString() + " "
					+ fetchPropertyData(properties, currentName));

		}
	}

	private static String fetchPropertyData(JsonNode properties,
			String propertyName) {
		return properties.get(propertyName).get("type").asText() + " "
				+ properties.get(propertyName).get("comment").asText();
	}

}
