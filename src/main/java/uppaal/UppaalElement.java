package uppaal;

import org.jdom.Element;

public abstract class UppaalElement {

	public UppaalElement() {
		super();
	}

	/**
	 * Creates an XML Element object corresponding to the Nail object
	 * @return XML Element
	 */
	protected Element generateXMLElement(){
		Element result = new Element(getXMLElementName());		
		return result;		
	}

	public abstract String getXMLElementName();
}