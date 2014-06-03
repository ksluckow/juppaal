package uppaal.labels;

import java.util.ArrayList;
import java.util.Iterator;
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
			this.updates.addAll(update.getUpdates());
	}
	
	public Update(String update, int x, int y){
		super(x,y);
		updates.add(update.trim());
	}

	public Update(Element updateElement) {
		super(updateElement);
		updates.add(updateElement.getText());
	}
	
	public List<String> getUpdates() {
		return this.updates;
	}
	
	private List<String> updates = new ArrayList<String>();
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> updateIter = updates.iterator();
		while(updateIter.hasNext()) {
			sb.append(updateIter.next());
			if(updateIter.hasNext())
				sb.append(",\n");
		}
		return sb.toString();
	}
	
	public void add(Update update){
		updates.addAll(update.getUpdates());
	}

	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "assignment");
		result.addContent(this.toString().trim());
		return result;
	}
	

}
