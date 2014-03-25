package uppaal;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;


public class SystemDeclaration extends UppaalElement{

	List<String> declarations = new LinkedList<String>();
	
	public SystemDeclaration(Element child) {
		String decls[] = child.getText().split("\n");
		boolean foundInstances = false;
		for(String declaration : decls) {
			if(declaration.replaceAll("\\s", "").equals("system")) {
				foundInstances = true;
				continue;
			}
			if(!foundInstances)
				declarations.add(declaration);
			else {
				String instance = declaration.replaceAll("[\\s,;]", "");
				if(!"".equals(instance))
					systemInstances.add(instance);
			}
		}
	}

	public SystemDeclaration() {
	}
	
	List<String> systemInstances = new LinkedList<String>();
	public void addSystemInstance(String instance){
		systemInstances.add(instance);
	}
	public void addSystemInstances(List<String> instances){
		systemInstances.addAll(instances);
	}
	public List<String> getSystemInstances() {
		return this.systemInstances;
	}
	
	public SystemDeclaration(String string) {
		this.addDeclaration(string);
	}
	public List<String> getDeclarations() {
		return this.declarations;
	}
	public void addSystemDeclaration(String string) {
		this.addDeclaration(string);
	}
	@Override
	public String getXMLElementName() {
		return "system";
	}

	@Override
	public Element generateXMLElement() {

		if(declarations.size()==0 && systemInstances.size()==0){
			System.err.println("Generating empty " + this.getClass().getCanonicalName());
		}
		StringBuilder sb = new StringBuilder();
		for(String decl : declarations)
			sb.append(decl+"\n");
		if(systemInstances.size()>0){
			sb.append("system \n\t");
			for(int index=0;index<systemInstances.size();index++){
				sb.append(systemInstances.get(index) + ((index+1)<systemInstances.size()?",\n\t":";"));
			}
		}
		Element result = super.generateXMLElement();
		result.addContent(sb.toString());
		
		return result;
	}

	public void addDeclaration(String string) {
		declarations.add(string);		
	}
	public void addDeclarations(List<String> string) {
		declarations.addAll(string);		
	}
	public void removeDeclaration(String decl) {
		declarations.remove(decl);
	}
}
