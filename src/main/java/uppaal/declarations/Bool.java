package uppaal.declarations;

@Deprecated
public class Bool {
	String id;
	boolean value;
	boolean constant;
	
	public Bool(String id){
		this(id, false, false);
	}
	
	public Bool(String id, boolean value, boolean constant){
		this.id = id;
		this.value = value;
		this.constant = constant;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		if (constant){
			return "const bool " + id + " = " + value + ";";
		} else {
			return "bool " + id + " = " + value + ";";
		}
	}
}
