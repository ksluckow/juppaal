package uppaal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import uppaal.labels.Comment;
import uppaal.labels.Guard;
import uppaal.labels.Select;
import uppaal.labels.Update;

public class Automaton implements Comparable<Automaton>{
	private static final Pattern locationIdRegExPattern = Pattern.compile("\\d+");
	
	private Name name;
	private Parameters parameter;
	private Declaration declaration = new Declaration(); // local declarations
	private ArrayList<Location> locations = new ArrayList<Location>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private Location init;
	
	public void setAutoPositioned(boolean autoPositioned) {
		for(Location location: locations)
			location.setPositioned(autoPositioned);
		for(Transition transition: transitions)
			transition.setPositioned(autoPositioned);
	}
	
	/**
	 * Creates a new template with all properties set
	 * @param name The name of the template
	 * @param parameter The properties of the template
	 * @param declaration Local declarations for the template
	 * @param locations A list of locations in the template
	 * @param init The initial location in the template
	 * @param transitions A list of transitions between locations
	 */
	public Automaton(String name){
		this.name = new Name(name);
	}
	
	Map<String, Location> id_locationMap = new HashMap<String,Location>();	
	public Automaton(Element automatonElement) {
		this(automatonElement.getChildText("name"));
		if(automatonElement.getChild("declaration") != null)
			declaration = new Declaration(automatonElement.getChild("declaration"));
		if(automatonElement.getChild("parameter")!=null)
			parameter = new Parameters(automatonElement.getChild("parameter"));

		
		try{
			@SuppressWarnings("unchecked")
			List<Element> contents = automatonElement.getContent(new ElementFilter("location"));
			Iterator<Element> locationIterator = contents.iterator();
			while(locationIterator.hasNext()){
				Element locationElement = locationIterator.next();
				Location location = new Location(this, locationElement);
				id_locationMap.put(location.getUniqueIdString(), location);
			}
		} catch (ClassCastException e){
			e.printStackTrace();
		}
		try{
			@SuppressWarnings("unchecked")
			List<Element> contents = automatonElement.getContent(new ElementFilter("transition"));
			Iterator<Element> transitionIterator = contents.iterator();
			while(transitionIterator.hasNext()){
				Element transitionElement = transitionIterator.next();
				Transition transition = new Transition(this, transitionElement);
				addTransition(transition);
			}
		} catch (ClassCastException e){
			e.printStackTrace();
		}
		
		Element initElement =automatonElement.getChild("init");
		if (initElement != null){
			Matcher matcher = locationIdRegExPattern.matcher(initElement.getAttributeValue("ref"));
			
			boolean dbg_matchresult = matcher.find();
			assert dbg_matchresult;
			String s = matcher.group();
			init = id_locationMap.get("id" + s);
		}
	}
	
	public Location getLocation(String name){
		Location result = null;
		List<Location> locations = getLocations();
		for(Location l: locations){
			if(l.getName().toString().equals(name)){
				result = l;
				break;
			}
		}
		return result;
	}
	
	Location newLocation(){
		Location l = new Location(this);
		locations.add(l);
		return l;
	}
	
	Transition newTransition(Location from, Location to) {
		assert locations.contains(from);
		assert locations.contains(to);
		return new Transition(this, from, to);
	}
	
//	public void addBacktrack(Location location, String guard) {
//		Location backtrack = new Location("backtrack", LocationType.COMMITED, 0, 0);
//		Transition backtrackLoop = new Transition(backtrack.getId(), backtrack.getId());
//		addLocation(backtrack);
//		addTransition(backtrackLoop);
//
//		Transition t = new Transition(location.getId(), backtrack.getId());
//		t.addGuard("executionTime == inst" + location.getId());
//		t.addGuard(guard);
//		t.addUpdate("executionTime = 0");
//		addTransition(t);
//	}

	public Name getName() {
		return name;
	}

	public void setName(String name) {
		this.name.setName(name);
	}

	public Parameters getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = new Parameters(parameter);
	}

	public Declaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(Declaration declaration) {
		this.declaration = declaration;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	// This method might not be safe to use -- what happens with "dangling transitions"?
	public void removeLocation(Location loc) {
		this.locations.remove(loc);
	}

	public Location getInit() {
		if(this.init== null){
			System.err.println("initial location not set for automaton " + this.getName().getName());
			if(locations.size() > 0){
				init = locations.get(0);
				System.err.println("setting initial location");
			}
		}

		return init;
	}

	public void setInit(Location init) {
		assert contains(init) : "Initial location set is not part of the template";
		this.init = init;
	}

	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	// This method might not be safe to use @see removeLocation
	public void removeTransition(Transition removeTrans) {
		this.transitions.remove(removeTrans);
	}
	
	public void addLocation(Location location) {
		assert !getLocations().contains(location) : "Template already contains this location";
		assert location.getAutomaton() == null || this.equals(location.getAutomaton()) : "Cannot add from other automata yet";
		if(location.getAutomaton() == null)
			location.setAutomaton(this);
		this.id_locationMap.put(location.getUniqueIdString(), location);
		this.locations.add(location);
	}

	public void addTransition(Transition transition) {
		assert contains(transition.destination) :"Cannot add transition with destination outside template";
		assert contains(transition.source) : "Cannot add transition with source outside template";
		assert !getTransitions().contains(transition) : "Template already contains this location";
		this.transitions.add(transition);
	}

	public boolean contains(Location location){
		return locations.contains(location);
	}
	
	public void removeAllTransitions(Location source, Location target) {
		assert contains(source) : "Template does not contain source location";
		assert contains(target) : "Template does not contain target location";
		List<Transition> transitionCandidates = getTransitions(source, target);
		for(Transition t : transitionCandidates){
			transitions.remove(t);
		}
	}
	
	public List<Transition> getTransitions(Location source, Location target){
		assert contains(source) : "Template does not contain source location";
		assert contains(target) : "Template does not contain target location";
		ArrayList<Transition> trans = new ArrayList<Transition>();
		for (Transition transition : getTransitions()) {
			if (transition.getSource().equals(source) && transition.getTarget().equals(target))
				trans.add(transition);
		}
		return trans;
	}
	
	public List<Transition> getIncommingTransitions(Location target) {
		assert contains(target) : "Template does not contain target location";
		List<Transition> result = new LinkedList<Transition>();
		for(Transition t : getTransitions()){
			if(t.getTarget().equals(target)) {
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Transition> getOutgoingTransitions(Location source) {
		assert contains(source) : "Template does not contain source location";
		List<Transition> result = new LinkedList<Transition>();
		for(Transition t : getTransitions()){
			if(t.getSource().equals(source)) {
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Location> getSuccs(Location source){
		assert contains(source) : "Template does not contain source location";
		List<Location> result = new LinkedList<Location>();
		for(Transition transition : getTransitions()){
			for(Location location : getLocations()){
				if(location.equals(transition.getTarget()) && source.equals(transition.getSource())){
					result.add(location);
				}
			}
		}
		return result;
	}
	
	public List<Location> getPreds(Location source){
		assert contains(source) : "Template does not contain source location";
		List<Location> result = new LinkedList<Location>();
		for(Transition transition : getTransitions()){
			for(Location location : getLocations()){
				if(location.equals(transition.getSource()) && source.equals(transition.getTarget())){
					result.add(location);
				}
			}
		}
		return result;
	}
	public boolean isBranching(Location location){
		assert contains(location) : "Location is not part of the template";
		if(getSuccs(location).size()==2){
			return true;
		}
		return false;
	}
	
	public boolean isMerging(Location location){
		assert contains(location) : "Location is not part of the template";
		if(getPreds(location).size()==2){
			return true;
		}
		return false;
	}
	
	public Location getLeftBranch(Location location){
		assert isBranching(location) : "Location is not branching";
		assert contains(location) : "Location is not part of the template";
		return getSuccs(location).get(0);
	}
	public Location getRightBranch(Location location){
		assert isBranching(location) : "Location is not branching";
		assert contains(location) : "Location is not part of the template";
		return getSuccs(location).get(1);
	}


	public List<Location> getBranchPoints() {
		LinkedList<Location> branchPoints = new LinkedList<Location>();
		for(Location l : this.locations) {
			if(l.isBranchPointLocation())
				branchPoints.add(l);
		}
		return branchPoints;
	}
	
	public List<Location> getRegularLocations() {
		LinkedList<Location> regLocs = new LinkedList<Location>();
		for(Location l : this.locations) {
			if(!l.isBranchPointLocation())
				regLocs.add(l);
		}
		return regLocs;
	}
	
	/**
	 * Creates an XML Element object corresponding to the Template object
	 * @return XML Element
	 */
	public Element generateXMLElement() {
		Element result = new Element("template");

		if(name!=null)
			result.addContent(this.name.generateXMLElement());

		if(parameter!=null)
			result.addContent(this.parameter.generateXMLElement());

		if(declaration!=null){
			result.addContent(declaration.generateXMLElement());
		}
		for (Location location : getRegularLocations()) {
			result.addContent(location.generateXMLElement());
		}
		for (Location location : getBranchPoints()) {
			result.addContent(location.generateXMLElement());
		}

		if (this.getInit() != null) {
			Element initElement = new Element("init");
			initElement.setAttribute("ref", this.init.getUniqueIdString());
			result.addContent(initElement);
		}

		for (Transition transition : transitions) {
			result.addContent(transition.generateXMLElement());
		}

		return result;
	}

	@Override
	public int compareTo(Automaton arg0) {
		return this.getName().getName().compareTo(arg0.getName().getName());
	}
	
	public int removeStumpAngles(double angleThreshold, double lengthThreshold) {
		int count = 0;
		for(Transition t: transitions){
			count += t.removeStumpAngles(angleThreshold, lengthThreshold);
		}
		return count;
	}
	
	
	////******************** PRODUCT TEST *****************************
	private Map<String, Location> cloc = new HashMap<String, Location>();
	private Location getPLocation(Automaton product, Location a, Location b){
		String name = a.getName().toString()+"_"+b.getName().toString();
		String idstr = a.getUniqueIdString() + "_" + b.getUniqueIdString();
		Location result = new Location(product, name);
		if(a.isAccept() && b.isAccept()){
			System.out.println(name);
			result.setAccept(true);
		}
		cloc.put(idstr, result);
		result.getInvariant().conjoin(a.getInvariant());
		result.getInvariant().conjoin(b.getInvariant());
		System.out.println(idstr);
		if(a.getComment()!=null && b.getComment()!=null && b.getComment().equals(a.getComment()))
			result.setComment(new Comment(b.getComment()));
		return result;
	}
	public Automaton(Automaton a, Automaton b, String name){
		this(name);
		Automaton spec = a;
		Automaton impl = b;
		System.out.println("Location product");
		List<Location> iL = impl.getLocations();
		List<Location> iS = spec.getLocations();
		for (Location il: iL){
			for(Location sl: iS){
				getPLocation(this, il, sl);
			}
		}
		System.out.println("Transition product");
		List<Transition> iT = impl.getTransitions();
		List<Transition> sT = spec.getTransitions();
		System.out.println("IT");
		for(Transition it: iT){
			for(Location s : spec.getLocations()){
				String source = it.getSource().getUniqueIdString() +"_"+ s.getUniqueIdString(); 
				String target = it.getTarget().getUniqueIdString() +"_"+ s.getUniqueIdString();
				System.out.println(source +"    ->    " + target);				
				Transition transition = new Transition(this, cloc.get(source), cloc.get(target));
				transition.setGuard(new Guard(it.getGuard()));
				transition.setSelect(new Select(it.getSelect()));
				transition.setUpdate(new Update(it.getUpdate()));
				System.out.println(transition.getSource().getName() +"    ->    " + transition.getTarget().getName());
				System.out.println(it.getGuard());
				System.out.println(transition.getGuard());

			}
		}
		System.out.println("ST product");
		for(Transition st: sT){
			for(Location i : impl.getLocations()){
				System.out.println();
				System.out.println("loc: "+i.getName());
				String source = i.getUniqueIdString() + "_" + st.getSource().getUniqueIdString(); 
				String target = i.getUniqueIdString() + "_" + st.getTarget().getUniqueIdString() ;
				System.out.println(source +"    ->    " + target);
				System.out.println(cloc.get(source) + " --- "+ cloc.get(target));
				Transition transition = new Transition(this, cloc.get(source), cloc.get(target));
				transition.setGuard(new Guard(st.getGuard()));
				transition.setSelect(new Select(st.getSelect()));
				transition.setUpdate(new Update(st.getUpdate()));
				System.out.println(transition.getSource().getName() +"    ->    " + transition.getTarget().getName());				
			}
		}

		String init = impl.getInit().getUniqueIdString() + "_"+ spec.getInit().getUniqueIdString();
		this.setInit(cloc.get(init));

		Declaration d = new Declaration(spec.getDeclaration());
		System.out.println(d);
		d.add(impl.getDeclaration());
		System.out.println(d);
		this.setDeclaration(d);
		this.setAutoPositioned(true);
		/*		
			for(Transition st: sT){
//				if(st.getSync()!=null) {
//					System.err.println("No handling of syncs yet");;
//					continue;
//				}
				System.out.println(it.getSource().getName() + " -> "+ it.getTarget().getName());
				System.out.println(st.getSource().getName() + " -> "+ st.getTarget().getName());
				String source = it.getSource().getIdString() +"_"+ st.getSource().getIdString(); 
				String target = it.getSource().getIdString() +"_"+ st.getTarget().getIdString();
				System.out.println(source +"    ->    " + target);				
				Transition i = new Transition(product, cloc.get(source), cloc.get(target));
				i.setGuard(new Guard(st.getGuard()));
				i.setSelect(new Select(st.getSelect()));
				i.setUpdate(new Update(st.getUpdate()));
				product.addTransition(i);
				System.out.println(i.getSource().getName() +"    ->    " + i.getTarget().getName());				
				source = it.getSource().getIdString() +"_"+ st.getSource().getIdString(); 
				target = it.getTarget().getIdString() +"_"+ st.getSource().getIdString();
				Transition s = new Transition(product, cloc.get(source), cloc.get(target));
				s.setGuard(new Guard(it.getGuard()));
				s.setSelect(new Select(it.getSelect()));
				s.setUpdate(new Update(it.getUpdate()));
				product.addTransition(s);				
				System.out.println(s.getSource().getName() +"    ->    " + s.getTarget().getName());				
//				Transition prod = new Transition(product);
			}
		 */
	}


////******************** PRODUCT TEST END *****************************
}
