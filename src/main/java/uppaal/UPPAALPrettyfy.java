package uppaal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.Node;


public class UPPAALPrettyfy {
	private static Pattern posPattern = Pattern.compile("pos=\"(\\d+),(\\d+)");
	
	@SuppressWarnings("unchecked")
	public static Document getPrettyLayoutXml(Document uppaalXml) throws IOException, JDOMException, InterruptedException{
		
		Element rootElement = uppaalXml.getRootElement();
		
		List<Element> templates = rootElement.getChildren("template");
				
		for(Element template : templates){
			List<Element> transitions = template.getChildren("transition");
			for(Element transition : transitions) {
				List<Element> labels = transition.getChildren("label");
				for(Element label : labels) {
					label.removeAttribute("x");
					label.removeAttribute("y");
				}
				transition.removeChildren("nail");
				
			}
			List<Element> locations = template.getChildren("location");		
			for(Element location : locations) {
				location.removeAttribute("x");
				location.removeAttribute("y");
				
				List<Element> labels = location.getChildren("label");
				for(Element label : labels) {
					label.removeAttribute("x");
					label.removeAttribute("y");
				}
				List<Element> names = location.getChildren("name");
				for(Element name : names) {
					name.removeAttribute("x");
					name.removeAttribute("y");
				}
			}
			List<Element> branchpoints = template.getChildren("branchpoint");		
			for(Element branchpoint : branchpoints) {
				branchpoint.removeAttribute("x");
				branchpoint.removeAttribute("y");
			}

			Graph graph = new Graph(template.getChildText("name"));
			Attribute graphAttr = new Attribute(Attribute.SUBGRAPH, Attribute.LABEL_ATTR, template.getChildText("name"));
			graph.setAttribute(graphAttr);
			
			graph = loadGraph( template, graph);
			
			File graphFile = writeGraph(template, graph);
			
			FileReader freader = new FileReader(graphFile);
			LineNumberReader lreader = new LineNumberReader(freader);
			String line = "";
			int x,y;
			String elementName = "";

			while ((line = lreader.readLine()) != null) {
				if (line.contains("height")) {
				  int end = line.indexOf(' ');
					elementName = (end > 0) ? line.substring(0, end).trim() : "";
					Matcher matcher = posPattern.matcher(line);
					if (matcher.find()) {
						x = Integer.valueOf(matcher.group(1));
						y = Integer.valueOf(matcher.group(2));
						XPath xPath = XPath.newInstance("location[@id=$name] | branchpoint[@id=$name]");
						xPath.setVariable("name", elementName);
						Element foundElement = (Element) xPath.selectSingleNode(template);
						foundElement.setAttribute("x", String.valueOf(-x));
						foundElement.setAttribute("y", String.valueOf(-y));
					}
				}
			}
			lreader.close();
		}
		return uppaalXml;
	}
	
	
	private static File writeGraph(Element template, Graph graph) throws FileNotFoundException, IOException, InterruptedException {
		File folder = new File("/tmp/juppaal/");
		folder.mkdirs();
		String filename = "/tmp/juppaal/" + template.getChildText("name") + ".dot";
		File file = new File(filename);
		if(!file.exists())
		  file.createNewFile();
		FileOutputStream fo = new FileOutputStream(new File(filename));
		graph.printGraph(fo);
		if(template.getChildren().size()<500){
		  String OS = System.getProperty("os.name").toLowerCase();
		  String dotCmd;
		  if(OS.contains("mac"))
		    dotCmd = "/usr/local/bin/dot";
		  else
		    dotCmd = "/usr/bin/dot";

		  Process proc = Runtime.getRuntime().exec(dotCmd + " -T dot -O " +filename);
		  proc.waitFor();
		  proc.exitValue();
		  proc.destroy();
		}
		else{
			return new File(filename);
		}

		return new File(filename+".dot");
	}
	
	@SuppressWarnings("unchecked")
	private static Graph loadGraph(Element template, Graph graph)	throws IOException {
		List<Element> locations = template.getChildren("location");
		List<Element> branchpoints = template.getChildren("branchpoint");
		LinkedList<Element> allLocations = new LinkedList<Element>(locations);
		allLocations.addAll(branchpoints);
		//locations.addAll();
		for(Element location : allLocations){
			Node locationNode = new Node(graph, location.getAttributeValue("id"));
			String nameAttr = location.getChildText("name");
			if(nameAttr != null) {
				StringBuilder lblStr = new StringBuilder();
				lblStr.append(nameAttr);
				/*List<Element> lblChildren = location.getChildren("label");
				for(Element lblChild : lblChildren) {
					String attrVal = lblChild.getAttributeValue("kind");
					if(attrVal.equals("comments")) {
						String[] commentFormat = lblChild.getText().split(":");
						if(commentFormat.length > 0) { 
							lblStr.append('_')
								  .append(commentFormat[commentFormat.length - 1]);
							break;
						}
					}
				}*/
				Attribute attr = new Attribute(Attribute.NODE, Attribute.LABEL_ATTR, lblStr.toString());
				locationNode.setAttribute(attr);
			}

			graph.addNode(locationNode);
		}
		List<Element> transitions = template.getChildren("transition");	
		for(Element transition : transitions){
			
			Node head = graph.findNodeByName(transition.getChild("source").getAttributeValue("ref"));
			Node tail = graph.findNodeByName(transition.getChild("target").getAttributeValue("ref"));
			
			Edge edge = new Edge(graph, head, tail);
			graph.addEdge(edge);
		}
		
		return graph;
	
		
	}
	
}
