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
public class Parser {

	private String beanName;
	private List<Property> props;
	private boolean justName;
	private String delimiter;

	public Parser() {
		this.delimiter = "\\s+";
		props = new ArrayList<>();
	}

	/**
	 * Parser constructor with delimiter.
	 * 
	 * @param delimiter
	 *            delimiter
	 */
	public Parser(String delimiter) {
		this.delimiter = delimiter;
		props = new ArrayList<>();
	}

	/**
	 * Main parsing method.
	 * 
	 * @param input
	 *            textual input
	 */
	public void parseInput(String input) {
		beanName = "";
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

		beanName = ps[0].trim();

		for (int i = 1; i < ps.length; i++) {
			String trimmed = ps[i].trim();

			if (!trimmed.isEmpty()) {
				props.add(createProperty(trimmed.split(delimiter)));
			}
		}
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
			return new Property(attributes[0].trim(), "String", "");
		}
		if (attributes.length == 2) {
			return new Property(attributes[0].trim(), attributes[1].trim(), "");
		}
		String comment = "";
		for (int i = 2; i < attributes.length; i++) {
			comment += attributes[i].trim() + " ";
		}

		return new Property(attributes[0].trim(), attributes[1].trim(), comment);
	}

	/**
	 * Returns bean name
	 * 
	 * @return bean name
	 */
	public String getBeanName() {
		return beanName;
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

}
