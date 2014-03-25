/**
 * 
 */
package uppaal.labels;

import org.jdom.Element;
/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class Probability extends Label {

	private int probability;
	
	public Probability(int probability, int x, int y) {
		super(x,y);
		this.probability = probability;
	}
	public Probability(int probability) {
		this(probability, 0, 0);
	}
	public Probability() {
		this(0, 0,0);
	}
	public Probability(int x, int y) {
		this(0, x, y);
	}
	public Probability(Element probabilityElement) {
		super(probabilityElement);
		this.probability = Integer.parseInt(probabilityElement.getText());
	}
	
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "probability");
		return result.addContent(this.toString());
	}
	
	public void setProbability(Probability prob) {
		if(prob != null) 
			this.probability = prob.getProbabilityWeight();
	}
	
	public int getProbabilityWeight() {
		return this.probability;
	}
	
	@Override
	public String toString() {
		return Integer.toString(probability);
	}
	
	@Override
	public Object clone(){
		return new Probability(probability);
	}

	public boolean equals(Probability obj){
		return obj!=null && obj.equals(this.probability);
	}
	
	public boolean equals(String obj) {
		return Integer.toString(probability).equals(obj);
	}
	
	
}
