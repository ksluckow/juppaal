package uppaal.labels;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


public class Select extends Label {
	public Select() {
		this(0,0);
	}

	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "select");
		result.addContent(this.toString());
		return result;
	}
	
	
	public Select(int x, int y) {
		this("", x,y);
	}

	public Select(String select){
		this(select, 0,0);
	}
	
	public Select(Select select){
		if(select!=null)
			this.selects.add(select.toString().trim());
	}
	
	public Select(String select, int x, int y){
		super(x,y);
		selects.add(select.trim());
	}

	public Select(Element selectElement) {
		super(selectElement);
		selects.add(selectElement.getText());
	}
	
	private List<String> selects = new ArrayList<String>();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<selects.size();i++){
			sb.append(selects.get(i)+ ((i==selects.size()-2)?",\n":"\n"));
			
		}
		return sb.toString();
	}
	
	public void add(Select select){
		selects.add(select.toString());
	}
}
