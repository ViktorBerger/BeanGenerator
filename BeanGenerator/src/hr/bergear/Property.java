package hr.bergear;

public class Property {
	private String name;
	private String type;
	private String comment;
	
	public Property() {	}

	
	private  Property(String name, String type, String comment) {
		this.name = decapitalize(name);
		this.type = type;
		this.comment = comment;
	}
	
	public static Property withComment(String name, String type, String comment) {
		return new Property(name, type, comment);
	}
	
	public static Property withoutComment(String name, String type) {
		return new Property(name, type, "");
	}
	
	
	private static String decapitalize(String field) {
		return Character.toLowerCase(field.charAt(0)) + field.substring(1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Property [name=" + name + ", type=" + type + ", comment="
				+ comment + "]";
	}
}
