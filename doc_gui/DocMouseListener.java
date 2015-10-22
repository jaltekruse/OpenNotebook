/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.event.MouseInputListener;

import doc.Page;
import doc.PointInDocument;
import doc.mathobjects.Grouping;
import doc.mathobjects.MathObject;
import doc.mathobjects.ProblemNumberObject;
import doc.mathobjects.RectangleObject;
import doc_gui.mathobject_gui.MathObjectGUI;

public class DocMouseListener implements MouseInputListener {

	private DocViewerPanel docPanel;

	private boolean draggingDot, draggingObject, objPlacementRequiresMouseDrag,
	selectionRectRequiresMouseDrag, selectionRectBeingResized;

	private int currentDragDot, xBoxOffset, yBoxOffset, xDragStart, yDragStart;

	public DocMouseListener(DocViewerPanel docPanel) {
		this.docPanel = docPanel;
		draggingDot = false;
	}

	public void mouseClicked(MouseEvent e) {
		docPanel.requestFocus();

		boolean clickHandled = false, requiresRedraw = false;
		if (selectionRectRequiresMouseDrag || selectionRectBeingResized) {
			selectionRectRequiresMouseDrag = false;
			selectionRectBeingResized = false;
			docPanel.setSelectionRect(null);
		}
		PointInDocument clickedPt = docPanel.panelPt2DocPt(e.getX(), e.getY());

		if (!clickedPt.isOutSidePage()) {
			Rectangle objRect;

			// gives extra space around object to allow selection,
			// most useful for when objects are very thin
			int clickBuffer = 3;

			Vector<MathObject> currPageObjects = docPanel.getDoc()
					.getPage(clickedPt.getPage()).getObjects();
			MathObject mObj, oldFocused;
			oldFocused = docPanel.getFocusedObject();
			Page page = docPanel.getDoc().getPage(clickedPt.getPage());

			for (int i = (currPageObjects.size() - 1); i >= 0; i--)
			{// cycle through all of the objects belonging the page that was clicked on

				mObj = currPageObjects.get(i);
				objRect = mObj.getBounds();
				if (objRect.contains(new Point(clickedPt.getxPos(), clickedPt
						.getyPos())) && mObj != docPanel.getFocusedObject())
				{// the click occurred within an object, that was already selected
					if (mObj instanceof Grouping && docPanel.isInStudentMode()) {
						for (MathObject subObj : ((Grouping) mObj).getObjects()) {
							objRect = subObj.getBounds();
							if (objRect.contains(new Point(clickedPt.getxPos(),
									clickedPt.getyPos()))
									&& mObj != docPanel.getFocusedObject()) {
								docPanel.setFocusedObject(subObj);
								docPanel.repaintDoc();
								return;
							}
						}
					} else {
						if (e.isShiftDown()) {
							docPanel.addObjectToSelection(mObj);
						} else {
							docPanel.setFocusedObject(mObj);
						}
						docPanel.repaintDoc();
						docPanel.updateObjectToolFrame();
						return;
					}
				}
				// allow users to select objects within groups to modify them
				// without ungrouping
				if (e.getClickCount() == 2
						&& objRect.contains(new Point(clickedPt.getxPos(),
								clickedPt.getyPos()))
								&& mObj == docPanel.getFocusedObject()
								&& docPanel.getFocusedObject() instanceof Grouping) {
					for (MathObject subObj : ((Grouping) mObj).getObjects()) {
						objRect = new Rectangle(subObj.getxPos(),
								subObj.getyPos(), subObj.getWidth(),
								subObj.getHeight());
						if (objRect.contains(new Point(clickedPt.getxPos(),
								clickedPt.getyPos()))) {
							docPanel.setFocusedObject(subObj);
							docPanel.repaintDoc();
							return;
						}
					}
				}
			}

			if (docPanel.getFocusedObject() == oldFocused) {
				if (oldFocused != null) {
					objRect = new Rectangle(oldFocused.getxPos() - clickBuffer,
							oldFocused.getyPos() - clickBuffer,
							oldFocused.getWidth() + 2 * clickBuffer,
							oldFocused.getHeight() + 2 * clickBuffer);
					if (!objRect.contains(new Point(clickedPt.getxPos(),
							clickedPt.getyPos()))) {
						// send event to object
						docPanel.setFocusedObject(null);
						docPanel.repaintDoc();
						return;
					}
				}
			}
		} else {// click was outside of page
			docPanel.setSelectedPage(null);
			docPanel.setFocusedObject(null);
			docPanel.repaintDoc();
			return;
		}

		// objects can be off of the page, so this check must happen out here
		if (docPanel.getFocusedObject() != null && !clickHandled) {
			Point objPos = null;
			objPos = docPanel.getObjectPos(docPanel.getFocusedObject());
			Rectangle focusedRect = new Rectangle(objPos.x, objPos.y,
					(int) (docPanel.getFocusedObject().getWidth() * docPanel
							.getZoomLevel()),
							(int) (docPanel.getFocusedObject().getHeight() * docPanel
									.getZoomLevel()));
			if (focusedRect.contains(new Point(e.getX(), e.getY())))
			{// user clicked on the object that already had focus, send an event to the objet so it
				// can handle it for (for drag and drop, moving graphs etc.)
				docPanel.getPageGUI().handleMouseAction(
						docPanel.getFocusedObject(),
						(int) (e.getX() - objPos.getX()),
						(int) (e.getY() - objPos.getY()),
						PageGUI.MOUSE_LEFT_CLICK);
				clickHandled = true;
				requiresRedraw = true;
			}
			// throw click down to focused object
		}

		if (!clickHandled && !clickedPt.isOutSidePage()) {
			// the click hit a page, but missed all of its objects, select the
			// page
			docPanel.setSelectedPage(clickedPt.getPage());
			clickHandled = true;
			requiresRedraw = true;
		}

		if (!clickHandled)
		{// click occurred on a non-active part of screen,
			// unfocus current object
			docPanel.setFocusedObject(null);
			requiresRedraw = true;
		}

		if (requiresRedraw) {
			docPanel.repaintDoc();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		docPanel.requestFocus();
		draggingDot = false;

		PointInDocument clickedPt = docPanel.panelPt2DocPt(e.getX(), e.getY());

		MathObject objToPlace = docPanel.getObjToPlace();
		if (docPanel.isPlacingObject()) {
			objToPlace.setxPos(clickedPt.getxPos());
			objToPlace.setyPos(clickedPt.getyPos());
			objToPlace.setWidth(1);
			objToPlace.setHeight(1);
			objToPlace.setParentContainer(docPanel.getDoc().getPage(
					clickedPt.getPage()));
			objToPlace.getParentContainer().addObject(objToPlace);
			docPanel.repaintDoc();
			docPanel.updateObjectToolFrame();
			objPlacementRequiresMouseDrag = true;
			docPanel.setFocusedObject(objToPlace);
			return;
		} else if (detectResizeOrMove(e)) {// the mouse press occurred over a
			// resize dot or the selected object
			return;
		} else if (clickedPt.isOutSidePage()) {// click was outside of page
			if (docPanel.isPlacingObject()) {
				docPanel.getNotebookPanel().objHasBeenPlaced();
				docPanel.ungroupTempGroup();
			}
		} else {// the user clicked on a page, but none of the objects were
			// contacted
			// create a box to select multiple objects
			if (!docPanel.isInStudentMode()) {
				docPanel.ungroupTempGroup();
				RectangleObject rect = new RectangleObject(docPanel.getDoc()
						.getPage(clickedPt.getPage()));
				rect.setxPos(clickedPt.getxPos());
				rect.setyPos(clickedPt.getyPos());
				rect.setWidth(1);
				rect.setHeight(1);
				docPanel.setSelectionRect(rect);
				selectionRectRequiresMouseDrag = true;
			}

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (objPlacementRequiresMouseDrag) {// the mouse was pressed, but not
			// dragged to set an objects size
			// set a default size
			docPanel.addUndoState();
			docPanel.getNotebookPanel().getObjToPlace().setWidth(50);
			docPanel.getNotebookPanel().getObjToPlace().setHeight(50);
			docPanel.getNotebookPanel().objHasBeenPlaced();
			objPlacementRequiresMouseDrag = false;
		}
		if (selectionRectBeingResized || selectionRectRequiresMouseDrag) {
			selectionRectBeingResized = false;
			selectionRectRequiresMouseDrag = false;
			docPanel.setSelectionRect(null);
			docPanel.repaintDoc();
		}
		if (draggingDot) {
			docPanel.addUndoState();
			// allows for the text field to gain focus after the resize of an
			// object
			docPanel.propertiesFrameRefacoringNeeded = true;
			docPanel.repaintDoc();
		}
		if (draggingObject) {
			if (xDragStart != docPanel.getFocusedObject().getxPos()
					|| yDragStart != docPanel.getFocusedObject().getyPos()) 
			{// the object was clicked, and the position changed
				docPanel.addUndoState();
				if (docPanel.getFocusedObject() instanceof ProblemNumberObject
						|| (docPanel.getFocusedObject() instanceof Grouping && ((Grouping) docPanel
								.getFocusedObject()).containsProblemNumber())) {
					docPanel.getDoc().refactorPageNumbers();
					docPanel.repaintDoc();
				}
			}
		}
		selectionRectRequiresMouseDrag = false;
		selectionRectBeingResized = false;
		// docPanel.updateObjectToolFrame();
		draggingDot = false;
		draggingObject = false;
		docPanel.getNotebookPanel().objHasBeenPlaced();
		objPlacementRequiresMouseDrag = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		docPanel.requestFocus();

		if (dragMouseToPlaceObj(e)) {
			return;
		} else if (draggingObject) {// A mathobject on the document is being
			// shifted to a new location

			Page contactedPage = docPanel.getDoc().getPage(
					docPanel.panelPt2DocPt(e.getX(), e.getY()).getPage());
			if (contactedPage != docPanel.getFocusedObject().getParentPage())
			{ // the mouse moved off of the page the object was on previously
				docPanel.getFocusedObject().getParentContainer()
				.removeObject(docPanel.getFocusedObject());
				contactedPage.addObject(docPanel.getFocusedObject());
				docPanel.getFocusedObject().setParentContainer(contactedPage);
			}
			MathObjectGUI.moveBoxToPoint(new Point(e.getX(), e.getY()),
					docPanel.getFocusedObject(), docPanel
					.getPageOrigin(docPanel.getFocusedObject()
							.getParentPage()), docPanel.getZoomLevel(),
							xBoxOffset, yBoxOffset);
			if (docPanel.getFocusedObject().getParentContainer() instanceof Grouping) {
				((Grouping) docPanel.getFocusedObject().getParentContainer())
				.adjustSizeToFitChildren();
			}
			docPanel.repaintDoc();
			return;

		} else if (dragMouseToResizeObj(e)) {
			return;
		}
	}

	/**
	 * Check a mouse event to see what type it is, and assign it a code from the
	 * list of mouse event codes in PagieGUI.
	 * 
	 * @return - the code to best describe the MouseEvent Passed
	 */
	public int getMouseCode(MouseEvent e) {
		// TODO implement this
		return 0;
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	private boolean detectResizeOrMove(MouseEvent e) {
		if (docPanel.getFocusedObject() == null) {// the document mouse is not
			// in a state in which this
			// method is needed
			return false;
		}

		int dot = MathObjectGUI.detectResizeDotCollision(
				new Point(e.getX(), e.getY()), docPanel.getFocusedObject(),
				docPanel.getPageOrigin(docPanel.getFocusedObject()
						.getParentPage()), docPanel.getZoomLevel());
		if (dot < Integer.MAX_VALUE && !docPanel.isInStudentMode())
		{// the user clicked on one of the dots for resizing an object
			draggingDot = true;
			draggingObject = false;
			currentDragDot = dot;
			return true;
		} else {// a resize dot was note contacted
			boolean clickedSelectedObj;
			// these three lines change the functionality to require contact
			// with the object's boarder
			// instead of anywhere on it, will likely work better when I start
			// forwarding mouse events
			// for individual objects to handle. Or will have to flip between
			// the two depending
			// on the state of the selected object
      if (false) {
        clickedSelectedObj = MathObjectGUI.detectBorderCollision(new
        Point(e.getX(), e.getY()),
        docPanel.getFocusedObject(), docPanel.getPageOrigin(
        docPanel.getFocusedObject().getParentPage()),
        docPanel.getZoomLevel());
      }
			clickedSelectedObj = MathObjectGUI.detectObjectCollision(new Point(
					e.getX(), e.getY()), docPanel.getFocusedObject(),
					docPanel.getPageOrigin(docPanel.getFocusedObject()
							.getParentPage()), docPanel.getZoomLevel());

			if (clickedSelectedObj && !docPanel.isInStudentMode()) {
				draggingObject = true;
				xBoxOffset = (int) ((e.getX()
						- docPanel.getPageOrigin(
								docPanel.getFocusedObject().getParentPage())
								.getX() - docPanel.getFocusedObject().getxPos()
								* docPanel.getZoomLevel()) / docPanel.getZoomLevel());
				yBoxOffset = (int) ((e.getY()
						- docPanel.getPageOrigin(
								docPanel.getFocusedObject().getParentPage())
								.getY() - docPanel.getFocusedObject().getyPos()
								* docPanel.getZoomLevel()) / docPanel.getZoomLevel());
				xDragStart = docPanel.getFocusedObject().getxPos();
				yDragStart = docPanel.getFocusedObject().getyPos();
				return true;
			}
		}
		return false;
	}

	private boolean dragMouseToPlaceObj(MouseEvent e) {
		if (!(objPlacementRequiresMouseDrag || selectionRectRequiresMouseDrag)) {// the
			// document mouse is not is a state in which this method is needed
			return false;
		}
		PointInDocument docPt = docPanel.panelPt2DocPt(e.getX(), e.getY());
		MathObject objToPlace = docPanel.getObjToPlace();
		if (selectionRectRequiresMouseDrag) {// swap in the selection rectangle,
			// allows reuse of code for the
			// addition of a
			// rectangle to select objects
			objToPlace = docPanel.getSelectionRect();
			selectionRectBeingResized = true;
			selectionRectRequiresMouseDrag = false;
		}
		if (docPt.isOutSidePage()) {
			objToPlace.getParentContainer().removeObject(objToPlace);
			docPanel.setFocusedObject(null);
		} else {
			boolean isWest = false;
			if (!selectionRectBeingResized) { // if the object being re-sized is
				// not the selection rectangle
				draggingDot = true;
			}
			if (docPt.getxPos() < objToPlace.getxPos()) {
				objToPlace.setWidth(objToPlace.getxPos() - docPt.getxPos());
				objToPlace.setxPos(docPt.getxPos());
				isWest = true;
			} else {
				objToPlace.setWidth(docPt.getxPos() - objToPlace.getxPos());
			}
			if (objToPlace.isHorizontallyResizable()
					&& !objToPlace.isVerticallyResizable()) {
				objToPlace.setHeight(15);
				if (isWest) {
					currentDragDot = MathObjectGUI.WEST_DOT;
				} else {
					currentDragDot = MathObjectGUI.EAST_DOT;
				}
			} else if (!objToPlace.isHorizontallyResizable()
					&& objToPlace.isVerticallyResizable()) {
				if (docPt.getyPos() < objToPlace.getyPos()) {
					objToPlace.setyPos(docPt.getyPos());
					currentDragDot = MathObjectGUI.NORTH_DOT;
				} else {
					currentDragDot = MathObjectGUI.SOUTH_DOT;
				}
			} else if (docPt.getyPos() < objToPlace.getyPos()) {
				objToPlace.setHeight(objToPlace.getyPos() - docPt.getyPos());
				objToPlace.setyPos(docPt.getyPos());
				if (isWest) {
					currentDragDot = MathObjectGUI.NORTHWEST_DOT;
				} else {
					currentDragDot = MathObjectGUI.NORTHEAST_DOT;
					objToPlace.flipHorizontally();
				}
			} else {
				objToPlace.setHeight(docPt.getyPos() - objToPlace.getyPos());
				if (isWest) {
					currentDragDot = MathObjectGUI.SOUTHWEST_DOT;
					objToPlace.flipVertically();
				} else {
					currentDragDot = MathObjectGUI.SOUTHEAST_DOT;
				}
			}
			if (selectionRectRequiresMouseDrag) {
				selectionRectRequiresMouseDrag = false;
				selectionRectBeingResized = true;
			}
			setObjPlacementRequiresMouseDrag(false);
			docPanel.repaintDoc();
		}
		return true;
	}

	private boolean dragMouseToResizeObj(MouseEvent e) {
		if (!(draggingDot || selectionRectBeingResized)) {// the document mouse
			// is not in a state
			// in which this
			// method is needed
			return false;
		}
		// one of the dots of an object is being moved to make it larger or
		// smaller
		// or the temporary selection rectangle is being resized
		PointInDocument docPt = docPanel.panelPt2DocPt(e.getX(), e.getY());
		MathObject objToResize = null;
		if (draggingDot) {// an object is focused and one of its resizing dots
			// has been contacted
			// resize the focues object
			objToResize = docPanel.getFocusedObject();
		} else if (selectionRectBeingResized) {// the user drew a selection
			// rectangle on the screen,
			// resize the selection
			// rectangle
			objToResize = docPanel.getSelectionRect();
		}

		Page pageOfResizeObject = objToResize.getParentPage();
		Page contactedPage = docPanel.getDoc().getPage(docPt.getPage());

		if (!docPt.isOutSidePage() && pageOfResizeObject == contactedPage) {

			if (selectionRectBeingResized) {// a special object that does note
				// exist in the document was
				// resized, it is used
				// to select multiple objects at
				// once and create groups

				MathObjectGUI.moveResizeDot(docPanel.getSelectionRect(),
						currentDragDot, docPt, this);
				adjustTempGroupSelection();
			} else {// a regular object was resized
				MathObjectGUI.moveResizeDot(objToResize, currentDragDot, docPt,
						this);
			}
			docPanel.repaintDoc();
			return true;
		} else {// the mouse moved outside of the page where the object to
			// resize resides
			Point pageOrigin = null;
			pageOrigin = docPanel.getPageOrigin(objToResize.getParentPage());
			// flags to indicate that a position has not been assigned to the
			// click
			int xMouseRequest = Integer.MAX_VALUE;
			int yMouseRequest = Integer.MAX_VALUE;

			if (e.getX() <= pageOrigin.getX()) {// event was to the left of the
				// document, send an resize
				// request with the edge of page
				// and the events given y
				// position
				xMouseRequest = 0;
			} else if (e.getX() >= pageOrigin.getX()
					+ (int) (pageOfResizeObject.getWidth() * docPanel
							.getZoomLevel())) {
				xMouseRequest = pageOfResizeObject.getWidth();
			}

			if (e.getY() <= pageOrigin.getY()) {
				yMouseRequest = 0;
			}

			else if (e.getY() >= pageOrigin.getY()
					+ (int) (pageOfResizeObject.getHeight() * docPanel
							.getZoomLevel())) {
				yMouseRequest = pageOfResizeObject.getHeight();
			}

			if (yMouseRequest == Integer.MAX_VALUE) {
				yMouseRequest = docPt.getyPos();
			}
			if (xMouseRequest == Integer.MAX_VALUE) {
				xMouseRequest = docPt.getxPos();
			}
			MathObjectGUI.moveResizeDot(objToResize, currentDragDot,
					new PointInDocument(1, xMouseRequest, yMouseRequest), this);

			if (objToResize.getParentContainer() instanceof Grouping) {
				((Grouping) objToResize.getParentContainer())
				.adjustSizeToFitChildren();
			}
			if (selectionRectBeingResized) {// a special object that does note
				// exist in the document was
				// resized, it is used
				// to select multiple objects at
				// once and create groups

				adjustTempGroupSelection();

			}
			docPanel.repaintDoc();
		}
		return true;
	}

	/**
	 * Method to change the temporary group selection based on the current
	 * selection rectangle. Removes objects no longer contacted, and adds new
	 * objects that are not yet group members.
	 * 
	 * @return - whether or not a redraw event is needed
	 */
	private boolean adjustTempGroupSelection() {
		// need to modify this code to take into account the current stacking of
		// the objects
		// large objects automatically cover up smaller ones using this system
		Rectangle selectRect = docPanel.getSelectionRect().getBounds();
		Grouping tempGroup = docPanel.getTempGroup();
		Vector<MathObject> pageObjects = docPanel.getSelectionRect()
				.getParentPage().getObjects();

		// temporary storage of objects that collide with the selection
		// rectangle
		Vector<MathObject> collisionObjects = new Vector<MathObject>();

		for (MathObject mObj : pageObjects) {// find all of the objects that
			// were contacted by the
			// selection rectangle
			if (selectRect.intersects(mObj.getBounds())) {
				collisionObjects.add(mObj);
			}
		}

		if (collisionObjects.size() == 0) {// no objects were contacted
			docPanel.ungroupTempGroup();
			docPanel.setFocusedObject(null);
			return true;
		}

		if (collisionObjects.size() == 1) {// only one object was contacted
			MathObject contactedObj = collisionObjects.get(0);
			if (contactedObj.equals(docPanel.getFocusedObject())) {// collision
				// occurred
				// with a
				// single
				// object
				// already
				// selected
				;// do nothing
				if (contactedObj != tempGroup) {
					collisionObjects.remove(contactedObj);
					return true;
				}
			} else {
				docPanel.setFocusedObject(contactedObj);
				collisionObjects.remove(contactedObj);
				return true;
			}
		}

		if (collisionObjects.contains(tempGroup)) {
			MathObject mObj;
			// need to make sure all of the objects in the group were contacted,
			// otherwise remove them
			for (int i = 0; i < tempGroup.getObjects().size(); i++) {
				mObj = tempGroup.getObjects().get(i);
				if (!selectRect.intersects(mObj.getBounds())) {
					tempGroup.removeObject(mObj);
					tempGroup.getParentContainer().addObject(mObj);
					mObj.setParentContainer(tempGroup.getParentContainer());
					i--;
				}
			}
			// remove the temporary group from the contacted list, it now
			// contains only elements that were
			// in it before and were contacted by the current selection
			// rectangle
			collisionObjects.remove(tempGroup);
		}

		// objects were selected, that have not been added to the temp group yet

		if (collisionObjects.size() > 0) {
			tempGroup.setParentContainer(collisionObjects.get(0)
					.getParentContainer());
			collisionObjects.get(0).getParentContainer().addObject(tempGroup);
			for (MathObject mObj : collisionObjects) {
				mObj.getParentContainer().removeObject(mObj);
				mObj.setParentContainer(null);
				tempGroup.addObjectFromPage(mObj);
				mObj.setParentContainer(tempGroup);
			}
			docPanel.getDoc().refactorPageNumbers();
			docPanel.setFocusedObject(tempGroup);
		}

		if (tempGroup.getParentContainer() != null) {
			if (tempGroup.getObjects().size() == 1) {// there is one one object
				// left in the temporary group
				docPanel.setFocusedObject(tempGroup.getObjects().get(0));
				docPanel.ungroupTempGroup();
			}
		}
		return false;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	public int getCurrentDragDot() {
		return currentDragDot;
	}

	public void setCurrentDragDot(int dot) {
		currentDragDot = dot;
	}

	public boolean isPlacingObject() {
		return docPanel.isPlacingObject();
	}

	public void setObjPlacementRequiresMouseDrag(
			boolean objPlacementRequiresMouseMovement) {
		this.objPlacementRequiresMouseDrag = objPlacementRequiresMouseMovement;
	}

	public boolean objPlacementRequiresMouseDrag() {
		return objPlacementRequiresMouseDrag;
	}
}
