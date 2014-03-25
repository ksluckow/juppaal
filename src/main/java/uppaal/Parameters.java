package uppaal;

import org.jdom.Element;

public class Parameters extends UppaalElement{
	
	String parameters ="";
	/**
	 * Creates a new parameter element with position 0,0.
	 * @param name The name of the parameter element
	 */
	public Parameters(String parameters){
		this.parameters = parameters;
	}
	
	public Parameters(Element child) {
		this(child.getText());
	}

	@Override
	public String getXMLElementName() {
		return "parameter";
	}
	
	@Override
	protected Element generateXMLElement() {
		Element result =  super.generateXMLElement();
		result.addContent(parameters);
		return result;
	}
}
