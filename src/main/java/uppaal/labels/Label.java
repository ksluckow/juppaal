package uppaal.labels;

import org.jdom.*;

import uppaal.PositionedUppaalElement;

public abstract class Label extends PositionedUppaalElement implements Cloneable{
	@Override
	public
	String getXMLElementName() {
		return "label";
	}

	/**
	 * Creates a new label element with the specified name and kind/type.
	 * The position is set to 0,0
	 * @param name The name of the label element
	 */
	protected Label() {
		this(0, 0);
	}

	/**
	 * Creates a new label element with the specified name and kind/type and the position of the element
	 * @param name The name of the label element
	 * @param x The x position of the label location
	 * @param y The y position of the label location
	 */
	protected Label(int x, int y) {
		super(x,y);
	}

	public Label(Element commentElement) {
		super(commentElement);
	}
}
