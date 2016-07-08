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

package doc_gui.attribute_panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import cz.natur.cuni.mirai.math.editor.JMathField;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathSequence;
import doc.Document;
import doc.attributes.BooleanAttribute;
import doc.attributes.ColorAttribute;
import doc.attributes.EnumeratedAttribute;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc.mathobjects.AnswerBoxObject;
import doc.mathobjects.ExpressionObject;
import doc.mathobjects.GraphObject;
import doc.mathobjects.Grouping;
import doc.mathobjects.MathObject;
import doc.mathobjects.PolygonObject;
import doc.mathobjects.TextObject;
import doc.mathobjects.TriangleObject;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;
import doc_gui.OCButton;

public class ObjectPropertiesFrame extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 648301575472116469L;
	
	private JPanel mainPanel;
	private NotebookPanel notebookPanel; 
	private Vector<AdjustmentPanel> adjusters;
	private Vector<ListAdjuster> listAdjusters;
	private MathObject object;
	private String[] graphNavActions = {GraphObject.MOVE_DOWN, GraphObject.MOVE_UP,
			GraphObject.MOVE_LEFT, GraphObject.MOVE_RIGHT, GraphObject.DEFAULT_GRID,
			GraphObject.ZOOM_IN, GraphObject.ZOOM_OUT};
	private String[] expressionOpActions = {ExpressionObject.ADD_TO_BOTH_SIDES,
			ExpressionObject.MULTIPLY_BOTH_SIDES, ExpressionObject.DIVIDE_BOTH_SIDES,
			ExpressionObject.SUBTRACT_FROM_BOTH_SIDES, ExpressionObject.OTHER_OPERATIONS,
			ExpressionObject.MANUALLY_TYPE_STEP, ExpressionObject.MODIFY_EXPRESSION,
			ExpressionObject.SUB_IN_VALUE, ExpressionObject.UNDO_STEP};
	JScrollPane scrollPane;
	
	private static final Object[][] actionPicInfo = { 
		{ MathObject.MAKE_SQUARE,				getIcon("makeSquare.png")},
		{ MathObject.ADJUST_SIZE_AND_POSITION,	getIcon("adjustSizeAndPosition.png")},
		{ MathObject.ALIGN_PAGE_LEFT,			getIcon("alignLeftPage.png")},
		{ MathObject.ALIGN_PAGE_HORIZONTAL_CENTER, getIcon("alignHorizontalCenterPage.png")},
		{ MathObject.ALIGN_PAGE_RIGHT,			getIcon("alignRightPage.png")},
		{ MathObject.ALIGN_PAGE_TOP,			getIcon("alignTopPage.png")},
		{ MathObject.ALIGN_PAGE_VERTICAL_CENTER, getIcon("alignVerticalCenterPage.png")},
		{ MathObject.ALIGN_PAGE_BOTTOM, 		getIcon("alignBottomPage.png")},
		{ TriangleObject.MAKE_ISOSCELES_TRIANGLE, getIcon("makeIsosceles.png")},
		{ TriangleObject.MAKE_RIGHT_TRIANGLE,	getIcon("makeRightTriangle.png")},
		{ MathObject.FLIP_HORIZONTALLY, 		getIcon("flipHorizontally.png")},
		{ MathObject.FLIP_VERTICALLY, 			getIcon("flipVertically.png")},
		{ GraphObject.ZOOM_IN,					getIcon("smallZoomIn.png")},
		{ GraphObject.ZOOM_OUT,					getIcon("smallZoomOut.png")},
		{ GraphObject.DEFAULT_GRID,				getIcon("defaultGrid.png")},
		{ GraphObject.MOVE_LEFT,				getIcon("moveGraphLeft.png")},
		{ GraphObject.MOVE_RIGHT,				getIcon("moveGraphRight.png")},
		{ GraphObject.MOVE_UP, 					getIcon("moveGraphUp.png")},
		{ GraphObject.MOVE_DOWN, 				getIcon("moveGraphDown.png")},
		{ Grouping.BRING_TO_BOTTOM, 			getIcon("alignBottom.png")},
		{ Grouping.BRING_TO_TOP, 				getIcon("alignTop.png")},
		{ Grouping.BRING_TO_LEFT, 				getIcon("alignLeft.png")},
		{ Grouping.BRING_TO_RIGHT, 				getIcon("alignRight.png")},
		{ Grouping.DISTRIBUTE_HORIZONTALLY, 	getIcon("distributeHorizontally.png")},
		{ Grouping.DISTRIBUTE_VERTICALLY, 		getIcon("distributeVertically.png")},
		{ Grouping.ALIGN_GROUP_HORIZONTAL_CENTER, getIcon("alignHorizontalCenter.png")},
		{ Grouping.ALIGN_GROUP_VERTICAL_CENTER,	getIcon("alignVerticalCenter.png")},
		{ ExpressionObject.ADD_TO_BOTH_SIDES,	getIcon("addition.png")},
		{ ExpressionObject.SUBTRACT_FROM_BOTH_SIDES, getIcon("subraction.png")},
		{ ExpressionObject.DIVIDE_BOTH_SIDES,	getIcon("division.png")},
		{ ExpressionObject.MULTIPLY_BOTH_SIDES,	getIcon("multiplication.png")},
	};
	
	public static ImageIcon SMALL_KEYBOARD_IMAGE = getIcon("smallKeyboard.png");

	public ObjectPropertiesFrame(NotebookPanel notebookPanel){
		super("Tools",
				true, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable
		this.notebookPanel = notebookPanel;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		adjusters = new Vector<>();
		listAdjusters = new Vector<>();
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(scrollPane);
	}
	
	public ObjectPropertiesFrame(MathObject mObj, NotebookPanel notebookPanel){
		super("Tools",
				true, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable
		object = mObj;
		this.notebookPanel = notebookPanel;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		adjusters = new Vector<>();
		listAdjusters = new Vector<>();
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(scrollPane);
		generatePanel(object);
	}

	public JScrollPane getScrollPane(){
		return scrollPane;
	}

	public JPanel generatePanel(Document doc, DocViewerPanel docPanel) {
		JPanel panel = new JPanel();
		if (doc != null){
			panel.setLayout(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints();
			con.fill = GridBagConstraints.BOTH;
			con.weightx = 1;
			con.weighty = 1;
			con.insets = new Insets(2,0,2,0);
			con.gridx = 0;
			con.gridy = 0;
			for (MathObjectAttribute mAtt : doc.getAttributes()){
				if ( docPanel.isInStudentMode() && mAtt.isStudentEditable() ||
						! docPanel.isInStudentMode())
				{// only show editing dialog if in teacher mode (not student)
					//or if the attribute has been left student editable
					panel.add(getAdjuster(mAtt, notebookPanel, panel), con);
					con.gridy++;
				}
			}
		}
		return panel;
	}

	/**
	 * Generates a menu for adjusting the properties of a mathobject.
	 * 
	 * @param o - object to base menu panel on
	 */
	public void generatePanel(MathObject o){
		System.out.println("generate panel" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		if (o == null){
			return;
		}
		this.getContentPane().removeAll();
		object = o;
		JPanel panel = mainPanel;
		mainPanel.removeAll();
		adjusters.removeAllElements();
		listAdjusters.removeAllElements();
		this.setTitle(o.getType());
		JTabbedPane panelTabs = null;
		JPanel tabOneContents = null, tabTwoContents = null;
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		con.insets = new Insets(2, 2, 2, 2);
		con.gridx = 0;
		con.gridy = 0;
		
		System.out.println("end init stuff" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		
		if (o instanceof GraphObject || (o instanceof ExpressionObject && ! notebookPanel.isInStudentMode()))
		{// there are too many attributes and actions for the graph to put them all in one panel
			// add a tabbed pane to make it more reasonable and avoid scrolling
			panelTabs = new JTabbedPane();
			this.getContentPane().add(panelTabs);
			tabOneContents = new JPanel();
			tabOneContents.setLayout(new GridBagLayout());
			tabTwoContents = new JPanel();
			tabTwoContents.setLayout(new GridBagLayout());
			System.out.println("1 " + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
			JScrollPane tabScrollPane = new JScrollPane(panelTabs);
			tabScrollPane.getVerticalScrollBar().setUnitIncrement(16);
			tabScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
			System.out.println("2 " + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
			tabScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			if ( o instanceof GraphObject){
				panelTabs.add("Nav", tabOneContents);
				panelTabs.add("Grid", tabTwoContents);
				panel = tabOneContents;
			}
			else if (o instanceof ExpressionObject){
				panelTabs.add("Expression", tabOneContents);
				panelTabs.add("Solve", tabTwoContents);
				panel = tabOneContents;
			}
			System.out.println("3 " + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
			this.getContentPane().add(tabScrollPane);
			System.out.println("4 " + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		}
		else{
			this.getContentPane().add(scrollPane);
		}
		System.out.println("done with tabs " + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		
		con.weighty = .01;
		JPanel actionPics = new JPanel();
		actionPics.setLayout(new GridLayout(0, 4, 4, 4));
		JPanel otherActions = new JPanel();
		otherActions.setLayout(new GridLayout(0, 1, 4, 4));

		ImageIcon pic;
		JButton button;
		if ( ! notebookPanel.isInStudentMode()){
			for (final String s : o.getActions()){
				pic = getIconForAction(s);
				if (pic != null) createButton(s,0,0,0,0, actionPics);
				else createButton(s,0,0,0,0, otherActions);
			}
		}
		
		System.out.println("teacher actions done" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));

		boolean skipAction;
		for (final String s : o.getStudentActions()){
			skipAction = false;
			if ( object instanceof GraphObject)
			{// skip the actions for navigating around the graph
				// they are added in a separate panel to make their
				// use more intuitive
				for ( String str : graphNavActions){
					if (s.equals(str)) {skipAction = true; break;}
				}
			}
			if (object instanceof ExpressionObject)
			{// skip the operations that change both sides, they are added
				// in a separate panel to make the list of actions smaller
				for ( String str : expressionOpActions){
					if (s.equals(str)){skipAction = true;break;}
				}
			}
			if ( skipAction ){
				continue;
			}
			pic = getIconForAction(s);
			if (pic != null) createButton(s,0,0,0,0, actionPics);
			else createButton(s,0,0,0,0, otherActions);
		}
		System.out.println("student actions done" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		
		if (otherActions.getComponentCount() != 0)
		{// only add panel for actions if components have been added to it
			panel.add(otherActions, con);
			con.gridy++;
		}
		if (actionPics.getComponentCount() != 0)
		{// only add panel for action pics if components have been added to it
			panel.add(actionPics, con);
			con.gridy++;
		}
		if ( object instanceof GraphObject){
			panel.add(createGraphNavigator(), con);
			con.gridy++;
		}
		JMathField math = new JMathField();
		/*
		InputStream stream = MetaModel.class.getClassLoader().getResourceAsStream("Octave.xml");
		try {
			byte[] fileData = new byte[stream.available()];
			stream.read(fileData);
			//System.out.print(new String(fileData, Charset.defaultCharset()));
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		*/
		panel.add(math, con);
		MathFormula formula = new MathFormula(new MetaModel("Octave.xml"));
		formula.setRootComponent(new MathSequence(formula, "3"));
		math.setFormula(formula);

		/*
		math.addVetoableChangeListener(new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
				System.out.println("@@@@@!!!!!" + evt.getNewValue());
			}
		});
		*/
		con.gridy++;
		if (o instanceof ExpressionObject && ! notebookPanel.isInStudentMode())
		{// there are too many attributes and actions for the expression to put them all in one panel
			// added a tabbed pane to make it more reasonable and avoid scrolling
			// this line moves to the other tab to place components there
			panel = tabTwoContents;
		}
		if ( object instanceof ExpressionObject){
			panel.add(createExpressionModifier(), con);
			con.gridy++;
		}
		//Switch back to tab one if in teacher mode, to allow attribute adjusters to be on the first tab
		if (o instanceof ExpressionObject && ! notebookPanel.isInStudentMode())
			panel = tabOneContents;

		con.fill = GridBagConstraints.HORIZONTAL;
		
		if (o instanceof GraphObject)
		{// there are too many attributes and actions for the graph to put them all in one panel
			// added a tabbed pane to make it more reasonable and avoid scrolling
			// this line moves to the other tab to place components there
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			con.anchor = GridBagConstraints.PAGE_START;
			tabTwoContents.add(panel, con);
			con.anchor = GridBagConstraints.CENTER;
		}

		con.fill = GridBagConstraints.BOTH;
		for (MathObjectAttribute mAtt : o.getAttributes()){
			if ( notebookPanel.isInStudentMode() && mAtt.isStudentEditable() ||
					( ! notebookPanel.isInStudentMode() && mAtt.isUserEditable()) )
			{// only show editing dialog if in teacher mode (not student)
				//or if the attribute has been left student editable
				adjusters.add(getAdjuster(mAtt, notebookPanel, panel));
				if (mAtt instanceof StringAttribute){// make string panels stretch vertically
					con.weighty = 1;
					con.fill = GridBagConstraints.BOTH;
				}
				else{// make all others stretch very little vertically
					con.weighty = 0;
					con.fill = GridBagConstraints.HORIZONTAL;
				}
				panel.add(adjusters.get(adjusters.size() - 1), con);
				con.gridy++;
			}
		}
		
		System.out.println("end att adjusters:" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		con.weighty = 1;
		if (o instanceof GraphObject)
		{// see above comments about tabs for some objects
			panel = tabOneContents;
		}
		for ( ListAttribute list : o.getLists()){
			if ( ( notebookPanel.isInStudentMode() && list.isStudentEditable() ) ||
					( ! notebookPanel.isInStudentMode() && list.isUserEditable()) )
			{// only show editing dialog if in teacher mode (not student)
				//or if the attribute has been left student editable
				listAdjusters.add(new ListAdjuster(list, notebookPanel, panel));
				panel.add(listAdjusters.get(listAdjusters.size() - 1), con);
				con.gridy++;
			}
		}
		System.out.println("end lists:" + + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
		
		if ( panel.getComponentCount() == 0){
			panel.add(new JLabel("No actions for this object"), con);
		}
		panel.revalidate();
		this.pack();
		this.update();
		this.setSize(this.getWidth() + 30, this.getHeight());
		System.out.println("done making props frame" + (new Date().getTime() - notebookPanel.getOpenNotebook().timeAtStart));
	}
	
	public void setObject(MathObject o){
		object = o;
		for ( AdjustmentPanel adjuster : adjusters){
			adjuster.setAttribute(o.getAttributeWithName(adjuster.getAttribute().getName()));
			adjuster.updateData();
		}
		for( ListAdjuster listAdjuster : listAdjusters){
			listAdjuster.setList(o.getListWithName(listAdjuster.getList().getName()));
			listAdjuster.updateData();
		}
	}
	
	public MathObject getObjet(){
		return object;
	}

	public static AdjustmentPanel getAdjuster(MathObjectAttribute mAtt,
			NotebookPanel notebookPanel, JPanel panel){
		AdjustmentPanel p;
		if (mAtt instanceof BooleanAttribute)
			p = new BooleanAdjustmentPanel((BooleanAttribute)mAtt, notebookPanel, panel);
		else if (mAtt instanceof StringAttribute)
			p = new StringAdjustmentPanel((StringAttribute)mAtt, notebookPanel, panel);
		else if (mAtt instanceof ColorAttribute)
			p = new ColorAdjustmentPanel((ColorAttribute)mAtt, notebookPanel, panel );
		else if (mAtt instanceof EnumeratedAttribute)
			p = new EnumeratedAdjuster((EnumeratedAttribute)mAtt, notebookPanel, panel);
		else
			p = new GenericAdjustmentPanel(mAtt, notebookPanel, panel);
		return p;
	}
	
	private JPanel createExpressionModifier(){
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridBagLayout());
		ImageIcon pic;

		new OCButton(ExpressionObject.SUB_IN_VALUE, 4, 1, 0, 0, newPanel){
			public void associatedAction(){ buttonAction(ExpressionObject.SUB_IN_VALUE);}
		};
		new OCButton(ExpressionObject.MODIFY_EXPRESSION, 4, 1, 0, 1, newPanel){
			public void associatedAction(){ buttonAction(ExpressionObject.MODIFY_EXPRESSION);}
		};
		new OCButton(ExpressionObject.MANUALLY_TYPE_STEP, 4, 1, 0, 2, newPanel){
			public void associatedAction(){ buttonAction(ExpressionObject.MANUALLY_TYPE_STEP);}
		};
		new OCButton(ExpressionObject.UNDO_STEP, 4, 1, 0, 3, newPanel){
			public void associatedAction(){ buttonAction(ExpressionObject.UNDO_STEP);}
		};
		
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 4;
		con.gridwidth = 4;
		con.weightx = 1;
		con.weighty = .02;
		newPanel.add(new JLabel("Apply to both sides"), con);
		
		createButton(ExpressionObject.ADD_TO_BOTH_SIDES, 1, 1, 0, 5, newPanel);
		createButton(ExpressionObject.SUBTRACT_FROM_BOTH_SIDES, 1, 1, 1, 5, newPanel);
		createButton(ExpressionObject.MULTIPLY_BOTH_SIDES, 1, 1, 2, 5, newPanel);
		createButton(ExpressionObject.DIVIDE_BOTH_SIDES, 1, 1, 3, 5, newPanel);
		new OCButton(ExpressionObject.OTHER_OPERATIONS, 4, 1, 0, 6, newPanel){
			public void associatedAction(){ buttonAction(ExpressionObject.OTHER_OPERATIONS);}
		};
		return newPanel;
	}

	private JPanel createGraphNavigator(){
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridBagLayout());
		
		createButton(GraphObject.MOVE_UP, 1, 1, 1, 0, newPanel);
		createButton(GraphObject.MOVE_LEFT, 1, 1, 0, 1, newPanel);
		createButton(GraphObject.MOVE_RIGHT, 1, 1, 2, 1, newPanel);
		createButton(GraphObject.ZOOM_IN, 1, 1, 0, 0, newPanel);
		createButton(GraphObject.ZOOM_OUT, 1, 1, 2, 0, newPanel);
		createButton( GraphObject.MOVE_DOWN, 1, 1, 1, 2, newPanel);
		createButton(GraphObject.DEFAULT_GRID, 1, 1, 1, 1, newPanel);
		return newPanel;
	}
	
	private void createButton(final String actionName, int width, int height,
			int gridx, int gridy, JComponent comp){
		ImageIcon pic = getIconForAction(actionName);
		if ( pic != null){
			new OCButton(pic, actionName, width, height, gridx, gridy, comp){
				public void associatedAction(){
					buttonAction(actionName);
				}
			};
		}
		else{// make a button with text on it
			new OCButton(actionName, actionName, width, height, gridx, gridy, comp){
				public void associatedAction(){
					buttonAction(actionName);
				}
			};
		}
	}
	
	private void buttonAction(String s){
		object.performAction(s);
		// the order of these next two lines is important,
		// some properties, such as with the graph are adjusted
		// when the document redraws itself
		if ( ! object.actionWasCancelled()){
			notebookPanel.getCurrentDocViewer().addUndoState();
		}
		DocViewerPanel currentViewer = notebookPanel.getCurrentDocViewer();
		// allows for groups to accurately update the size/position of their members, if they are modified
		// by an action such as send to page left/right/center or any other future action to modify size/
		// position of an object in a group. Only applies to an object that is selected individually
		// at the time of this comment this is achieved with a double click
		if ( currentViewer.getFocusedObject() != null &&
				currentViewer.getFocusedObject().getParentContainer() instanceof Grouping){
			((Grouping)currentViewer.getFocusedObject().getParentContainer()).adjustSizeToFitChildren();
		}
		currentViewer.repaintDoc();
	}

	public void focusPrimaryAttributeField(){
		
		// the graph's primary attribute is a list, so by default focus the last
		// list member that was created
		if (object instanceof GraphObject){
			ListAttribute ex = object.getListWithName(GraphObject.EXPRESSIONS);
			for ( ListAdjuster aPanel : listAdjusters){
				if ( aPanel.getList().equals(ex)){
					aPanel.focusAttributField();
					return;
				}
			}
			return;
		}
		MathObjectAttribute primaryAttribute;
		if (object instanceof TextObject){
			primaryAttribute = object.getAttributeWithName(TextObject.TEXT);
		}
		else if (object instanceof ExpressionObject){
			primaryAttribute = object.getAttributeWithName(ExpressionObject.EXPRESSION);
		}
		else if (object instanceof AnswerBoxObject){
			primaryAttribute = object.getAttributeWithName(AnswerBoxObject.STUDENT_ANSWER);

		}
		else{
			return;
		}
		// some of the properties cannot be adjusted by students, this check is needed
		// so that a non-existent field is not focused causing an error
		if ( ! notebookPanel.isInStudentMode() ||
				(notebookPanel.isInStudentMode() && primaryAttribute.isStudentEditable())){
			for ( AdjustmentPanel aPanel : adjusters){
				if ( aPanel.getAttribute().equals(primaryAttribute)){
					aPanel.focusAttributField();
				}
			}
		}
	}

	public static ImageIcon getIconForAction(String actionName){
		for (int i = 0; i < actionPicInfo.length; i++){
			if (((String)actionPicInfo[i][0]).equals(actionName)){
				return (ImageIcon) actionPicInfo[i][1];
			}
		}
		return null;
	}
	
	public static ImageIcon getIcon(String filename){
		if (filename == null){
			return null;
		}
		try {
			filename = "img/" + filename;
			BufferedImage image = ImageIO.read(ObjectPropertiesFrame.class.getClassLoader().getResourceAsStream(filename));
			return new ImageIcon(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("cannot find image: " + filename);
		}
		return null;
	}

	public void update(){
		for (AdjustmentPanel a : adjusters){
			a.updateData();
		}
		this.revalidate();
		this.repaint();
	}



}
