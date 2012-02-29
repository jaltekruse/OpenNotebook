package curriculum;

import java.util.Vector;

import doc.Document;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.ProblemGenerator;

public class Section {

	public static final String CHAPTER_NAME = "subjectName";
	public static final String AUTHOR = "author";
	public static final String MAIN_TOPICS = "mainTopics";
	public static final String CREATION_DATE = "creationDate";
	public static final String LAST_MODIFIED = "lastModfied";
	
	private Vector<Document> docs;
	
	private Vector<ProblemGenerator> problems;

	private Vector<MathObjectAttribute> attributes;
	
	public Section(String name){
		attributes = new Vector<MathObjectAttribute>();
		docs = new Vector<Document>();
		addAttributes();
		setName(name);
	}
	
	private void addAttributes(){
		attributes.add(new StringAttribute(CHAPTER_NAME));
	}
	
	public void addChapter(Document doc){
		docs.add(doc);
	}
	
	public Vector<Document> getChapters(){
		return docs;
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
