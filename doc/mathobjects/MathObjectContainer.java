package doc.mathobjects;

import java.util.Vector;

import doc.Document;
import doc.Page;

public interface MathObjectContainer {

	public int getWidth();
	
	public int getHeight();
	
	public Vector<MathObject> getObjects();
	
	public boolean addObject(MathObject mObj);
	
	public boolean removeObject(MathObject mObj);
	
	public Document getParentDoc();
	
	public Page getParentPage();
	
	public boolean removeAllObjects();
	
	/**
	 * Returns true of this container can be resized. Objects that are contained in
	 * other objects must have their sizes scaled relative to their parents, this is a
	 * flag to determine if this container requires its child objects to be stored in this
	 * manner.
	 * 
	 * @return
	 */
	public boolean isResizable();
	
	/**
	 * Some containers such as groups, will not allow their child objects to gain focus
	 * as long as they belong to the container. Other containers, such as pages and 
	 * @return - if child objects are allowed to gain focus
	 */
	public boolean childObjectsFocusable();
}
