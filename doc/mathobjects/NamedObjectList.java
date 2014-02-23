package doc.mathobjects;

import java.util.Iterator;
import java.util.Vector;

public class NamedObjectList implements Iterable<MathObject>{
	
	private Vector<MathObject> objects;
	private String listName;
	private MathObject parentObject;
	
	public NamedObjectList(String listName){
		this.listName = listName;
		setObjects(new Vector<MathObject>());
	}
	
	public Vector<MathObject> getObjects(){
		return objects;
	}
	
	public void add(MathObject obj){
		getObjects().add(obj);
	}
	
	public String getName(){
		return listName;
	}

	@Override
	public Iterator<MathObject> iterator() {
		return getObjects().iterator();
	}

	public MathObject getParentObject() {
		return parentObject;
	}

	public void setParentObject(MathObject parentObject) {
		this.parentObject = parentObject;
	}

	public void setObjects(Vector<MathObject> objects) {
		this.objects = objects;
	}
}
