package uppaal.declarations;

import org.jdom.Element;

import uppaal.labels.Label;

@Deprecated
public class Channel extends Label implements Comparable<Channel>{
	boolean broadcast;
	String channelName;
	String originalName;
	String array = "";
	
//	public Channel(String name, String originalName){
//		this(name, originalName, false);
//	}
	
	public Channel(String name, String originalName, boolean broadcast){
		this.originalName = originalName;
		this.channelName = name;
		this.broadcast = broadcast;
	}
	
	public Channel(String name, String originalName, String arrayType){
		this(name, originalName, false);
		this.array = arrayType;
	}
	
	public Channel(Element child) {
		super(child);
	}

	//	public boolean isBroadcast() {
//		return broadcast;
//	}
//
//	public void setBroadcast(boolean broadcast) {
//		this.broadcast = broadcast;
//	}
//
	public String getIdentifier() {
		if (array == "")
			return channelName;
		else 
			return channelName + "[" + array + "]";
	}

//	public void setId(String id) {
//		this.id = id;
//	}
	
	public String toString(){
		if (broadcast){
			return "broadcast chan " + channelName + ";";
		} else {
			if (array == "")
				return "chan " + channelName + "; //" + originalName;
			else
				return "chan " + channelName + "[" + array + "]; //" + originalName;
		}
	}

	@Override
	public int compareTo(Channel o) {
		return this.getIdentifier().compareTo(o.getIdentifier());
	}
}
