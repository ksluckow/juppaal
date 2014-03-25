package uppaal.labels;

import org.jdom.Element;


public class Invariant extends Label{
	private String invariant;
	public Invariant(String invariant){
		this(invariant, 0,0);
	}
	public Invariant(String invariant, int x, int y) {
		super(x,y);
		this.invariant = invariant;
	}
	public Invariant() {
		this("", 0,0);
	}

	public Invariant(int x, int y) {
		this("", x,y);
	}

	public Invariant(Element invariantElement) {
		super(invariantElement);
		this.invariant = invariantElement.getText();
	}
	
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "invariant");
		result.addContent(this.toString());
		return result;
	}

	public void setInvariant(Invariant invariant) {
		if(invariant != null) 
			this.invariant = invariant.toString();
	}
	public void conjoin(Invariant invariant){
		if(invariant == null) return;
		// this ^ g
		if(this.invariant.equals("")) 
			this.setInvariant(invariant);
		else if(invariant.equals(""))
			return;
		else
			setInvariant( new Invariant("("+this.invariant+") && ("+invariant+")"));
	}
	
	public void disjoin(Invariant invariant){
		if(invariant == null) return;
		// this V g
		if(this.invariant.equals("")) 
			this.setInvariant(invariant);
		else if(invariant.equals(""))
			return;
		else 
			setInvariant(new Invariant("("+this.invariant+") || ("+invariant+")"));
	}
	
	@Override
	public String toString() {
		return invariant;
	}
	
	@Override
	public Object clone(){
		return new Invariant(invariant);
	}

	public boolean equals(Invariant obj){
		return obj!=null && obj.equals(this.invariant);
	}
	
	public boolean equals(String obj) {
		return invariant.equals(obj);
	}
}
