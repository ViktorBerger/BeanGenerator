package hr.bergear;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser implements Parser{
	
	private static JsonParser instance;
	private BeanInfo beanInfo;
	
	
	private JsonParser(){}
	public static JsonParser getInstance() {
		if(instance == null) {
			instance = new JsonParser();
		}
		return instance;
	}
	
	public void parse(String text) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = null;
		beanInfo = new BeanInfo();
		
		try {
			node = mapper.readTree(text);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
			throw new Exception("Illegal JSON format!" );
		}
		beanInfo.setName(node.get("name").asText());
		//beanInfo.setComment(node.get("comment").asText());
		
		
		JsonNode properties = node.get("properties");
		Iterator<String> propsIterator = properties.fieldNames();

		List<Property> list = new ArrayList<>();
		while (propsIterator.hasNext()) {
			String propertyName = propsIterator.next();
			String comment = properties.get(propertyName).get("comment").asText();
			String type = properties.get(propertyName).get("type").asText() ;
			
			list.add(Property.withComment(propertyName, type, comment));
		}
		
		beanInfo.setProperties(list);
		
	}

	@Override
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

}
