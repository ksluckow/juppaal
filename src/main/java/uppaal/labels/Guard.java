package uppaal.labels;

import org.jdom.Element;


public class Guard extends Label {
	public Guard(String guard){
		this(guard, 0,0);
	}
	public Guard(String guard, int x, int y) {
		super(x,y);
		this.guard = guard;
	}
	public Guard() {
		this("", 0,0);
	}
	
	public Guard(Element guardElement) {
		super(guardElement);
		this.guard = guardElement.getText();
	}
	public Guard(Guard g){
		if(g!=null)
			guard = g.toString();
	}

	String guard = "";
	
	public void setGuard(Guard guard) {
		this.guard = guard.guard;
	}
	public void conjoin(Guard guard){
		// this ^ g
		if(guard == null) return;
		// this ^ g
		if(this.guard.equals("")) 
			this.setGuard(guard);
		else if(guard.equals(""))
			return;
		else
			setGuard( new Guard("("+this.guard+") && ("+guard+")"));
	}
	
	public void disjoin(Guard guard){
		// this V g

		if(guard == null) return;
		// this V g
		if(this.guard.equals("")) 
			this.setGuard(guard);
		else if(guard.equals(""))
			return;
		else 
			setGuard(new Guard("("+this.guard+") || ("+guard+")"));
	}
	
	@Override
	public String toString() {
		return guard;
	}
	
	public boolean equals(Guard obj){
		return obj!=null && obj.equals(this.guard);
	}
	
	public boolean equals(String obj) {
		return guard.equals(obj);
	}
	
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "guard");
		result.addContent(this.toString());
		return result;
	}
}
