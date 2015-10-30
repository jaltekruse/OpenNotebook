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

package doc_gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import doc.Page;
import doc.mathobjects.AnswerBoxObject;
import doc.mathobjects.ArrowObject;
import doc.mathobjects.ConeObject;
import doc.mathobjects.CubeObject;
import doc.mathobjects.CylinderObject;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GraphObject;
import doc.mathobjects.Grouping;
import doc.mathobjects.LineObject;
import doc.mathobjects.MathObject;
import doc.mathobjects.NumberLineObject;
import doc.mathobjects.OvalObject;
import doc.mathobjects.ParallelogramObject;
import doc.mathobjects.PolygonObject;
import doc.mathobjects.RectangleObject;
import doc.mathobjects.RegularPolygonObject;
import doc.mathobjects.TextObject;
import doc.mathobjects.TriangleObject;
import doc_gui.mathobject_gui.AnswerBoxGUI;
import doc_gui.mathobject_gui.ConeObjectGUI;
import doc_gui.mathobject_gui.CubeObjectGUI;
import doc_gui.mathobject_gui.CylinderObjectGUI;
import doc_gui.mathobject_gui.ExpressionObjectGUI;
import doc_gui.mathobject_gui.GraphObjectGUI;
import doc_gui.mathobject_gui.LineObjectGUI;
import doc_gui.mathobject_gui.MathObjectGUI;
import doc_gui.mathobject_gui.NumberLineObjectGUI;
import doc_gui.mathobject_gui.OvalObjectGUI;
import doc_gui.mathobject_gui.PolygonObjectGUI;
import doc_gui.mathobject_gui.TextObjectGUI;

public class PageGUI {

	private int printableXOrigin, printableYOrigin;

	private DocViewerPanel docPanel;

	public TextObjectGUI textGUI;
	public OvalObjectGUI ovalGUI;
	public GraphObjectGUI graphGUI;
	public PolygonObjectGUI polygonGUI;
	public ExpressionObjectGUI expressionGUI;
	public NumberLineObjectGUI numLineGUI;
	public AnswerBoxGUI answerBoxGUI;
	public CubeObjectGUI cubeGUI;
	public CylinderObjectGUI cylinderGUI;
	public ConeObjectGUI coneGUI;
	public LineObjectGUI lineGUI;

	// TODO - define an interface for handling mouse clicks
	// that will replace the conditional blocks checking for
	// these IDs
	public static final int MOUSE_LEFT_CLICK = 0;
	public static final int MOUSE_MIDDLE_CLICK = 1;
	public static final int MOUSE_RIGHT_CLICK = 2;
	public static final int MOUSE_LEFT_PRESS = 3;
	public static final int MOUSE_MIDDLE_PRESS = 4;
	public static final int MOUSE_RIGHT_PRESS = 5;
	public static final int MOUSE_RIGHT_RELEASE = 6;
	public static final int MOUSE_MIDDLE_RELEASE = 7;
	public static final int MOUSE_LEFT_RELEASE = 8;
	public static final int MOUSE_MOVED = 9;
	public static final int MOUSE_DRAGGED = 10;
	public static final int MOUSE_ENTERED = 11;
	public static final int MOUSE_EXITED = 12;

    public static final char UP = 21;
    public static final char DOWN = 22;
    public static final char LEFT = 23;
    public static final char RIGHT = 24;

	private final Map<Class, MathObjectGUI> mathObjectToGuiMap;

	static {
	}

	/**
	 * Constructor used for painting to the screen.
	 * 
	 * @param docViewerPanel - the viewer panel to be drawn on
	 */
	public PageGUI(DocViewerPanel docViewerPanel){
		this();
		this.docPanel = docViewerPanel;
	}


	/**
	 * Constructor used for printing to a physical document
	 */
	public PageGUI(){
		// TODO these could be static, but I think they might be used for storing some
		// temporary state, should look back at this
		textGUI = new TextObjectGUI();
		ovalGUI = new OvalObjectGUI();
		graphGUI = new GraphObjectGUI();
		polygonGUI = new PolygonObjectGUI();
		expressionGUI = new ExpressionObjectGUI();
		numLineGUI = new NumberLineObjectGUI();
		answerBoxGUI = new AnswerBoxGUI();
		cubeGUI = new CubeObjectGUI();
		cylinderGUI = new CylinderObjectGUI();
		coneGUI = new ConeObjectGUI();
		lineGUI = new LineObjectGUI();

		mathObjectToGuiMap = new HashMap<>();
		mathObjectToGuiMap.put(TextObject.class, textGUI);
		mathObjectToGuiMap.put(OvalObject.class, ovalGUI);
		mathObjectToGuiMap.put(GraphObject.class, graphGUI);
		mathObjectToGuiMap.put(LineObject.class, lineGUI);
		mathObjectToGuiMap.put(PolygonObject.class, polygonGUI);
		mathObjectToGuiMap.put(RegularPolygonObject.class, polygonGUI);
		mathObjectToGuiMap.put(ParallelogramObject.class, polygonGUI);
		mathObjectToGuiMap.put(CubeObject.class, polygonGUI);
		mathObjectToGuiMap.put(ArrowObject.class, polygonGUI);
		mathObjectToGuiMap.put(RectangleObject.class, polygonGUI);
		mathObjectToGuiMap.put(TriangleObject.class, polygonGUI);
		mathObjectToGuiMap.put(ExpressionObject.class, expressionGUI);
		mathObjectToGuiMap.put(NumberLineObject.class, numLineGUI);
		mathObjectToGuiMap.put(AnswerBoxObject.class, answerBoxGUI);
		mathObjectToGuiMap.put(CubeObject.class, cubeGUI);
		mathObjectToGuiMap.put(CylinderObject.class, cylinderGUI);
		mathObjectToGuiMap.put(ConeObject.class, coneGUI);
		mathObjectToGuiMap.put(LineObject.class, lineGUI);
	}

	/**
	 * @param mObj
	 * @param x
	 * @param y
	 * @param mouseActionCode
	 */
	public void handleMouseAction(MathObject mObj, int x, int y, int mouseActionCode){
		if (mathObjectToGuiMap.containsKey(mObj.getClass())) {
			mathObjectToGuiMap.get(mObj.getClass())
					.mouseClicked((GraphObject)mObj, x, y, docPanel.getZoomLevel());
		} else{
			// TODO - logging
//			System.out.println("unreconginzed object (PageGUI.handleMouseAction)");
		}
	}
	
	public void handleKeypress(MathObject mObj, char key){
		if (mObj instanceof ExpressionObject){
			docPanel.repaintDoc();
		}
	}

	public void drawPage(Graphics g, Page p, Point pageOrigin, Rectangle visiblePageSection,
			float zoomLevel){



		//draw MathObjects, only if they intersect with the available viewport

		//translate viewport into an available subsection of printable document
		//used to detect collisions with the rectangles that contain mathObjects

		for (MathObject mObj : p.getObjects()){
			drawObject(mObj, g, pageOrigin, zoomLevel);
		}

	}

    public MathObjectGUI getGUIForObj(MathObject mObj){
      if (mathObjectToGuiMap.containsKey(mObj.getClass())) {
      	return mathObjectToGuiMap.get(mObj.getClass());
      } else{
				throw new RuntimeException("could not find an object GUI for the type, " + mObj.getClass() );
      }
    }

	public void drawObject(MathObject mObj, Graphics g, Point pageOrigin, float zoomLevel){
		if (mObj instanceof Grouping){
			Grouping group = ((Grouping)mObj);
			for (MathObject mathObj : group.getObjects()){
				drawObject(mathObj, g, pageOrigin, zoomLevel);
			}
		} else if (mathObjectToGuiMap.containsKey(mObj.getClass())) {
			mathObjectToGuiMap.get(mObj.getClass()).drawMathObject(mObj, g, pageOrigin, zoomLevel);
		}
		else{
			// TODO - logging
//			System.out.println("unreconginzed object (printed in PageGUI.drawMathObj)");
		}
	}

	public void drawPageWithDecorations(Graphics g, Page p, Point pageOrigin, Rectangle visiblePageSection,
			float zoomLevel){
		int adjustedWidth = (int) (p.getWidth() * zoomLevel);
		int adjustedHeight = (int) (p.getHeight() * zoomLevel);

		int pageXOrigin = (int) pageOrigin.getX();
		int pageYOrigin = (int) pageOrigin.getY();

		if (docPanel.getSelectedPage() == p){
			g.setColor(new Color(230,240, 255));
		}
		else{
			g.setColor(Color.WHITE);
		}
		g.fillRect(pageXOrigin, pageYOrigin, adjustedWidth, adjustedHeight);

		//draw gray box to show margins
		int adjustedxMargin = (int) (p.getxMargin() * zoomLevel);
		int adjustedyMargin = (int) (p.getyMargin() * zoomLevel);
		printableXOrigin = pageXOrigin + adjustedxMargin;
		printableYOrigin = pageYOrigin + adjustedyMargin;
		g.setColor(Color.GRAY.brighter());
		g.drawRect(printableXOrigin, printableYOrigin,
				adjustedWidth - 2 * adjustedxMargin, adjustedHeight - adjustedyMargin * 2);

		//draw shadow and outline of page

		if (docPanel.getSelectedPage() == p){
			g.setColor(Color.BLUE);
		}
		else{
			g.setColor(Color.BLACK);
		}
		g.fillRect(pageXOrigin + 3, pageYOrigin + adjustedHeight, adjustedWidth, 3);
		g.fillRect(pageXOrigin + adjustedWidth, pageYOrigin + 3, 3, adjustedHeight);
		g.drawRect(pageXOrigin, pageYOrigin, adjustedWidth, adjustedHeight);

		drawPage(g, p, pageOrigin, visiblePageSection, zoomLevel);

		if (docPanel.getFocusedObject() != null){
			MathObject focusedObj = docPanel.getFocusedObject();
			if (p.objectContainedBelow(focusedObj))
			{//the focused object is on this page, print the resize dots
				//the temporary rectangle used for selecting a group of objects is
				//created using a RectangleObject, but it is not added to its parent page
				//in that case the dots are not drawn
				if( focusedObj instanceof ExpressionObject){
					expressionGUI.drawInteractiveComponents((ExpressionObject)focusedObj, g,
							new Point((int) pageOrigin.getX(), (int) pageOrigin.getY()), zoomLevel);
				}
				else if (focusedObj instanceof Grouping){
					Grouping group = ((Grouping)focusedObj);
					if (docPanel != null && group == docPanel.getFocusedObject()){
						for (MathObject mathObj : group.getObjects()){
							g.setColor(Color.BLUE);
							((Graphics2D)g).setStroke(new BasicStroke(2));
							// TODO - fix this hack, need to make a better mapping between MathObject classes and their corresponding
							// GUI classes, this method to get the polygon should be called in the specific object
							// this refactoring will simplify this whole class
							Graphics2D g2d = (Graphics2D)g;
							if (mathObj instanceof PolygonObject) {
								g2d.drawPolygon(polygonGUI.getCollisionAndSelectionPolygon(mathObj, pageOrigin, zoomLevel));
							} else {
								g2d.drawPolygon(expressionGUI.getCollisionAndSelectionPolygon(mathObj, pageOrigin, zoomLevel));
							}
							((Graphics2D)g).setStroke(new BasicStroke(1));
						}
					}
				}
				if ( (! focusedObj.isHorizontallyResizable() && ! focusedObj.isVerticallyResizable())
						|| docPanel.isInStudentMode())
				{// if the object cannot be resized, or if in student mode (where users cannot resize objects)
					g.setColor(Color.GRAY);
					((Graphics2D)g).setStroke(new BasicStroke(3));
					g.drawRect((int) (pageOrigin.getX() + focusedObj.getxPos() * zoomLevel) - 3, 
							(int) (pageOrigin.getY() + focusedObj.getyPos() * zoomLevel ) - 3,
							(int) ( focusedObj.getWidth() * zoomLevel ) + 6,
							(int) ( focusedObj.getHeight() * zoomLevel ) + 6);
					((Graphics2D)g).setStroke(new BasicStroke(1));
				}
				else{
					MathObjectGUI.drawResizingDots(focusedObj, g,
							new Point((int) pageOrigin.getX(), (int) pageOrigin.getY()), zoomLevel);
				}	
			}
		}	
	}
}
