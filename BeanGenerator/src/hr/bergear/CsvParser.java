package hr.bergear;

import java.util.ArrayList;
import java.util.List;

/**
 * Input parser. Extracts bean name and it's properties. File format: property
 * BeanName
 * propertyName1 (delimiter) propertyType1 (delimiter) comment1
 * propertyName2 (delimiter) propertyType2 (delimiter) comment2
 * ...
 * @author Viktor
 *
 */
public class CsvParser implements Parser{
	
	private static CsvParser instance;

	private BeanInfo beanInfo;
	private List<Property> props;
	private boolean justName;
	private String delimiter;
	
	public static CsvParser getInstance() {
		if(instance == null) {
			instance = new CsvParser();
		}
		return instance;
	}

	private  CsvParser() {
		this.delimiter = "\\s+";
		props = new ArrayList<>();
	}

	// TODO parsing exception
	
	/**
	 * Main parsing method.
	 * 
	 * @param input
	 *            textual input
	 */
	public void parse(String input) {
		beanInfo = new BeanInfo();
		props.clear();
		justName = false;

		String[] ps = input.split("\n");

		if (ps.length == 1) {
			justName = true;
		} else {
			justName = false;
		}

		if (ps.length < 2) {
			return;
		}

		
		String[] firstLine = ps[0].split(delimiter); 
		int fllength = firstLine.length;
		
		if (fllength == 1) {
			beanInfo.setName(firstLine[0]);
		} else if (fllength == 2) {
			beanInfo.setName(firstLine[0]);
			beanInfo.setComment(firstLine[1]);
		}
		
		for (int i = 1; i < ps.length; i++) {
			String trimmed = ps[i].trim();

			if (!trimmed.isEmpty()) {
				props.add(createProperty(trimmed.split(delimiter)));
			}
		}
		
		beanInfo.setProperties(props);
		// TODO parse imports
		beanInfo.setImports(new ArrayList<String>());
		
	}

	/**
	 * Creates properties based on provided attributes.
	 * 
	 * @param attributes
	 *            attributes of the property
	 * @return new property
	 */
	private Property createProperty(String[] attributes) {
		if (attributes.length == 1) {
			return Property.withoutComment(attributes[0].trim(), "String");
		}
		if (attributes.length == 2) {
			return Property.withoutComment(attributes[0].trim(), attributes[1].trim());
		}
		String comment = "";
		for (int i = 2; i < attributes.length; i++) {
			comment += attributes[i].trim() + " ";
		}

		return Property.withComment(attributes[0].trim(), attributes[1].trim(), comment);
	}	
	
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

	/**
	 * Returns bean properties
	 * 
	 * @return bean properties
	 */
	public List<Property> getProperties() {
		return props;
	}

	public boolean isJustName() {
		return justName;
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
