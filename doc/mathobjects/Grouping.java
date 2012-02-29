package doc.mathobjects;

import java.awt.Rectangle;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JOptionPane;

import doc.Document;
import doc.Page;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;

public class Grouping extends MathObject implements MathObjectContainer{

	private Vector<MathObject> objects;
	private Vector<DecimalRectangle> objectBounds;

	public static final String STORE_IN_DATABASE = "Store in Database", BRING_TO_LEFT = "Bring All to Left",
			BRING_TO_RIGHT = "Bring All to Right", BRING_TO_TOP = "Bring All to Top",
			BRING_TO_BOTTOM = "Bring All to Bottom", STRETCH_HORIZONTALLY = "Stretch all Horizontally",
			STRETCH_VERTICALLY = "Stretch all Vertically";

	public Grouping(){
		objects = new Vector<MathObject>();
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public Grouping(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
		objects = new Vector<MathObject>();
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public Grouping(MathObjectContainer p){
		super(p);
		objects = new Vector<MathObject>();
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public void addGroupAttributes() {
	}

	public void addGroupActions(){
		addAction(MathObject.MAKE_INTO_PROBLEM);
		addAction(BRING_TO_LEFT);
		addAction(BRING_TO_RIGHT);
		addAction(BRING_TO_TOP);
		addAction(BRING_TO_BOTTOM);
		addAction(STRETCH_HORIZONTALLY);
		addAction(STRETCH_VERTICALLY);
	}
	
	public boolean containsProblemNumber(){
		for ( MathObject mObj : getObjects()){
			if (mObj instanceof ProblemNumberObject){
				return true;
			}
			else if ( mObj instanceof Grouping){
				if ( ((Grouping)mObj).containsProblemNumber()){
					return true;
				}
			}
		}
		return false;
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

	@Override
	public void performSpecialObjectAction(String s) {
		if (s.equals(MathObject.MAKE_INTO_PROBLEM)){
			for ( MathObject mObj : getObjects()){
				if ( mObj instanceof ProblemGenerator || mObj instanceof GeneratedProblem){
					JOptionPane.showMessageDialog(null,
							"Problems cannot contain other problems. If you would like to use\n" +
							"some of the objects from a previous problem in a new one, try\n" +
							"copying the old problem and using the \"Remove Problem\" option to\n" +
							"convert it to raw objects. Those objects can then be used to create\n" +
							"new problems.",
							"Error Making Problem",
							JOptionPane.WARNING_MESSAGE);
					setActionCancelled(true);
					return;
				}
			}
			VariableValueInsertionProblem newProblem = new VariableValueInsertionProblem(getParentContainer(), getxPos(),getyPos(),
					getWidth(), getHeight());
			getParentContainer().removeObject(this);
			getParentContainer().addObject(newProblem);
			newProblem.setObjects(getObjects());
			newProblem.setObjectBounds(getObjectBounds());

			//this group might be the temporary one in the docViewerPanel, so just reset it
			this.getParentContainer().getParentDoc().getDocViewerPanel().resetTempGroup();
			this.getParentContainer().getParentDoc().getDocViewerPanel().setFocusedObject(newProblem);

		}
		else if (s.equals(BRING_TO_LEFT)){
			for (MathObject mObj  : getObjects()){
				mObj.setxPos(getxPos());
			}
			adjustSizeToFixChildren();
		}
		else if (s.equals(BRING_TO_TOP)){
			for (MathObject mObj  : getObjects()){
				mObj.setyPos(getyPos());
			}
			adjustSizeToFixChildren();
		}
		else if (s.equals(BRING_TO_RIGHT)){
			for (MathObject mObj  : getObjects()){
				mObj.setxPos(getxPos() + getWidth() - mObj.getWidth());
			}
			adjustSizeToFixChildren();
		}
		else if (s.equals(BRING_TO_BOTTOM)){
			for (MathObject mObj  : getObjects()){
				mObj.setyPos(getyPos() + getHeight() - mObj.getHeight());
			}
			adjustSizeToFixChildren();
		}
		else if (s.equals(STRETCH_VERTICALLY)){
			for (MathObject mObj  : getObjects()){
				mObj.setHeight(this.getHeight());
				mObj.setyPos(this.getyPos());
			}
			adjustSizeToFixChildren();
		}
		else if (s.equals(STRETCH_HORIZONTALLY)){
			for (MathObject mObj  : getObjects()){
				mObj.setWidth(this.getWidth());
				mObj.setxPos(this.getxPos());
			}
			adjustSizeToFixChildren();
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

	@Override
	public Grouping clone() {
		// this creation method allows for subclasses of grouping to use this clone method
		Grouping o = (Grouping) newInstanceWithType(getType());
		o.setParentContainer(getParentContainer());
		o.removeAllAttributes();
		for ( MathObjectAttribute mAtt : getAttributes()){
			o.addAttribute( mAtt.clone());
		}
		o.removeAllLists();
		for ( ListAttribute list : getLists()){
			o.addList(list.clone());
		}
		for ( MathObject mObj : getObjects()){
			mObj.setParentContainer(null);
			o.addObjectFromPage(mObj.clone());
			mObj.setParentContainer(this);
		}
		return o;
	}
	
	public MathObject getObjectWithAnswer(){
		// this creation method allows for subclasses of grouping to use this clone method
		Grouping o = (Grouping) newInstanceWithType(getType());
		o.setParentContainer(getParentContainer());
		o.removeAllAttributes();
		for ( MathObjectAttribute mAtt : getAttributes()){
			o.addAttribute( mAtt.clone());
		}
		o.removeAllLists();
		for ( ListAttribute list : getLists()){
			o.addList(list.clone());
		}
		for ( MathObject mObj : getObjects()){
			mObj.setParentContainer(null);
			o.addObjectFromPage(mObj.getObjectWithAnswer());
			mObj.setParentContainer(this);
		}
		return o;
	}

	@Override
	public String getType() {
		return GROUPING;
	}

	public void setObjects(Vector<MathObject> objects) {
		this.objects = objects;
	}

	public Vector<MathObject> getObjects() {
		return objects;
	}

	public boolean addObject(MathObject mObj){
		objects.add(mObj);
		mObj.setParentContainer(this);
		return true;
	}

	public void convertPositionsToPage(){
		//converts objects back to their on page positions
		for (MathObject mathObj : objects){
			mathObj.setxPos(getxPos() + (int) Math.round(mathObj.getxPos()/1000.0 * getWidth()) );
			mathObj.setyPos(getyPos() + (int) Math.round(mathObj.getyPos()/1000.0 * getHeight()) );
			mathObj.setWidth((int) Math.round(mathObj.getWidth()/1000.0 * getWidth()) );
			mathObj.setHeight((int) Math.round(mathObj.getHeight()/1000.0 * getHeight()) );
		}
	}

	public void convertPositionsGroupRelative(){
		for (MathObject mathObj : objects){
			mathObj.setWidth( (int) ((double) mathObj.getWidth()/getWidth() * 1000) );
			mathObj.setHeight( (int) ((double) mathObj.getHeight()/getHeight() * 1000) );
			mathObj.setxPos( (int) ( ((double) mathObj.getxPos() - getxPos())/getWidth() * 1000) );
			mathObj.setyPos( (int) ( ((double) mathObj.getyPos() - getyPos())/getHeight() * 1000) );
		}
	}

	private void adjustSizeToFixChildren(){
		Vector<MathObject> temp = getObjects();
		objects = new Vector<MathObject>();
		objectBounds = new Vector<DecimalRectangle>();
		for (MathObject mObj : temp){
			mObj.setParentContainer(null);
			addObjectFromPage(mObj);
			mObj.setParentContainer(this);
		}

	}

	public boolean addObjectFromPage(MathObject mObj){

		if ( mObj.getParentContainer() != null && getParentContainer() != null 
				&&getParentContainer() != mObj.getParentContainer())
		{// cannot add object from another page
			System.out.println("parent container did not match");
			return false;
		}
		if (objects.size() == 0){
			setWidth(mObj.getWidth());
			setHeight(mObj.getHeight());
			setxPos(mObj.getxPos());
			setyPos(mObj.getyPos());
			
			//positions of objects within Groups are relative to the group origin
			//and saved in a fraction of the total width/height, instead of number of pixels
			objectBounds.add(new DecimalRectangle(0,0,1,1));
//			getParentPage().shiftObjInFrontOfOther(this, mObj);
			objects.add(mObj);
			return true;
		}
		Rectangle objRect = new Rectangle(mObj.getxPos(), mObj.getyPos(), mObj.getWidth(), mObj.getHeight());
		Rectangle groupRect = new Rectangle(getxPos(), getyPos(), getWidth(), getHeight());
		if (groupRect.contains(objRect))
		{//the object fits into the current group rectangle, it must be added and have its position/size adjusted
			//so it is relative to the size of the group
			objectBounds.add(new DecimalRectangle(
					((double) mObj.getxPos() - getxPos())/getWidth(),
					((double) mObj.getyPos() - getyPos())/getHeight(),
					(double)mObj.getWidth()/getWidth(),
					(double)mObj.getHeight()/getHeight() ) );
			objects.add(mObj);
			return true;
		}
		else
		{// the new object is outside of the groups bounds
			
			//need to temporarily store the currently grouped objects
			//to prevent them from being resized with the calls to setWidth
			// which adjusts the size of the child objects as a side effect
			
			//they will be added back after the group is resized to accommodate
			//the new object that was outside of the groups bounds
			Vector<MathObject> oldObjects = objects;
			objects = new Vector<MathObject>();
			objectBounds = new Vector<DecimalRectangle>();
			
			//add additional height or width to the group if the object is outside

			if ( mObj.getxPos() < getxPos()){

				setWidth( getWidth() + getxPos() - mObj.getxPos());
				setxPos(mObj.getxPos());
				
			}
			if (mObj.getxPos() + mObj.getWidth() > getxPos() + getWidth()){
				setWidth( mObj.getxPos() + mObj.getWidth() - getxPos());
			}

			if ( mObj.getyPos() <  getyPos()){
				setHeight( getHeight() +  getyPos() - mObj.getyPos());
				setyPos(mObj.getyPos());
			}
			if (mObj.getyPos() + mObj.getHeight() > getyPos() + getHeight()){
				setHeight( mObj.getyPos() + mObj.getHeight() - getyPos());
			}
			
			for (MathObject mathObj : oldObjects){
				objects.add(mathObj);
				DecimalRectangle temp = new DecimalRectangle(
						((double) mathObj.getxPos() - getxPos())/getWidth(),
						((double) mathObj.getyPos() - getyPos())/getHeight(),
						(double)mathObj.getWidth()/getWidth(),
						(double)mathObj.getHeight()/getHeight() );

				objectBounds.add(temp);
			}
			

			objectBounds.add(new DecimalRectangle(
					((double) mObj.getxPos() - getxPos())/getWidth(),
					((double) mObj.getyPos() - getyPos())/getHeight(),
					(double) mObj.getWidth()/getWidth(),
					(double) mObj.getHeight()/getHeight() ) );
			objects.add(mObj);
			return true;
		}
	}

	public boolean removeObject(MathObject mObj){
		if ( ! objects.contains(mObj)){
			return false;
		}
		objectBounds.remove(objects.indexOf(mObj));
		objects.remove(mObj);
		adjustSizeToFixChildren();
		return true;
	}

	public void setParentContainer(Page p){
		// from superclass MathObject
		parentContainer = p;

		if (getObjects() != null){
			for ( MathObject mObj : getObjects()){
				mObj.setParentContainer(p);
			}
		}
	}

	public void unGroup(){

		MathObject mathObj;
		int size = getObjects().size();
		for (int i = 0; i < size; i++){
			mathObj = getObjects().remove(0);
			getParentContainer().addObject(mathObj);
			mathObj.setParentContainer(getParentContainer());
		}
		//note, this method does not remove the group from the page, it must be done externally
	}

	@Override
	public boolean childObjectsFocusable() {
		return false;
	}

	@Override
	public String exportToXML(){
		String output = "";
		output += "<" + getType() + ">\n";
		for (MathObjectAttribute mAtt : getAttributes()){
			output += mAtt.exportToXML();
		}
		for (ListAttribute lAtt : getLists()){
			output += lAtt.exportToXML();
		}
		for (MathObject mObj : getObjects()){
			output += mObj.exportToXML();
		}
		output += "</" + getType() + ">\n";
		return output;
	}


	@Override
	public boolean isResizable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean removeAllObjects() {
		// TODO Auto-generated method stub
		objects = new Vector<MathObject>();
		objectBounds = new Vector<DecimalRectangle>();
		return true;
	}

	/**
	 * This method is sued to move or resize all of the child object in a group after a corresponding
	 * change to the group is made. This is not a method for adjusting the group size after child
	 * objects have been modified and possibly been moved outside of the group.
	 */
	private void adjustChildrenToGroupChange(){
		int index = 0;
		DecimalRectangle tempBounds;
		for (MathObject mathObj : objects){
			tempBounds = objectBounds.get(index);
			mathObj.setxPos(getxPos() + (int) Math.round(tempBounds.getX() * getWidth()) );
			mathObj.setyPos(getyPos() + (int) Math.round(tempBounds.getY() * getHeight()) );
			mathObj.setWidth((int) Math.round(tempBounds.getWidth() * getWidth()) );
			mathObj.setHeight((int) Math.round(tempBounds.getHeight() * getHeight()) );
			index++;
		}
	}

	@Override
	public void setWidth(int width) {
		getAttributeWithName(WIDTH).setValue(width);
		adjustChildrenToGroupChange();
	}
	
	@Override
	public void setHeight(int height) {
		getAttributeWithName(HEIGHT).setValue(height);
		adjustChildrenToGroupChange();
	}

	@Override
	public void setxPos(int xPos) {
		getAttributeWithName(X_POS).setValue(xPos);
		adjustChildrenToGroupChange();
	}

	@Override
	public void setyPos(int yPos) {
		getAttributeWithName(Y_POS).setValue(yPos);
		adjustChildrenToGroupChange();
	}
	public void setObjectBounds(Vector<DecimalRectangle> objectBounds) {
		this.objectBounds = objectBounds;
	}

	public Vector<DecimalRectangle> getObjectBounds() {
		return objectBounds;
	}

	@Override
	public Document getParentDoc() {
		return getParentContainer().getParentDoc();
	}

	@Override
	protected void addDefaultAttributes() {

	}

	@Override
	public MathObject newInstance() {
		return new Grouping();
	}
}
