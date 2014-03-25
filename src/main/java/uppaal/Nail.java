package uppaal;

import org.jdom.*;

public class Nail extends PositionedUppaalElement{
	/**
	 * The position of a nail
	 * @param x The x position
	 * @param y The y position
	 */
	public Nail(int x, int y){
		super(x,y);
		setPositioned(true);
	}
	
	public Nail(Element nailElement) {
		super(nailElement);
	}

	/**
	 * Creates an XML Element object corresponding to the Nail object
	 * @return XML Element
	 */
	protected Element generateXMLElement(){
		Element result = super.generateXMLElement();
		return result;		
	}

	@Override
	public void setPositioned(boolean positioned) {
		super.setPositioned(true); // must have coordinates
	}
	
	@Override
	public String toString() {
		return getPosX()+","+getPosY();
	}
	
	@Override
	public
	String getXMLElementName(){
		return "nail";
	}
}
