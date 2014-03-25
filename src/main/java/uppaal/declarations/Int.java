package uppaal.declarations;

@Deprecated
public class Int {
	String id;
	int value;
	boolean constant;
	
	public Int(String id){
		this(id, 0, false);
	}
	
	public Int(String id, int value){
		this(id, value, false);
	}
	
	public Int(String id, int value, boolean constant){
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}
	
	@Override
	public String toString(){
		if (constant){
			return "const int " + id + " = " + value + ";";
		} else {
			return "int " + id + " = " + value + ";";
		}
	}
}
