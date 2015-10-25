package doc.attributes;

import java.util.List;
import java.util.Vector;

import doc.mathobjects.MathObject;

/**
 * Class to associate a name with a list of attributes. All values should 
 * have a connection to one another and the name of the list. Examples include
 * a list of points to graph, or a list steps performed while solving an algebra
 * problem.
 * 
 * @author jason
 *
 */
public class ListAttribute <K extends MathObjectAttribute> {

	public static final String	LIST = "list", 				ENTRY = "entry",	VAL = "v",
								CHILD_NAME = "childName",	NAME = "name",		TYPE = "type";
	
	private String name;
	private K template;
	private int maxSize;
	public static final int MAX_NOT_SET = Integer.MAX_VALUE;
	private Vector<K> values;
	private boolean userEditable, studentEditable;
	private MathObject parentObject;
	
	public ListAttribute(String s, K val){
		this(s,val, MAX_NOT_SET);
	}
	
	public ListAttribute(String s, K val, boolean userEditable){
		this(s, val, MAX_NOT_SET, userEditable);
	}
	
	public ListAttribute(String s, K val, boolean userEditable, boolean studentEditable){
		this(s,val, MAX_NOT_SET, userEditable, studentEditable);
	}
	
	public ListAttribute(String s, K val, int maxSize){
		values = new Vector<K>();
		this.maxSize = maxSize;
		values.add(val);
		template = val;
		setName(s);
		setUserEditable(true);
		setStudentEditable(true);
	}
	
	public ListAttribute(String s, K val, int maxSize, boolean userEditable){
		values = new Vector<K>();
		this.maxSize = maxSize;
		values.add(val);
		template = val;
		setName(s);
		setUserEditable(userEditable);
		setStudentEditable(false);
	}
	
	public ListAttribute(String s, K val, int maxSize, boolean userEditable, boolean studentEditable){
		values = new Vector<K>();
		this.maxSize = maxSize;
		values.add(val);
		template = val;
		setName(s);
		this.setUserEditable(userEditable);
		this.setStudentEditable(studentEditable);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public synchronized void addNewValue() throws AttributeException{
		if ( values.size() == maxSize){
			throw new AttributeException("List can only hold " + maxSize + " items.");
		}
		K newVal = (K) template.clone();
		newVal.resetValue();
		values.add(newVal);
	}
	
	public synchronized void addValueWithString(String s) throws AttributeException{
		if ( values.size() == maxSize){
			throw new AttributeException("List can only hold " + maxSize + " items.");
		}
		K newVal = (K) template.clone();
		newVal.resetValue();
		newVal.setValue(newVal.readValueFromString(s));
		values.add(newVal);
	}
	
	public synchronized ListAttribute<K> clone(){
		ListAttribute<K> newList = new ListAttribute<K>(getName(), template);
		newList.removeAll();
		for ( K mAtt : values){
			newList.addValue( (K) mAtt.clone());
		}
		newList.setStudentEditable(isStudentEditable());
		newList.setUserEditable(isUserEditable());
		newList.setMaxSize(getMaxSize());
		return newList;
		
	}
	
	public synchronized boolean contains(Object obj){
		for ( K val : values){
			if (val.getValue().equals(obj)){
				return true;
			}
		}
		return false;
	}
	
	public String exportToXML(){
		String output = "";
		output += "\t<" + LIST + " " + NAME + "=\"" + getName() + "\" " + TYPE +"=\"" +
		template.getType() + "\" " + CHILD_NAME + "=\"" + template.getName() + "\""+ ">\n";
		for ( K val : values){
			output += "\t\t<" + ENTRY + " " + VAL + "=\"" + val.getValue() + "\"/>\n";  
		}
		output += "</" + LIST + ">";
		return output;
	}
	
	public synchronized void addValue(K value){
		values.add(value);
	}

	public synchronized K getValue(int i){
		return values.get(i);
	}
	
	public boolean isEmpty(){
		return values.isEmpty();
	}
	
	public synchronized K getLastValue(){
		if ( values.isEmpty()){
			return null;
		}
		return values.get(values.size() - 1);
	}
	
	public synchronized boolean removeValue(K value){
		return values.remove(value);
	}
	
	public K removeValue(int i){
		return values.remove(i);
	}
	
	public void removeAll(){
		values.removeAllElements();
	}
	
	public void setValues(Vector<K> values) {
		this.values = values;
	}

	public List<K> getValues() {
		return values;
	}
	
	public void setStudentEditable(boolean studentEditable) {
		this.studentEditable = studentEditable;
	}

	public boolean isStudentEditable() {
		return studentEditable;
	}

	public void setUserEditable(boolean userEditable) {
		this.userEditable = userEditable;
	}

	public boolean isUserEditable() {
		return userEditable;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public MathObject getParentObject() {
		return parentObject;
	}

	public void setParentObject(MathObject parentObject) {
		this.parentObject = parentObject;
	}
}
