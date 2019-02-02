package uppaal;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class NTA extends UppaalElement{
	private Declaration declarations = new Declaration();
	private SystemDeclaration systemDeclaration = new SystemDeclaration();
	
	public SystemDeclaration getSystemDeclaration() {
		return systemDeclaration;
	}

	public void setSystemDeclaration(SystemDeclaration systemDeclaration) {
		this.systemDeclaration = systemDeclaration;
	}

	private List<Automaton> automata = new LinkedList<Automaton>();
	private String systemName;

	public NTA() {
	}
	
	public void setAutoPositioned(boolean autoPositioned) {
		for(Automaton a: automata)
			a.setAutoPositioned(autoPositioned);
	}

	
//	public void normalize(){
//		for (Automaton automaton : automata){
//			
//		}
//	}

	public NTA(String uppaalFile) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document uppaalDoc = builder.build(uppaalFile);
			buildNTA(uppaalDoc);
		} catch (IOException | JDOMException e) {
			e.printStackTrace();
		}
	}

	public NTA(InputStream uppaalFile) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document uppaalDoc = builder.build(uppaalFile);
			buildNTA(uppaalDoc);
		} catch (IOException | JDOMException e) {
			e.printStackTrace();
		}
	}

	private void buildNTA(Document uppaalDoc){
		@SuppressWarnings("unchecked")
		Iterator<Element> i = uppaalDoc.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element child = i.next();
			if (child.getName().equals("declaration")) {
				assert child.getContent().size() == 1 : "Declaration elements should not have children";
				declarations = new Declaration(child);
			} else if (child.getName().equals("template")) {
				Automaton automaton = new Automaton(child);
				automata.add(automaton);
			} else if (child.getName().equals("system")) {
				systemDeclaration = new SystemDeclaration(child);
			} else {
				System.err.println("unhandled element: "+child.getName());
			}
		}
	}

	public Declaration getDeclarations() {
		return declarations;
	}

	public void setDeclarations(Declaration declarations) {
		this.declarations = declarations;
	}

	public List<Automaton> getAutomata() {
		return automata;
	}

	public void setTemplates(ArrayList<Automaton> automata) {
		this.automata = automata;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	public void addAutomaton(Automaton automaton) {
		automata.add(automaton);
	}
	public void addAutomata(List<Automaton> automataList) {
		automata.addAll(automataList);
	}
	public boolean removeAutomaton(Automaton automaton) {
		return automata.remove(automaton);
	}
	public boolean removeAutomaton(String automatonName) {
		for(Automaton aut : this.automata) {
			if(aut.getName().getName().equals(automatonName)) {
				return this.automata.remove(aut);
			}
		}
		return false;
	}
	@Deprecated
	private void updateLabelPositions() {
		int spaceBetweenLabels = 15; //The size between lines in UPPAAL
		int locationDistance = 30;

		for (Automaton template : automata) {
			for (Location location : template.getLocations()) {
				int x = location.getPosX();
				int y = location.getPosY();
				int offset = locationDistance;
				if (location.getName()!= null) {
					location.getName().setPosX(x - spaceBetweenLabels);
					location.getName().setPosY(y - offset);
					offset += spaceBetweenLabels;
				}
				if (location.getInvariant()!=null) {
					location.getInvariant().setPosX(x - spaceBetweenLabels);
					location.getInvariant().setPosY(y - offset);
				}
			}
			for (Transition transition : template.getTransitions()) {
				Location source = transition.getSource();
				Location target = transition.getTarget();
				int sourceX = 0;
				int sourceY = 0;
				int targetX = 0;
				int targetY = 0;

				List<Nail> nails = transition.getNails();
				int nrNails = nails.size();
				if (nrNails == 0) {
					for (Location location : template.getLocations()) {
						if (location.equals(source)) {
							sourceX = location.getPosX();
							sourceY = location.getPosY();
						}
						if (location.equals(target)) {
							targetX = location.getPosX();
							targetY = location.getPosY();
						}
					}
				} else if (nrNails == 1) {
					for (Location location : template.getLocations()) {
						if (location.equals(source)) {
							sourceX = location.getPosX();
							sourceY = location.getPosY();
							break;
						}
					}
					targetX = nails.get(0).getPosX();
					targetY = nails.get(0).getPosY();
				} else {
					sourceX = nails.get((nrNails / 2) - 1).getPosX();
					sourceY = nails.get((nrNails / 2) - 1).getPosY();
					targetX = nails.get(nrNails / 2).getPosX();
					targetY = nails.get(nrNails / 2).getPosY();
				}

				int x = sourceX + (targetX - sourceX) / 2;
				int y = sourceY + (targetY - sourceY) / 2;

				// TODO test if it works
				int offset = -locationDistance;
				if (transition.getSelect()!=null) {
					transition.getSelect().setPosX(x - locationDistance);
					transition.getSelect().setPosY(y + offset);
					//offset must be added with spaceBetweenLabels as many times there are newlines
					offset += spaceBetweenLabels * transition.getSelect().toString().split("\n").length;
				}
				if (transition.getGuard()!=null) {
					transition.getGuard().setPosX(x - locationDistance);
					transition.getGuard().setPosY(y + offset);
					//offset must be added with spaceBetweenLabels as many times there are newlines
					offset += spaceBetweenLabels * transition.getGuard().toString().split("\n").length;
				}
				if (transition.getSync()!=null) {
					transition.getSync().setPosX(x - locationDistance);
					transition.getSync().setPosY(y + offset);
				}
				if (transition.getUpdate()!=null) {
					transition.getUpdate().setPosX(x - locationDistance);
					transition.getUpdate().setPosY(y + offset);
					//offset must be added with spaceBetweenLabels as many times there are newlines
					offset += spaceBetweenLabels * transition.getUpdate().toString().split("\n").length;
				}
			}
		}
	}

	final int xInterval = 200;
	final int yInterval = 125;
	final int nailX1 = 25;
	final int nailY1 = 25;
	final int nailX2 = 25;
	final int nailY2 = -25;

	@Deprecated
	public void userFriendlyOutput() {
		for (Automaton automaton : automata) {
			if (automaton.getInit() == null) {
				int initId = Integer.MAX_VALUE;
				Location initLoc = null;
				for (Location location : automaton.getLocations()) {
					// if no init is set, the location with the lowest ID is
					// chosen
					if (location.getId() < initId) {
						initId = location.getId();
						initLoc = location;
					}
					// reset all positions
					location.setPosX(-1);
					location.setPosY(-1);
				}
				initLoc.setPosX(0);
				initLoc.setPosY(0);
				automaton.setInit(initLoc);
			} else {
				for (Location location : automaton.getLocations()) {
					// if no init is set, the location with the lowest ID is
					// chosen
					location.setPosX(-1);
					location.setPosY(-1);
				}
				automaton.getInit().setPosX(0);
				automaton.getInit().setPosY(0);
			}

			updateLocations(automaton, automaton.getInit());
			
			//Add nails to loops
			for (Transition transition : automaton.getTransitions()) {
				if (transition.getSource() == transition.getTarget()) {
					for (Location location : automaton.getLocations()) {
						if (location.equals(transition.getSource())){
							transition.addNail(new Nail(location.getPosX() + nailX1,
									location.getPosY() + nailY1));
							transition.addNail(new Nail(location.getPosX() + nailX2,
									location.getPosY() + nailY2));
						}
					}
				}
			}
		}
		
		
		// Updates the position of the labels after the locations and
		// transitions has been moved
		updateLabelPositions();
	}

	@Deprecated
	private void updateLocations(Automaton automaton,
			Location currentLocation) {
		ArrayList<Location> succ = new ArrayList<Location>();
		int locationOffset = 0;
		for (Transition transition : automaton.getTransitions()) {
			// Only change location if it is not a loop
			if (transition.getSource() != transition.getTarget()) {
				if (transition.getSource().equals(currentLocation)) {
					// Find the opposite location
					for (Location location : automaton.getLocations()) {
						if (location.equals(transition.getTarget())) {
							// only update the position if it has not already
							// been updated
							if (location.getPosX() == -1) {
								location.setPosX(currentLocation.getPosX()
										+ xInterval * locationOffset);
								location.setPosY(currentLocation.getPosY()
										+ yInterval);
								succ.add(location);
								locationOffset++;
								// Check if it is a A -> B -> A set of
								// transitions
								for (Transition trans2 : automaton
										.getTransitions()) {
									if (trans2.getSource() == transition
											.getTarget()
											&& trans2.getTarget() == transition
													.getSource()) {
										if (trans2.getNails().size() == 0
												&& transition.getNails().size() == 0) {
											// if it is a A -> B -> A then we
											// want B to be to the right of A,
											// but it must not interfere with
											// other locations so subtract 50
											// from the X position
											location.setPosX(currentLocation
													.getPosX()
													+ xInterval
													* (locationOffset) - 50);
											location.setPosY(currentLocation
													.getPosY());
											trans2.addNail(new Nail(
													currentLocation.getPosX()
															+ nailX1,
													currentLocation.getPosY()
															+ nailY1));
											transition.addNail(new Nail(
													currentLocation.getPosX()
															+ nailX2,
													currentLocation.getPosY()
															+ nailY2));
											break;
										}
									}
								}
								break;
							} else {
								// Check if the location is above the current
								// location and at the same X coordinate
								if (currentLocation.getPosX() == location
										.getPosX()
										&& currentLocation.getPosY() > location
												.getPosY()) {
									currentLocation.setPosX(currentLocation
											.getPosX()
											+ xInterval - 50);
								}
							}
						}
					}
				}
			}
		}
		for (Location location : succ) {
			updateLocations(automaton, location);
		}
	}
	
	public void writeXML(PrintStream printStream){
//		updateLabelPositions();
		writeDocument(generateXMLDocument(), printStream);
	}
	
	public void writeXMLWithPrettyLayout(PrintStream printStream) {
		try {
			Document uDoc = UPPAALPrettyfy.getPrettyLayoutXml(generateXMLDocument());
			writeDocument(uDoc, printStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
	
	private void writeDocument(Document uppaalXMLDoc, PrintStream pStream) {
		Format format = Format.getPrettyFormat();
		XMLOutputter output = new XMLOutputter(format);
		try {
			
			output.output(uppaalXMLDoc, pStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Element generateXMLElement() {
		Element result=super.generateXMLElement(); 
		
		result.addContent(declarations.generateXMLElement());
		for(Automaton automaton : automata){
			result.addContent(automaton.generateXMLElement());
		}
		result.addContent(systemDeclaration.generateXMLElement());
		return result;
	}
	
	/**
	 * Creates a valid UPPAAL XML document, containing the entire model
	 * 
	 * @return The UPPAAL model as an XML document
	 */
	private Document generateXMLDocument() {
		return new Document(this.generateXMLElement(),new DocType("nta",
				"-//Uppaal Team//DTD Flat System 1.1//EN",
		"flat-1_1.dtd"));
	}
	
	/**
	 * Writes the entire XML document of the model to a file
	 * 
	 * @param outputFile
	 *            The target file
	 */
	public void writeModelToFile(String outputFile) {
		try {
			java.io.PrintStream printStream = new PrintStream(outputFile);
			this.writeXML(printStream);
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writePrettyLayoutModelToFile(String outputFile) {
		try {
			java.io.PrintStream printStream = new PrintStream(outputFile);
			this.writeXMLWithPrettyLayout(printStream);
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean correctXML(String inputFile) {
		// Check whether the output is valid UPPAAL XML
		System.out.println("Validating XML file...");
		try {
			SAXBuilder b = new SAXBuilder();
			b.setValidation(true);
			String msg = "Correct XML!";
			try {
				b.build(inputFile);
			} catch (JDOMParseException e) {
				msg = e.getMessage();
			}
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("XML validation complete.");
		return true;
	}
	
	//TODO Reconsider this! -- replace by getChannelIdentifier
	static int ID = 0;
	
	@Override
	public String getXMLElementName() {
		return "nta";
	}

	public Automaton getAutomaton(String string) {
		List<Automaton> automata = getAutomata();
		for(Automaton automaton : automata){
			if(automaton.getName().toString().equals(string)){
				return automaton;
			}
		}
		System.err.println("Automaton ["+string +"] not found in "+this.getSystemName());
		return null;
	}

	public int removeStumpAngles(double angleThreshold, double lengthThreshold) {
		int count = 0;
		for(Automaton a: automata){
			count += a.removeStumpAngles(angleThreshold, lengthThreshold);
		}
		return count;
	}
}
