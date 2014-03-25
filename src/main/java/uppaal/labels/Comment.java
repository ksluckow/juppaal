package uppaal.labels;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;


public class Comment extends Label {
	List<String> comments = new LinkedList<String>();

	public Comment(Comment comment) {
		comments = new LinkedList<String>();
		setPositioned(true);
		comments.add(comment.toString());
	}

	public Comment(Element commentElement) {
		super(commentElement);
		String[] cmts = commentElement.getText().split("\n");
		for(String cmt : cmts)
			comments.add(cmt);
		setPositioned(true);
	}

	public Comment() {
	}
	
	public Comment(String string) {
		comments.add(string);
	}

	public void addCommentLine(String line){
		comments.add(line);
	}
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.setAttribute("kind", "comments");
		result.addContent(this.toString());
		return result;
	}
	
	public List<String> getCommentLines(){
		return comments;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String comment : comments){
			sb.append(comment+"\n");
		}
		return sb.toString();
	}
}
