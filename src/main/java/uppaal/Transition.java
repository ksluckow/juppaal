package uppaal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import uppaal.labels.Guard;
import uppaal.labels.Probability;
import uppaal.labels.Select;
import uppaal.labels.Synchronization;
import uppaal.labels.Update;

public class Transition {
	private static final Pattern locationIdRegExPattern = Pattern.compile("[a-zA-Z_]*([0-9]*)");
	Location source;
	Location destination;
	Automaton automaton;
	public void setPositioned(boolean positioned) {
		for(Nail nail : nails){
			nail.setPositioned(positioned);
		}
		if(select != null)
			select.setPositioned(positioned);
		if(guard != null)
			guard.setPositioned(positioned);
		if(update != null)
			update.setPositioned(positioned);
		if(sync!= null)
			sync.setPositioned(positioned);
	}
	
	private Select select;
	private Guard guard;
	private Synchronization sync;
	private Probability prob;
	
	private List<Nail> nails = new ArrayList<Nail>();
	private Update update;
	private Color color;

	public int removeStumpAngles(double angleThreshold, double lengthThreshold){
		// dot product: Ax * Bx + Ay * By
		// angle = acos(dot(v1, v2))
		int count = 0;
		boolean removed = false;
		do{
			removed = false;
			Nail prev = null;
			for(int index=0;index<nails.size();index++){
				Nail next;
				Nail cur = nails.get(index);
				if(index > 0){
					prev = nails.get(index-1);
				} else {
					prev = new Nail(source.getPosX(), source.getPosY());
				}
				if(nails.size() > index+1){
					next = nails.get(index+1);
				} else {
					next = new Nail(destination.getPosX(), destination.getPosY());
				}
				double v1x = prev.getPosX()-cur.getPosX();
				double v1y = prev.getPosY()-cur.getPosY();
				double v2x = next.getPosX()-cur.getPosX();
				double v2y = next.getPosY()-cur.getPosY();
				double lenA = (Math.sqrt((v1x*v1x)+(v1y*v1y)));
				double lenB = (Math.sqrt((v2x*v2x)+(v2y*v2y)));
				if(lenA<lengthThreshold || lenB<lengthThreshold){
					nails.remove(index);
					count++;
					removed=true;
					break;
				}
				double dot = (v1x * v2x + v1y * v2y)/(lenA*lenB);
				//clamp input to between 1.0..-1.0
				dot =  Math.max(-1.0, Math.min(dot, 1.0));
				double radian = Math.acos(dot);
				double angle  = (radian * (180.0/Math.PI));
				if(angle > angleThreshold){
					nails.remove(index);
					count++;
					removed=true;
					break;
				}
			}
		} while(removed);
		
		return count;
	}
	
	/**
	 * Creates a new transition between the source and target.
	 * These must exist in the template
	 * @param sourceID The source location
	 * @param targetID The target location
	 * @param labels The labels for the transition
	 * @param nails A list of nails/positions, to change the apperance of the transition
	 */
	public Transition(Automaton automaton, Location source, Location destination) {
		this.source = source;
		this.source.addOutgoingTransition(this);
		this.destination = destination;
		this.destination.addIncomingTransition(this);
		this.automaton = automaton;
		automaton.addTransition(this);
	}
	
	public Transition(Automaton automaton, Location source) {
		this(automaton, source, null);
	}

	
	public Transition(Automaton automaton, Element transitionElement) {
		this.automaton = automaton;
		String sourceString = transitionElement.getChild("source").getAttributeValue("ref");
		Matcher matcher = locationIdRegExPattern.matcher(sourceString);
		boolean dbg_matchresult = matcher.find();
		assert dbg_matchresult;
		source = automaton.id_locationMap.get("id" + matcher.group(1));
		source.addOutgoingTransition(this);
		String targetString = transitionElement.getChild("target").getAttributeValue("ref");
		matcher = locationIdRegExPattern.matcher(targetString);
		dbg_matchresult = matcher.find();
		assert dbg_matchresult;
		destination = automaton.id_locationMap.get("id" + matcher.group(1));
		destination.addIncomingTransition(this);
		color = ColorUtil.findColor(transitionElement);
		@SuppressWarnings("unchecked")
		List<Element> children = transitionElement.getChildren();
		for(Element child: children){
			if(child.getName().equals("nail")){
				nails.add(new Nail(child));
			}else if(child.getName().equals("label")){
				if("assignment".equals(child.getAttributeValue("kind"))){
					update=new Update(child);
				}
				if("select".equals(child.getAttributeValue("kind"))){
					select = new Select(child);
				}
				if("guard".equals(child.getAttributeValue("kind"))){
					guard = new Guard(child);
				}
				if("synchronisation".equals(child.getAttributeValue("kind")) && child.getText() != ""){
					sync = new Synchronization(child);
				}
				if("probability".equals(child.getAttributeValue("kind")) && child.getText() != ""){
					setProb(new Probability(child));
				}
			}

		}
	}

	public Location getSource() {
		return source;
	}

	public void setSource(Location source) {
		this.source = source;
	}

	public Location getTarget() {
		return destination;
	}

	public void setTarget(Location destination) {
		this.destination = destination;
	}

	public List<Nail> getNails() {
		return nails;
	}

	public void setNails(List<Nail> list) {
		this.nails = list;
	}
	
	public void addNail(Nail nail){
		this.nails.add(nail);
	}
	
	public Select getSelect() {
		return select;
	}
	public void setSelect(Select select){
		this.select = select;
	}
	
	public void addSelect(Select select){
		if(this.select==null)
			this.select = new Select(select);
		else
			this.select.add(select);
	}

	
	public Guard getGuard() {
		return guard;
	}
	public void setGuard(Guard guard){
		this.guard = guard;
	}

	public Update getUpdate() {
		return update;
	}

	public void setUpdate(Update update){
		this.update = update;
	}
	
	public void addUpdate(Update update){
		if(this.update==null)
			this.update = new Update(update);
		else
			this.update.add(update);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Creates an XML Element object corresponding to the Transition object
	 * @return XML Element
	 */
	Element generateXMLElement(){
		
		Element result = new Element("transition");
		/************ TIGA specific ********/
		if(!isControllable())
			result.setAttribute("controllable", "false");
		/************ END ********/
			
		Element sourceElement = new Element("source");
		sourceElement.setAttribute("ref", source.getUniqueIdString());
		result.addContent(sourceElement);
		
		Element targetElement = new Element("target");
		targetElement.setAttribute("ref", destination.getUniqueIdString());
		result.addContent(targetElement);
		if(select != null)
			result.addContent(this.select.generateXMLElement());
		if(guard != null)
			result.addContent(this.guard.generateXMLElement());
		if(getSync() != null) 
			result.addContent(this.getSync().generateXMLElement());
		if(update!=null)
			result.addContent(this.update.generateXMLElement());
		if(prob!=null)
			result.addContent(this.prob.generateXMLElement());
		if (color!=null)
			result.setAttribute("color", ColorUtil.toHexString(color));

		for (Nail nail : nails) {
			result.addContent(nail.generateXMLElement());
		}
		
		return result;		
	}
	public void setSync(Synchronization sync) {
		this.sync = sync;
	}
	public Synchronization getSync() {
		return sync;
	}


	public void addUpdate(String updateString) {
		this.addUpdate(new Update(updateString));
	}


	public void setGuard(String string) {
		this.setGuard(new Guard(string));
	}

	/************ TIGA SPECIFIC ***************/
	
	private boolean controllable = true;
	public boolean isControllable() {
		return controllable;
	}


	public void setControllable(boolean controllable) {
		this.controllable = controllable;
	}

	public Probability getProb() {
		return prob;
	}

	public void setProb(Probability prob) {
		this.prob = prob;
	}
	
	public void setProb(int prob) {
		this.prob = new Probability(prob);
	}	
}
