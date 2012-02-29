package curriculum;

import java.util.Vector;

import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;

public class Chapter {

	public static final String CHAPTER_NAME = "subjectName";
	public static final String AUTHOR = "author";
	public static final String MAIN_TOPICS = "mainTopics";
	public static final String CREATION_DATE = "creationDate";
	public static final String LAST_MODIFIED = "lastModfied";
	
	private Vector<Section> sections;

	private Vector<MathObjectAttribute> attributes;
	
	public Chapter(String name){
		attributes = new Vector<MathObjectAttribute>();
		sections = new Vector<Section>();
		addAttributes();
		setName(name);
	}
	
	private void addAttributes(){
		attributes.add(new StringAttribute(CHAPTER_NAME));
	}
	
	public void addChapter(Section s){
		sections.add(s);
	}
	
	public Vector<Section> getChapters(){
		return sections;
	}
	
	public void setName(String s){
		getAttributeWithName(CHAPTER_NAME).setValue(s);
	}
	
	public String getName(){
		return ((StringAttribute)getAttributeWithName(CHAPTER_NAME)).getValue();
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
