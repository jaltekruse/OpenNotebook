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

	private Vector<DecimalRectangle> objectBounds;

	public static final String STORE_IN_DATABASE = "Store in Database", BRING_TO_LEFT = "Bring All to Left",
			BRING_TO_RIGHT = "Bring All to Right", BRING_TO_TOP = "Bring All to Top",
			BRING_TO_BOTTOM = "Bring All to Bottom", STRETCH_HORIZONTALLY = "Stretch all Horizontally",
			STRETCH_VERTICALLY = "Stretch all Vertically", DISTRIBUTE_VERTICALLY = "Distribute Vertically",
			DISTRIBUTE_HORIZONTALLY = "Distribute Horizontally",
			ALIGN_GROUP_VERTICAL_CENTER = "Align to Vertical Group Center",
			ALIGN_GROUP_HORIZONTAL_CENTER = "Align to Hoizontal Group Center",
			OBJECTS = "objects";

	public Grouping(){
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public Grouping(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public Grouping(MathObjectContainer p){
		super(p);
		objectBounds = new Vector<DecimalRectangle>();
		addGroupAttributes();
		addGroupActions();
	}

	public void addGroupAttributes() {
	}

	public void addGroupActions(){
		addAction(MathObject.MAKE_INTO_PROBLEM);
		addAction(DISTRIBUTE_HORIZONTALLY);
		addAction(BRING_TO_LEFT);
		addAction(ALIGN_GROUP_HORIZONTAL_CENTER);
		addAction(BRING_TO_RIGHT);
		addAction(DISTRIBUTE_VERTICALLY);
		addAction(BRING_TO_BOTTOM);
		addAction(ALIGN_GROUP_VERTICAL_CENTER);
		addAction(BRING_TO_TOP);
		addAction(STRETCH_HORIZONTALLY);
		addAction(STRETCH_VERTICALLY);
		addObjectList(new NamedObjectList(OBJECTS));
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

	public void generateNewVersion(){
		Vector<MathObject> groupObjects;
		MathObject mObj;
		int oldSize;
		boolean problemsGenerated = false;
		oldSize = getObjects().size();
		for (int j = 0; j < oldSize; j++) {
			mObj = getObjects().get(j);
			if (mObj instanceof GeneratedProblem) {
				((GeneratedProblem) mObj).generateNewProblem();
				problemsGenerated = true;
				j--;
				oldSize--;
			}
			else if ( mObj instanceof Grouping && ((Grouping)mObj).containsGeneratedProblems()){
				((Grouping)mObj).generateNewVersion();
				problemsGenerated = true;
			}
		}
		if (problemsGenerated){
			adjustSizeToFitChildren();
		}
	}
	
	public boolean containsGeneratedProblems(){
		for (MathObject mObj : getObjects()){
			if (mObj instanceof GeneratedProblem){
				return true;
			}
			else if (mObj instanceof Grouping && ((Grouping)mObj).containsGeneratedProblems()){
				return true;
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
			this.getParentContainer().getParentDoc().getDocViewerPanel().resetAndRemoveTempGroup();
			this.getParentContainer().getParentDoc().getDocViewerPanel().setFocusedObject(newProblem);

		}
		else if (s.equals(DISTRIBUTE_VERTICALLY)){
			Vector<MathObject> orderedObjects = new Vector<MathObject>();
			int i;
			int totalHeight = 0;
			for ( MathObject mObj : getObjects()){
				i = 0;
				totalHeight += mObj.getHeight();
				for ( MathObject mathObj : orderedObjects){
					if ( mObj.getyPos() + mObj.getHeight() < mathObj.getyPos() + mathObj.getHeight()){
						break;
					}
					i++;
				}
				orderedObjects.add(i, mObj);
			}
			int extraSpace = getHeight() - totalHeight;
			int spaceBetweenEach = extraSpace/(getObjects().size() - 1);
			int currentPos = getyPos();
			for (MathObject mObj : orderedObjects){
				mObj.setyPos(currentPos);
				currentPos += mObj.getHeight() + spaceBetweenEach;
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(DISTRIBUTE_HORIZONTALLY)){
			Vector<MathObject> orderedObjects = new Vector<MathObject>();
			int i;
			int totalWidth = 0;
			for ( MathObject mObj : getObjects()){
				i = 0;
				totalWidth += mObj.getWidth();
				for ( MathObject mathObj : orderedObjects){
					if ( mObj.getxPos() + mObj.getWidth() < mathObj.getxPos() + mathObj.getWidth()){
						break;
					}
					i++;
				}
				orderedObjects.add(i, mObj);
			}
			int extraSpace = getWidth() - totalWidth;
			int spaceBetweenEach = extraSpace/(getObjects().size() - 1);
			int currentPos = getxPos();
			for (MathObject mObj : orderedObjects){
				mObj.setxPos(currentPos);
				currentPos += mObj.getWidth() + spaceBetweenEach;
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(ALIGN_GROUP_HORIZONTAL_CENTER)){
			int widest = 0;
			int center = getWidth()/2 + getxPos();
			for ( MathObject mObj : getObjects()){
				if ( mObj.getWidth() < widest){
					widest = mObj.getWidth();
				}
			}
			for ( MathObject mObj : getObjects()){
				mObj.setxPos(center - mObj.getWidth()/2);
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(ALIGN_GROUP_VERTICAL_CENTER)){
			int tallest = 0;
			int center = getHeight()/2 + getyPos();
			for ( MathObject mObj : getObjects()){
				if ( mObj.getHeight() < tallest){
					tallest = mObj.getHeight();
				}
			}
			for ( MathObject mObj : getObjects()){
				mObj.setyPos(center - mObj.getHeight()/2);
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(BRING_TO_LEFT)){
			for (MathObject mObj  : getObjects()){
				mObj.setxPos(getxPos());
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(BRING_TO_TOP)){
			for (MathObject mObj  : getObjects()){
				mObj.setyPos(getyPos());
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(BRING_TO_RIGHT)){
			for (MathObject mObj  : getObjects()){
				mObj.setxPos(getxPos() + getWidth() - mObj.getWidth());
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(BRING_TO_BOTTOM)){
			for (MathObject mObj  : getObjects()){
				mObj.setyPos(getyPos() + getHeight() - mObj.getHeight());
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(STRETCH_VERTICALLY)){
			for (MathObject mObj  : getObjects()){
				mObj.setHeight(this.getHeight());
				mObj.setyPos(this.getyPos());
			}
			adjustSizeToFitChildren();
		}
		else if (s.equals(STRETCH_HORIZONTALLY)){
			for (MathObject mObj  : getObjects()){
				mObj.setWidth(this.getWidth());
				mObj.setxPos(this.getxPos());
			}
			adjustSizeToFitChildren();
		}
	}

	public boolean objectContainedBelow(MathObject o){
		for (MathObject mObj : getObjects()){
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

	@Override
	public boolean equals(Object other) {
		super.equals(other);
		if (!(other instanceof Grouping)) {
			return false;
		}
		Grouping otherGroup = (Grouping) other;
		// to handle subclasses of Grouping, check the type string as well
		if (!getType().equals(otherGroup.getType())) {
			return false;
		}
		if (otherGroup.getObjects().size() != getObjects().size()) {
			return false;
		}
		return otherGroup.getObjects().containsAll(getObjects());
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
		getObjListWithName(OBJECTS).setObjects(objects);
	}

	public Vector<MathObject> getObjects() {
		return (Vector<MathObject>) getObjListWithName(OBJECTS).getObjects();
	}

	public boolean addObject(MathObject mObj){
		getObjects().add(mObj);
		mObj.setParentContainer(this);
		return true;
	}

	public void adjustSizeToFitChildren(){
		Vector<MathObject> temp = getObjects();
		setObjects(new Vector<MathObject>());
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
			return false;
		}
		if (getObjects().isEmpty()){
			setWidth(mObj.getWidth());
			setHeight(mObj.getHeight());
			setxPos(mObj.getxPos());
			setyPos(mObj.getyPos());

			//positions of getObjects() within Groups are relative to the group origin
			//and saved in a fraction of the total width/height, instead of number of pixels
			objectBounds.add(new DecimalRectangle(0,0,1,1));
			//			getParentPage().shiftObjInFrontOfOther(this, mObj);
			getObjects().add(mObj);
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
			getObjects().add(mObj);
			return true;
		}
		else
		{// the new object is outside of the groups bounds

			//need to temporarily store the currently grouped getObjects()
			//to prevent them from being resized with the calls to setWidth
			// which adjusts the size of the child getObjects() as a side effect

			//they will be added back after the group is resized to accommodate
			//the new object that was outside of the groups bounds
			Vector<MathObject> oldObjects = getObjects();
			setObjects(new Vector<MathObject>());
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
				getObjects().add(mathObj);
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
			getObjects().add(mObj);
			return true;
		}
	}

	public boolean removeObject(MathObject mObj){
		if ( ! getObjects().contains(mObj)){
			return false;
		}
		objectBounds.remove(getObjects().indexOf(mObj));
		getObjects().remove(mObj);
		adjustSizeToFitChildren();
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
		return true;
	}

	@Override
	public boolean removeAllObjects() {
		getObjects().removeAllElements();
		objectBounds = new Vector<DecimalRectangle>();
		return true;
	}

	/**
	 * This method is sued to move or resize all of the child object in a group after a corresponding
	 * change to the group is made. This is not a method for adjusting the group size after child
	 * getObjects() have been modified and possibly been moved outside of the group.
	 */
	private void adjustChildrenToGroupChange(){
		int index = 0;
		DecimalRectangle tempBounds;
		for (MathObject mathObj : getObjects()){
			tempBounds = objectBounds.get(index);
			mathObj.setxPos(getxPos() + (int) Math.round(tempBounds.getX() * getWidth()) );
			mathObj.setyPos(getyPos() + (int) Math.round(tempBounds.getY() * getHeight()) );
			mathObj.setWidth((int) Math.round(tempBounds.getWidth() * getWidth()) );
			mathObj.setHeight((int) Math.round(tempBounds.getHeight() * getHeight()) );
			if (mathObj.getWidth() == 0){
				mathObj.setWidth(1);
			}
			if (mathObj.getHeight() == 0){
				mathObj.setHeight(1);
			}
			index++;
		}
	}

	@Override
	public void setWidth(int width) {
		if (width == 0){
			width = 1;
		}
		getAttributeWithName(WIDTH).setValue(width);
		adjustChildrenToGroupChange();
	}

	@Override
	public void setHeight(int height) {
		if (height == 0){
			height = 1;
		}
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
