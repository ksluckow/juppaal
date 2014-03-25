package uppaal;

import org.jdom.*;

/**
 * Represents a name element
 */
public class Name extends PositionedUppaalElement{
	@Override
	public String toString() {
		return name;
	}

	private String name;
	/**
	 * Creates a new name element with position 0,0.
	 * @param name The name of the Name element
	 */
	public Name(String name){
		this(name, 0, 0);
	}
	
	/**
	 * Creates a new name element
	 * @param name The name of the Name element	
	 * @param x The x position of the name location
	 * @param y The y position of the name location
	 */
	public Name(String name, int x, int y){
		super(x,y);
		this.name = name;
	}

	public Name(Element nameElement) {
		super(nameElement);
		name = nameElement.getText();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Creates an XML Element object corresponding to the Name object
	 * @return XML Element
	 */
	public Element generateXMLElement(){
		Element result = super.generateXMLElement();
		result.addContent(this.name);
		return result;
	}

	@Override
	public
	String getXMLElementName() {
		return "name";
	}
}
