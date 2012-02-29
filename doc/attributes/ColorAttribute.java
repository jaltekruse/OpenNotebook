package doc.attributes;

import java.awt.Color;
import java.util.StringTokenizer;


public class ColorAttribute extends MathObjectAttribute<Color> {

	public ColorAttribute(String n) {
		super(n);
	}
	
	public ColorAttribute(String n,  boolean userEditable) {
		super(n, userEditable);
	}
	
	public ColorAttribute(String n,  boolean userEditable, boolean studentEditable) {
		super(n);
	}
	
	public ColorAttribute(String n, Color c) {
		super(n, c);
	}
	
	public ColorAttribute(String n, Color c,  boolean userEditable) {
		super(n, c, userEditable);
	}
	
	public ColorAttribute(String n, Color c,  boolean userEditable, boolean studentEditable) {
		super(n, c, userEditable, studentEditable);
	}

	@Override
	public String getType() {
		return COLOR_ATTRIBUTE;
	}
	
	@Override
	public String exportToXML(){
		String s = "<" + getType() + " name=\"" + getName()
		+ "\" value=\"";
		if (getValue() == null){
			s += "null";
		}
		else{
			s += getValue().getRed() + "," + getValue().getGreen() +
			"," + getValue().getBlue();
		}
		s += "\"/>\n";
		return s;
	}

	@Override
	public Color readValueFromString(String s) throws AttributeException {
		// TODO Auto-generated method stub
		try{
			if ( s.equals("null") ){
				return null;
			}
			StringTokenizer st = new StringTokenizer(s, ",");
			
			int red = Integer.parseInt(st.nextToken());
			int green = Integer.parseInt(st.nextToken());
			int blue = Integer.parseInt(st.nextToken());
			return new Color(red, green, blue);
		} catch (Exception e){
			throw new AttributeException("error setting color");
		}
	}

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		setValue(null);
	}
}
