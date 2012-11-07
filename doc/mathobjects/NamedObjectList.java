package doc.mathobjects;

import java.util.Vector;

public class NamedObjectList<T extends MathObject>{
	
	private Vector<T> objects;
	private String listName;
	
	public NamedObjectList(String listName){
		this.listName = listName;
		objects = new Vector<T>();
	}
	
	public Vector<T> getObjects(){
		return objects;
	}
	
	public void add(T obj){
		objects.add(obj);
	}
	
	public String getName(){
		return listName;
	}
}
