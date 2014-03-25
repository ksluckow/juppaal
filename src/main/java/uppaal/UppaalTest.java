package uppaal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uppaal.labels.Comment;
import uppaal.labels.Guard;
import uppaal.labels.Select;
import uppaal.labels.Update;

public class UppaalTest {
	Map<String, Location> cloc = new HashMap<String, Location>();

	private Location getPLocation(Automaton product, Location a, Location b){
		String name = a.getName().toString()+"_"+b.getName().toString();
		String idstr = a.getUniqueIdString() + "_" + b.getUniqueIdString();
		Location result = new Location(product, name);
		cloc.put(idstr, result);
		result.getInvariant().conjoin(a.getInvariant());
		result.getInvariant().conjoin(b.getInvariant());
		System.out.println(idstr);
		if(a.getComment()!=null && b.getComment()!=null && b.getComment().equals(a.getComment()))
			result.setComment(new Comment(b.getComment()));
		return result;
	}
	
	
	public void getProduct(){
		NTA nta = new NTA("/tmp/testAut.xml");
		System.out.println("Loaded");
		Automaton spec = nta.getAutomaton("P0");
		Automaton impl = nta.getAutomaton("P2");
		
		Automaton product = new Automaton("product");
		nta.addAutomaton(product);
		
		
		System.out.println("Location product");
		List<Location> iL = impl.getLocations();
		List<Location> iS = spec.getLocations();
		for (Location il: iL){
			for(Location sl: iS){
				getPLocation(product, il, sl);
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
				Transition transition = new Transition(product, cloc.get(source), cloc.get(target));
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
				Transition transition = new Transition(product, cloc.get(source), cloc.get(target));
				transition.setGuard(new Guard(st.getGuard()));
				transition.setSelect(new Select(st.getSelect()));
				transition.setUpdate(new Update(st.getUpdate()));
				System.out.println(transition.getSource().getName() +"    ->    " + transition.getTarget().getName());				
			}
		}
		
		String init = impl.getInit().getUniqueIdString() + "_"+ spec.getInit().getUniqueIdString();
		product.setInit(cloc.get(init));
		Declaration d = new Declaration(spec.getDeclaration());
		d.add(impl.getDeclaration());
		product.setDeclaration(d);
		product.setAutoPositioned(true);
/*		
		for(Transition st: sT){
//			if(st.getSync()!=null) {
//				System.err.println("No handling of syncs yet");;
//				continue;
//			}
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
//			Transition prod = new Transition(product);
		}
*/
		
		System.out.println("writing");
		nta.writeModelToFile("/tmp/out.xml");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		NTA nta = new NTA("/tmp/test.xml");
//		System.out.println("loaded");
//		nta.writeModelToFile("/tmp/test2.xml");
//		System.out.println("written");
		UppaalTest t = new UppaalTest();

		t.getProduct();
		
		System.out.println("done");
		// product construction

	}

}
