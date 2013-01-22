package doc.attributes;

import doc_gui.graph.Selection;

public class SelectionAttribute extends MathObjectAttribute<Selection> {

	public static final String SELECTION = "selection";
	
	public SelectionAttribute(String n, Selection val, boolean userEditable) {
		super(n, val, userEditable);
	}

	@Override
	public Selection readValueFromString(String s) throws AttributeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return SELECTION;
	}

}
