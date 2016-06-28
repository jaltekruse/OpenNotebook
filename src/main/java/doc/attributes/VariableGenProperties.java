package doc.attributes;

import java.util.Vector;

public class VariableGenProperties {

	private Vector<MathObjectAttribute> attributes;
	private static final String[] numberTypes = {"Integer", "Decimal", "Fraction"};
	private static final String[] generationMethods = {"Random", "Expression"};
	
	private static final String RANDOM = "random", EXPRESSION = "expression", MINIMUM = "mininimum",
			MAX = "maximum", ROUND_TO = "round to", GENERATION_METHOD = "Generation Method",
			NUMBER_TYPE = "number type", 
			NUMBERS_TO_EXCLUDE = "Numbers to exclude(separate with commas)";
	
	
	public VariableGenProperties(){
		attributes = new Vector<>();
	}
	
	private void addAttributes(){
		addAttribute(new EnumeratedAttribute(GENERATION_METHOD, generationMethods));
	}
	

	public MathObjectAttribute getAttributeWithName(String n){
		for (MathObjectAttribute mAtt : attributes){
			if (mAtt.getName().equals(n)){
				return mAtt;
			}
		}
		return null;
	}
	
	public Object getAttributeValue(String n){
		for (MathObjectAttribute mAtt : attributes){
			if (mAtt.getName().equals(n)){
				return mAtt.getValue();
			}
		}
		return null;
	}
	
	public void setAttributeValueWithString(String s, String val) throws AttributeException{
		setAttributeValue(s, getAttributeWithName(s).readValueFromString(val));
	}
	
	public void setAttributeValue(String n, Object o) throws AttributeException{
		getAttributeWithName(n).setValue(o);
	}
	
	public boolean addAttribute(MathObjectAttribute a){
		if (getAttributeWithName(a.getName()) == null){
			attributes.add(a);
			return true;
		}
		return false;
	}
	
	public void removeAttribute(MathObjectAttribute a){
		attributes.remove(a);
	}
	
	public void removeAllAttributes(){
		attributes = new Vector<>();
	}

}
