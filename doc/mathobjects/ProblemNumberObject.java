package doc.mathobjects;

import doc.Page;
import doc.attributes.AttributeException;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class ProblemNumberObject extends TextObject {

	public static final String NUMBER = "number";
	
	public ProblemNumberObject(){
		this(null);
	}
	
	public ProblemNumberObject(MathObjectContainer p){
		this(p, 1, 1, 1, 1, 12);
	}

	public ProblemNumberObject(MathObjectContainer p, int x, int y, int width, int height, int i){
		super(p, x, y, width, height, 12, i + ".");
		getAttributeWithName(TextObject.FONT_SIZE).setUserEditable(false);
		getAttributeWithName(TextObject.TEXT).setUserEditable(false);
		getAttributeWithName(TextObject.SHOW_BOX).setUserEditable(false);
		getAttributeWithName(TextObject.ALIGNMENT).setUserEditable(false);
		getAttributeWithName(TextObject.ALIGNMENT).setValue(TextObject.RIGHT);
		removeAction(MathObject.MAKE_SQUARE);
		addAttribute(new IntegerAttribute(NUMBER, i, false));
	}
	
	@Override
	public String getType() {
		return PROBLEM_NUMBER_OBJECT;
	}
	
	@Override
	public boolean setAttributeValue(String n, Object o) throws AttributeException{
		if ( n.equals(NUMBER)){
			if (o instanceof Integer){
				setAttributeValue(TEXT, o + ".");
			}
			else{
				return false;
			}
		}
		getAttributeWithName(n).setValue(o);
		return true;
	}
	
	public MathObject newInstance(){
		return new ProblemNumberObject();
	}
}