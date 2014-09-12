package hr.bergear;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates JavaBeans source code.
 * 
 * @author Viktor
 *
 */
public class BeanGenerator {

	private static Set<String> types = new HashSet<>(Arrays.asList("int", "double", "float", "boolean", "String",
			"Ineteger", "Double", "Float", "Boolean"));

	private static StringBuffer beanCodeBuffer = new StringBuffer();
	private static StringBuffer commentBuffer = new StringBuffer();

	private static final int GETTER = 0;
	private static final int SETTER = 1;

	/**
	 * Generates bean fields.
	 * 
	 * @param properties
	 *            list of bean properties (fields).
	 */
	private static void generateFields(List<Property> properties) {
		for (Property property : properties) {
			beanCodeBuffer.append("\tprivate " + property.getType() + " " + decapitalize(property.getName()) + ";\r\n");
		}
		beanCodeBuffer.append("\r\n");
	}

	/**
	 * Wraps comment to the given number of characters per row.
	 * 
	 * @param comment
	 *            text of the comment
	 * @param numChars
	 *            number of characters per row
	 * @return wrapped comment
	 */
	private static String wrapComment(String comment, int numChars) {
		commentBuffer.setLength(0);
		commentBuffer.append(comment);
		int i = 0;
		while (i + numChars < commentBuffer.length() && (i = commentBuffer.lastIndexOf(" ", i + numChars)) != -1) {
			commentBuffer.replace(i, i + 1, "\n\t * \t");
		}
		return commentBuffer.toString();
	}

	/**
	 * Generates a comment for getter or setter based on given type argument.
	 * 
	 * @param property
	 *            bean fields
	 * @param type
	 *            getter or setter
	 */
	private static void generateComment(Property property, int type) {
		if (property.getComment().isEmpty()) {
			return;
		}

		beanCodeBuffer.append("\t/**\r\n\t");

		if (type == GETTER) {
			beanCodeBuffer.append(wrapComment(" * Returns " + property.getComment() + "\r\n", 80));
			beanCodeBuffer.append(wrapComment("\t * @return " + property.getComment(), 80));
		} else if (type == SETTER) {
			beanCodeBuffer.append(wrapComment(" * Sets " + property.getComment() + "\r\n", 80));
			beanCodeBuffer
					.append(wrapComment("\t * @param " + property.getName() + " is " + property.getComment(), 80));
		}

		beanCodeBuffer.append("\r\n\t */\r\n");
	}

	/**
	 * Generates bean getter methods based on provided fields.
	 * 
	 * @param properties
	 *            bean fields
	 */
	private static void generateGetters(List<Property> properties) {
		for (Property property : properties) {
			generateComment(property, GETTER);
			beanCodeBuffer.append("\tpublic " + property.getType() + " get" + capitalize(property.getName())
					+ "() {\r\n");
			beanCodeBuffer.append("\t\treturn " + decapitalize(property.getName()) + ";\r\n\t}");
			beanCodeBuffer.append("\r\n");
		}

	}

	/**
	 * Generates bean setter methods based on provided fields.
	 * 
	 * @param properties
	 *            bean fields
	 */
	private static void generateSetters(List<Property> properties) {
		for (Property property : properties) {
			generateComment(property, SETTER);
			beanCodeBuffer.append("\tpublic void set" + capitalize(property.getName()) + "("
					+ property.getType() + "  " + decapitalize(property.getName()) + ") {\r\n");
			beanCodeBuffer.append("\t\tthis." + decapitalize(property.getName()) + "=" + decapitalize(property.getName())
					+ ";\r\n\t}");
			beanCodeBuffer.append("\r\n");
		}
	}

	/**
	 * Decapitalizes the first character in the provided string.
	 * 
	 * @param field
	 *            string to decapitalize
	 * @return decapitalized string
	 */
	private static String decapitalize(String field) {
		return Character.toLowerCase(field.charAt(0)) + field.substring(1);
	}

	/**
	 * Capitalizes the first character in the provided string.
	 * 
	 * @param field
	 *            string to capitalize
	 * @return capitalized string
	 */
	private static String capitalize(String field) {
		return Character.toUpperCase(field.charAt(0)) + field.substring(1);
	}

	private static boolean checkTypes(List<Property> properties) {
		boolean isValid = true;

		for (Property prop : properties) {
			if (!types.contains(prop.getType())) {
				System.out.println("Type of property " + prop.getName() + " is not supported (" + prop.getType() + ")");
				isValid = false;
			}
		}

		return isValid;
	}

	/**
	 * Generates code of a java bean based on the provided properties.
	 * 
	 * @param name
	 *            name of a new java bean
	 * @param properties
	 *            bean properties
	 * @return source code of the new java bean
	 */
	public static String generateBean(BeanInfo info) {

		beanCodeBuffer.setLength(0);
		commentBuffer.setLength(0);
		
		List<Property> properties = info.getProperties();

		if (!checkTypes(properties)) {
			return "Pogrešni tip!";
		}

		beanCodeBuffer.append("public class " + info.getName() + " {\r\n");
		generateFields(properties);
		generateGetters(properties);
		generateSetters(properties);
		beanCodeBuffer.append("}");

		return beanCodeBuffer.toString();
	}

}
