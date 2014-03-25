package uppaal;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;


public class Declaration extends UppaalElement{
	List<String> declarations = new LinkedList<String>();;
	public Declaration(Element declarationsElement){
		String[] decls = declarationsElement.getText().split("\n");
		for(String decl : decls)
			declarations.add(decl);
	}
	
	public Declaration(Declaration declarations) {
		this.declarations.add(declarations.toString().trim());
	}
	
	public void add(Declaration declarations){
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration(String declarations){
		this.declarations = new LinkedList<String>();
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration() {
	}

	@Override
	public String getXMLElementName() {
		return "declaration";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String declaration : declarations){
			sb.append(declaration+"\n");
		}
		return sb.toString();
	}
	
	@Override
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.addContent(toString());
		return result;
	}

	public List<String> getStrings() {
		return declarations;
	}

	public void add(String s) {
		declarations.add(s);
	}
	public void remove(String s) {
		declarations.remove(s);
	}
	
}
