package curriculum;

import java.util.Vector;

import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;

public class Book {
	
	public static final String SUBJECT_NAME = "subjectName";
	public static final String AUTHOR = "author";
	public static final String MAIN_TOPICS = "mainTopics";
	public static final String CREATION_DATE = "creationDate";
	public static final String LAST_MODIFIED = "lastModfied";
	
	private Vector<Chapter> chapters;

	private Vector<MathObjectAttribute> attributes;
	
	public Book(String name){
		attributes = new Vector<MathObjectAttribute>();
		chapters = new Vector<Chapter>();
		addAttributes();
		setName(name);
	}
	
	private void addAttributes(){
		attributes.add(new StringAttribute(SUBJECT_NAME));
	}
	
	public void addChapter(Chapter c){
		chapters.add(c);
	}
	
	public Vector<Chapter> getChapters(){
		return chapters;
	}
	
	public void setName(String s){
		getAttributeWithName(SUBJECT_NAME).setValue(s);
	}
	
	public String getName(){
		return ((StringAttribute)getAttributeWithName(SUBJECT_NAME)).getValue();
	}
	
	public MathObjectAttribute getAttributeWithName(String n){
		for (MathObjectAttribute mAtt : attributes){
			if (mAtt.getName().equals(n)){
				return mAtt;
			}
		}
		return null;
	}

}
