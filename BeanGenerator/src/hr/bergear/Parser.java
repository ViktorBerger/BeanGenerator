package hr.bergear;

public interface Parser {
	void parse(String input) throws Exception;
	BeanInfo getBeanInfo();
}
