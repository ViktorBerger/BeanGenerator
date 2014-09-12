package hr.bergear;

public class XmlParser implements Parser{

	private static XmlParser instance;
	
	private XmlParser() {
	}
	
	public static XmlParser getInstance() {
		if(instance == null) {
			instance = new XmlParser();
		}	
		return instance;
	}
	
	@Override
	public BeanInfo getBeanInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parse(String input) throws Exception{
		// TODO Auto-generated method stub
		
	}

}
