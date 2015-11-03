package doc.attributes;

import java.awt.Color;
import java.util.StringTokenizer;

import doc_gui.graph.Selection;

public class SelectionAttribute extends MathObjectAttribute<Selection> {
	
	public SelectionAttribute(String n, Selection val, boolean userEditable) {
		super(n, val, userEditable);
	}

	@Override
	public String exportToXML(){
		String s = "<" + getType() + " name=\"" + getName()
		+ "\" value=\"";
		if (getValue() == null){
			s += "null";
		}
		else{
			s += getValue().getStart() + "," + getValue().getEnd();
		}
		s += "\"/>\n";
		return s;
	}

	@Override
	public Selection readValueFromString(String s) throws AttributeException {
		try{
			if ( s.equals("null") ){
				return null;
			}
			StringTokenizer st = new StringTokenizer(s, ",");
			
			double start = Double.parseDouble(st.nextToken());
			double end = Double.parseDouble(st.nextToken());
			return new Selection(start, end);
		} catch (Exception e){
			throw new AttributeException("error setting Selection");
		}
	}

	@Override
	public void resetValue() {

	}

	@Override
	public String getType() {
		return SELECTION;
	}
	
	@Override
	public String toString(){
		return getValue().getStart() + "," + getValue().getEnd();
	}

}
