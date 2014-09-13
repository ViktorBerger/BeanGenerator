package hr.bergear;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser implements Parser {

	private static XmlParser instance;
	private static BeanInfo beanInfo;

	private XmlParser() {
	}

	public static XmlParser getInstance() {
		if (instance == null) {
			instance = new XmlParser();
		}
		return instance;
	}

	@Override
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

	@Override
	public void parse(String input) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Document document = builder.parse(new File("test/Person.xml"));

		NodeList beanList = document.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);

			if (bean.getNodeType() == Element.ELEMENT_NODE) {
				beanInfo = extractBeanData((Element) bean);
			}
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Document document = builder.parse(new File("test/Person.xml"));

		NodeList beanList = document.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < beanList.getLength(); i++) {
			Node bean = beanList.item(i);

			if (bean.getNodeType() == Element.ELEMENT_NODE) {
				beanInfo = extractBeanData((Element) bean);
			}
		}

	}

	private static BeanInfo extractBeanData(Element bean) {

		BeanInfo info = new BeanInfo();

		info.setName(getNodeValue(bean, "name"));

		NodeList propertyNodes = bean.getElementsByTagName("properties").item(0).getChildNodes();
		List<Property> properties = new ArrayList<>();

		for (int i = 0; i < propertyNodes.getLength(); i++) {
			if (propertyNodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
				properties.add(getProperty((Element) propertyNodes.item(i)));
			}
		}

		info.setProperties(properties);

		return info;
	}

	private static String getNodeValue(Element el, String tag) {
		NodeList children = el.getElementsByTagName(tag).item(0).getChildNodes();
		return children.getLength() > 0 ? children.item(0).getNodeValue() : "";
	}

	private static Property getProperty(Element el) {
		Property property = new Property();

		property.setName(el.getNodeName());
		property.setType(getNodeValue(el, "type"));
		property.setComment(getNodeValue(el, "comment"));

		return property;
	}

}
