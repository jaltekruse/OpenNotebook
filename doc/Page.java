/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
 */

package doc;

import java.awt.Rectangle;
import java.util.UUID;
import java.util.Vector;

import doc.attributes.MathObjectAttribute;
import doc.mathobjects.*;

public class Page extends MathObject implements MathObjectContainer{

	protected Vector<MathObject> objects;

	private Document parentDoc;

	public Page(Document doc){
		setObjects(new Vector<MathObject>());
		setParentDoc(doc);
	}

	public void setObjects(Vector<MathObject> objects) {
		this.objects = objects;
	}

	public boolean removeObject(MathObject mObj){

		boolean success = objects.remove(mObj);
		if ( success){
			if (mObj instanceof ProblemNumberObject ||
					(mObj instanceof Grouping &&
					((Grouping)mObj).containsProblemNumber()) ){
				getParentDoc().refactorPageNumbers();
			}
		}
		return success;
	}

	public Vector<MathObject> getObjects() {
		return objects;
	}

	public boolean objectIDInUse(UUID id){
		for ( MathObject mObj : getObjects()){
			if ( mObj.getObjectID().equals(id)){
				return true;
			}
			if ( mObj instanceof Grouping){
				if ( (( Grouping )mObj).objectIDInUse(id)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Add a MathObject to this page.
	 * @param mObj - object to add
	 * @return true if add was successful, if object did not fit in printable area it is not added
	 */
	public boolean addObject(MathObject mObj){

		//check to make sure the object will fit on the page, inside of the margins
		if ( getObjects().contains(mObj)){
			return true;
		}
		Rectangle printablePage = new Rectangle(0, 0, getWidth(),
				getHeight());
		
		//		Rectangle objRect = new Rectangle(mObj.getxPos(), mObj.getyPos(), mObj.getWidth(), mObj.getHeight());
		//		if (printablePage.contains(objRect)){
		//		objects.add(mObj);
		//			return true;
		//		}

		while (getParentDoc().objectIDInUse(mObj.getObjectID()))
		{// randomly assign the object a new ID until it is unique within the document
			mObj.setObjectID( UUID.randomUUID() );
		}
		if ( ! objects.contains(mObj)){
			objects.add(mObj);
			mObj.setParentContainer(this);
			return true;
		}

		return false;
		//throw error? the object would not fit within the printable page with the current position and dimensions
	}

	public Page clone(){
		Page newPage = new Page(getParentDoc());
		MathObject mObj;
		MathObject clone;
		for ( int i = 0; i < objects.size() ; i++){
			mObj = objects.get(i);
			clone = mObj.clone();
			if ( mObj == getParentDoc().getLastFocused()){
				getParentDoc().setLastFocused(clone);
			}
			newPage.addObject(clone);
		}
		return newPage;
	}

  @Override
  public MathObject newInstance() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void bringObjectToFront(MathObject mObj){
		objects.remove(mObj);
		objects.add(mObj);
	}

	public void shiftObjInFrontOfOther(MathObject toMove, MathObject toBeBehind){
		objects.remove(toMove);
		objects.add(objects.indexOf(toBeBehind), toMove);
	}

	public boolean objInFrontOfOther(MathObject obj1, MathObject obj2){
		if (objects.indexOf(obj1) > objects.indexOf(obj2)){
			return true;
		}
		return false;
	}

	public void sendObjectForward(MathObject mObj){
		int index = objects.lastIndexOf(mObj);
		Rectangle objRect = new Rectangle(mObj.getxPos(), mObj.getyPos(), mObj.getWidth(), mObj.getHeight());
		MathObject mObj2;
		Rectangle objRect2;
		for (int i = index + 1; i < objects.size(); i++){
			mObj2 = objects.get(i);
			objRect2 = new Rectangle(mObj2.getxPos(), mObj2.getyPos(), mObj2.getWidth(), mObj2.getHeight());
			if (objRect.intersects(objRect2)){
				objects.remove(mObj);
				objects.add(i, mObj);
				break;
			}
		}
	}

	public boolean objectContainedBelow(MathObject o){
		for (MathObject mObj : objects){
			if ( o == mObj){
				return true;
			}
			if ( mObj instanceof Grouping){
				if ( ((Grouping)mObj).objectContainedBelow(o)){
					return true;
				}
			}
		}
		return false;
	}

	public void sendObjectBackward(MathObject mObj){
		int index = objects.lastIndexOf(mObj);
		Rectangle objRect = new Rectangle(mObj.getxPos(), mObj.getyPos(), mObj.getWidth(), mObj.getHeight());
		MathObject mObj2;
		Rectangle objRect2;
		for (int i = index - 1; i >= 0; i--){
			mObj2 = objects.get(i);
			objRect2 = new Rectangle(mObj2.getxPos(), mObj2.getyPos(), mObj2.getWidth(), mObj2.getHeight());
			if (objRect.intersects(objRect2)){
				objects.remove(mObj);
				objects.add(i, mObj);
				break;
			}
		}
	}

	public void bringObjectToBack(MathObject mObj){
		objects.remove(mObj);
		objects.add(0,mObj);
	}

  @Override
  public String getType() {
    return PAGE;
  }

  public String exportToXML(){
		//should store page width and height at document level
		//do not need to allow teachers to mix page orientations
		String output = "";
		output += "<Page>\n";
		for (MathObject mObj : objects){
			output += mObj.exportToXML();
		}
		output += "</Page>\n";
		return output;
	}

	public int getWidth() {
		return getParentDoc().getWidth();
	}

	public int getHeight() {
		return getParentDoc().getHeight();
	}
	public int getxMargin() {
		return getParentDoc().getxMargin();
	}
	public int getyMargin() {
		return getParentDoc().getyMargin();
	}

	public void setParentDoc(Document parentDoc) {
		this.parentDoc = parentDoc;
	}

	public Document getParentDoc() {
		return parentDoc;
	}

	@Override
	public boolean childObjectsFocusable() {
		return true;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public boolean removeAllObjects() {
		objects = new Vector<MathObject>();
		return true;
	}

	@Override
	public Page getParentPage() {
		return null;
	}

}
