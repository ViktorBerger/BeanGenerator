package hr.bergear;

public class Property {
	private String name;
	private String type;
	private String comment;
	
	public Property() {	}

	public Property(String name, String type, String comment) {
		super();
		this.name = decapitalize(name);
		this.type = type;
		this.comment = comment;
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
