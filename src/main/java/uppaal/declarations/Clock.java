package uppaal.declarations;

@Deprecated
public class Clock {
	String id;
	
	public Clock(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString(){
		return "clock " + id + ";";
	}
}
