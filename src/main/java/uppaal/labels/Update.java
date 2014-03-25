package uppaal.labels;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;



public class Update extends Label {
	public Update(int x, int y) {
		this("", x,y);
	}

	public Update(String update){
		this(update, 0,0);
	}
	
	public Update(Update update){
		if(update!=null)
			this.updates.add(update.toString().trim());
	}
	
	public Update(String update, int x, int y){
		super(x,y);
		updates.add(update.trim());
	}

	public Update(Element updateElement) {
		super(updateElement);
		updates.add(updateElement.getText());
	}
	
	private List<String> updates = new ArrayList<String>();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<updates.size();i++){
			sb.append(updates.get(i)+ ((i==updates.size()-2)?",\n":"\n"));
			
		}
		return sb.toString();
	}
	
	public void add(Update update){
		updates.add(update.toString());
	}

	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "assignment");
		result.addContent(this.toString().trim());
		return result;
	}
	

}
