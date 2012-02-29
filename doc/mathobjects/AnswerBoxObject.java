package doc.mathobjects;

import doc.Page;
import doc.attributes.AttributeException;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;

public class AnswerBoxObject extends MathObject {

	public static final String FONT_SIZE = "font size";
	public static final String STUDENT_ANSWER = "student answer", CORRECT_ANSWER = "correct answer";
	
	public AnswerBoxObject(Page p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
	}
	
	public AnswerBoxObject(MathObjectContainer p){
		super(p);
	}

	public AnswerBoxObject() {}
	
	public boolean isStudentSelectable(){
		return true;
	}

	@Override
	public void addDefaultAttributes() {
		addAttribute(new StringAttribute(STUDENT_ANSWER, "", true, true));
		addAttribute(new StringAttribute(CORRECT_ANSWER, true, false));
		addAttribute(new IntegerAttribute(FONT_SIZE, 12, 1, 50, true, false));
	}

	public void setFontSize(int fontSize) throws AttributeException {
		setAttributeValue("fontSize", fontSize);
	}

	public int getFontSize() {
		return (Integer) getAttributeValue(FONT_SIZE);
	}

	@Override
	public String getType() {
		return ANSWER_BOX_OBJ;
	}
	
	public String getStudentAnswer(){
		return (String) getAttributeValue(STUDENT_ANSWER);
	}
	
	public void setStudentAnswer(String s) throws AttributeException{
		setAttributeValue(STUDENT_ANSWER, s);
	}
	
	public String getCorrectAnswer(){
		return ((StringAttribute)getAttributeWithName(CORRECT_ANSWER)).getValue();
	}

	public void setCorrectAnswer(String s) throws AttributeException{
		setAttributeValue(CORRECT_ANSWER, s);
	}

	@Override
	public MathObject newInstance() {
		return new AnswerBoxObject();
	}
	
	public MathObject getObjectWithAnswer(){
		AnswerBoxObject o = (AnswerBoxObject) this.clone();
		try {
			o.setStudentAnswer(o.getCorrectAnswer());
		} catch (AttributeException e) {
			// should not throw an error, just setting a string attribute
			e.printStackTrace();
		}
		return o;
	}
}
