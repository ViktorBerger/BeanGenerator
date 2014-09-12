package hr.bergear;

import java.util.List;

public class BeanInfo {

	private String name = "";
	private String comment = "";
	private List<String> imports;
	private List<Property> props;

	public List<Property> getProperties() {
		return props;
	}

	public void setProperties(List<Property> props) {
		this.props = props;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
