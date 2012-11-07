package doc.mathobjects;

import doc.Page;
import doc.attributes.AttributeException;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.IntegerAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.attributes.UUIDAttribute;
import doc_gui.attribute_panels.ObjectPropertiesFrame;

public class AnswerBoxObject extends MathObject implements Gradeable {

	public static final String FONT_SIZE = "font size", STUDENT_ANSWER = "student answer", CORRECT_ANSWERS = "correct answers",
			ANSWER_TYPE = "answer type", EXPRESSION = "expression", PLAIN_TEXT = "plain text", NUMBER = "number",
			LETTER = "letter", WORD = "word", TOTAL_POINTS = "total points", STUDENT_SCORE = "student score", GRADE = "Grade";
	
	public static final String[] ANSWER_TYPES = {EXPRESSION, PLAIN_TEXT, NUMBER, LETTER, WORD};
	
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
		this.addAction(GRADE);
		addAttribute(new StringAttribute(STUDENT_ANSWER, "", true, true));
		addList(new ListAttribute<StringAttribute>(CORRECT_ANSWERS,
				new StringAttribute(""), 20, true, false));
		addAttribute(new IntegerAttribute(FONT_SIZE, 12, 1, 50, true, false));
		addAttribute(new IntegerAttribute(TOTAL_POINTS, 5, 1, 100, true, false));
		addAttribute(new IntegerAttribute(STUDENT_SCORE, 5, 1, 100, true, false));
		addAttribute(new EnumeratedAttribute(ANSWER_TYPE, PLAIN_TEXT, ANSWER_TYPES));
	}
	
	public int[] grade(){
		int[] ret = new int[2];
		if (getAttributeValue(ANSWER_TYPE).equals(PLAIN_TEXT)){
			if (getListWithName(CORRECT_ANSWERS).contains(getAttributeValue(STUDENT_ANSWER))){
				System.out.println("contains true");
				ret[0] = getTotalPoints();
				ret[1] = getTotalPoints();
				getAttributeWithName(STUDENT_SCORE).setValue(getTotalPoints());
				return ret;
			}
			getAttributeWithName(STUDENT_SCORE).setValue(0);
			ret[0] = 0;
			ret[1] = getTotalPoints();
		}
		return null;
	}
	
	public void setFontSize(int fontSize) throws AttributeException {
		setAttributeValue("fontSize", fontSize);
	}

	public int getTotalPoints(){
		return (Integer) getAttributeValue(TOTAL_POINTS);
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
	
	public ListAttribute<StringAttribute> getCorrectAnswers(){
		return (ListAttribute<StringAttribute>)getListWithName(CORRECT_ANSWERS);
	}
	
	public void performSpecialObjectAction(String s){
		if (s.equals(GRADE)){
			this.grade();
		}
	}

	public void addCorrectAnswer(String s) throws AttributeException{
		if ( ! getListWithName(CORRECT_ANSWERS).isEmpty()){
			StringAttribute lastVal = (StringAttribute) getListWithName(CORRECT_ANSWERS).getLastValue();
			if (lastVal != null && lastVal.getValue().equals("")){
				lastVal.setValue(s);
				return;
			}
		}
		getListWithName(CORRECT_ANSWERS).addValueWithString(s);
	}

	@Override
	public MathObject newInstance() {
		return new AnswerBoxObject();
	}
	
	public AnswerBoxObject getObjectWithAnswer(){
		AnswerBoxObject o = (AnswerBoxObject) this.clone();
		try {
			String str = "";
			for ( StringAttribute strAtt : o.getCorrectAnswers().getValues()){
				str += strAtt.getValue() + "; ";
			}
			o.setStudentAnswer(str);
		} catch (AttributeException e) {
			// should not throw an error, just setting a string attribute
			e.printStackTrace();
		}
		return o;
	}
}
