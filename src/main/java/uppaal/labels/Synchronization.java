package uppaal.labels;

import org.jdom.Element;



public class Synchronization extends Label {
	String channel;
	public enum SyncType { RECEIVER, INITIATOR }
	
	public Synchronization(String channel, SyncType type) {
		this(channel, type, 0,0);
	}

	private SyncType syncType;
	
	public SyncType getSyncType() {
		return syncType;
	}

	public void setSyncType(SyncType syncType) {
		this.syncType = syncType;
	}
	public String getChannelName() {
		return this.channel;
	}

	public Synchronization(String channel, SyncType type, int x, int y) {
		super(x, y);
		this.channel = channel;
		syncType = type;
	}

	public Synchronization(Element child) {
		super(child);
		this.channel = child.getText().substring(0, child.getText().length()-1);
		if("?".equals(child.getText().substring(child.getText().length()-1, child.getText().length()))){
			syncType = SyncType.RECEIVER;
		} else {
			syncType = SyncType.INITIATOR;
		}
	}

	/**
	 * Creates an XML Element object corresponding to the Label object
	 * @return XML Element
	 */
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "synchronisation");
		result.addContent(this.toString());
		return result;
	}

	@Override
	public String toString() {
		return channel + (syncType==SyncType.RECEIVER?"?":"!");
	}
}
