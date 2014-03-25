/**
 * 
 */
package uppaal.labels;

import org.jdom.Element;
/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class ExponentialRate extends Label {

	private String expRate;
	
	public ExponentialRate(String expRate, int x, int y) {
		super(x,y);
		this.expRate = expRate;
	}
	public ExponentialRate(String expRate) {
		this(expRate, 0, 0);
	}
	public ExponentialRate() {
		this("", 0,0);
	}
	public ExponentialRate(int x, int y) {
		this("", x, y);
	}
	public ExponentialRate(Element exponentialRateElement) {
		super(exponentialRateElement);
		this.expRate = exponentialRateElement.getText();
	}
	
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "exponentialrate");
		return result.addContent(this.toString());
	}
	
	public void setExponentialRate(ExponentialRate expRate) {
		if(expRate != null) 
			this.expRate = expRate.toString();
	}
	
	@Override
	public String toString() {
		return expRate;
	}
	
	@Override
	public Object clone(){
		return new ExponentialRate(expRate);
	}

	public boolean equals(ExponentialRate obj){
		return obj!=null && obj.equals(this.expRate);
	}
	
	public boolean equals(String obj) {
		return expRate.equals(obj);
	}
	
	
}
