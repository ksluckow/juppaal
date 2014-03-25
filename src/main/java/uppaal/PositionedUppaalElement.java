package uppaal;

import org.jdom.Element;

public abstract class PositionedUppaalElement extends UppaalElement {
	private int posX;
	private int posY;
	private boolean positioned = false;
	
	public boolean isPositioned() {
		return positioned;
	}

	public void setPositioned(boolean positioned) {
		this.positioned = positioned;
	}

	/**
	 * The position of an element
	 * @param x The x position
	 * @param y The y position
	 */
	public PositionedUppaalElement(int x, int y){
		this.posX = x;
		this.posY = y;
		setPositioned(true);
	}
	
	public PositionedUppaalElement(Element xmlRepresentation) {
		if (xmlRepresentation.getAttributeValue("x") == null || xmlRepresentation.getAttributeValue("y") == null){
			positioned = false;
		} else {
			posX = Integer.parseInt(xmlRepresentation.getAttributeValue("x"));
			posY = Integer.parseInt(xmlRepresentation.getAttributeValue("y"));
			setPositioned(true);
		}
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
		positioned = true;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
		positioned = true;
	}
	
	/**
	 * Creates an XML Element object corresponding to the Nail object
	 * @return XML Element
	 */
	protected Element generateXMLElement(){
		Element result = super.generateXMLElement();
		if(isPositioned()){
			result.setAttribute("x", Integer.toString(posX));
			result.setAttribute("y", Integer.toString(posY));
		}
		
		return result;		
	}
	
}
