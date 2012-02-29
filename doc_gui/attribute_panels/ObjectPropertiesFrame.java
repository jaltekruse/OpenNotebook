/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

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
import doc_gui.OCButton;

public class ObjectPropertiesFrame extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 648301575472116469L;
	
	private JPanel mainPanel;
	private DocViewerPanel docPanel; 
	private Vector<AdjustmentPanel> adjusters;
	private Vector<ListAdjuster> listAdjusters;
	private MathObject object;
	private String[] graphNavActions = {GraphObject.MOVE_DOWN, GraphObject.MOVE_UP,
			GraphObject.MOVE_LEFT, GraphObject.MOVE_RIGHT, GraphObject.DEFAULT_GRID};
	private String[] expressionOpActions = {ExpressionObject.ADD_TO_BOTH_SIDES,
			ExpressionObject.MULTIPLY_BOTH_SIDES, ExpressionObject.DIVIDE_BOTH_SIDES,
			ExpressionObject.SUBTRACT_FROM_BOTH_SIDES, ExpressionObject.OTHER_OPERATIONS};
	JScrollPane scrollPane;

	public ObjectPropertiesFrame(DocViewerPanel dvp){
		super("tools",
				true, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable
		docPanel = dvp;
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		adjusters = new Vector<AdjustmentPanel>();
		listAdjusters = new Vector<ListAdjuster>();
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(scrollPane);
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
					panel.add(getAdjuster(mAtt, docPanel, panel), con);
					con.gridy++;
				}
			}
		}
		return panel;
	}

	public void generatePanel(MathObject o){
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
		
		if (o instanceof GraphObject || o instanceof ExpressionObject)
		{// there are too many attributes and actions for the graph to put them all in one panel
			// add a tabbed pane to make it more reasonable and avoid scrolling
			panelTabs = new JTabbedPane();
			this.getContentPane().add(panelTabs);
			tabOneContents = new JPanel();
			tabOneContents.setLayout(new GridBagLayout());
			tabTwoContents = new JPanel();
			tabTwoContents.setLayout(new GridBagLayout());
			JScrollPane tabScrollPane = new JScrollPane(panelTabs);
			tabScrollPane.getVerticalScrollBar().setUnitIncrement(16);
			tabScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
			tabScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			if ( o instanceof GraphObject){
				panelTabs.add("Nav", tabOneContents);
				panelTabs.add("Grid", tabTwoContents);
				panel = tabOneContents;
			}
			else if (o instanceof ExpressionObject){
				panelTabs.add("Expression", tabOneContents);
				panelTabs.add("Solve", tabTwoContents);
				panel = tabTwoContents;
			}
			this.getContentPane().add(tabScrollPane);
			
		}
		else{
			this.getContentPane().add(scrollPane);
		}
		
		con.weighty = .01;
		JPanel actionPics = new JPanel();
		actionPics.setLayout(new GridLayout(0, 3, 4, 4));
		JPanel otherActions = new JPanel();
		otherActions.setLayout(new GridLayout(0, 1, 4, 4));

		ImageIcon pic;
		JButton button;
		if ( ! docPanel.isInStudentMode()){
			for (final String s : o.getActions()){
				pic = getIconForAction(s);
				if (pic != null){
					button = new JButton(pic);
					button.setMargin(new Insets(2, 2, 2, 2));
					button.setMaximumSize(new Dimension(20,20));
					button.setToolTipText(s);
					actionPics.add(button);
				}
				else{
					button = new JButton(s);
					button.setMargin(new Insets(2, 2, 2, 2));
					otherActions.add(button);
				}
				button.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						buttonAction(s);
					}
				});
			}
		}

		boolean skipAction;
		for (final String s : o.getStudentActions()){
			skipAction = false;
			if ( object instanceof GraphObject)
			{// skip the actions for navigating around the graph
				// they are added in a separate panel to make their
				// use more intuitive
				for ( String str : graphNavActions){
					if (s.equals(str)){
						skipAction = true;
						break;
					}
				}
			}
			if (object instanceof ExpressionObject)
			{// skip the operations that change both sides, they are added
				// in a separate panel to make the list of actions smaller
				for ( String str : expressionOpActions){
					if (s.equals(str)){
						skipAction = true;
						break;
					}
				}
			}
			if ( skipAction ){
				continue;
			}
			pic = getIconForAction(s);
			if (pic != null){
				button = new JButton(pic);
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setToolTipText(s);
				actionPics.add(button);
			}
			else{
				button = new JButton(s);
				button.setMargin(new Insets(2, 2, 2, 2));
				otherActions.add(button);
			}
			button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					buttonAction(s);
					docPanel.repaint();
					ObjectPropertiesFrame.this.update();
				}

			});
		}
		
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
		if ( object instanceof ExpressionObject){
			panel.add(createExpressionModifier(), con);
			con.gridy++;
		}

		con.fill = GridBagConstraints.HORIZONTAL;
		for ( ListAttribute list : o.getLists()){
			if ( ( docPanel.isInStudentMode() && list.isStudentEditable() ) ||
					( ! docPanel.isInStudentMode() && list.isUserEditable()) )
			{// only show editing dialog if in teacher mode (not student)
				//or if the attribute has been left student editable
				listAdjusters.add(new ListAdjuster(list, docPanel, panel));
				panel.add(listAdjusters.get(listAdjusters.size() - 1), con);
				con.gridy++;
			}
		}
		
		if (o instanceof GraphObject)
		{// there are too many attributes and actions for the graph to put them all in one panel
			// added a tabbed pane to make it more reasonable and avoid scrolling
			// this line moves to the other tab to place components there
			panel = tabTwoContents;
		}
		else if (o instanceof ExpressionObject)
		{// there are too many attributes and actions for the expression to put them all in one panel
			// added a tabbed pane to make it more reasonable and avoid scrolling
			// this line moves to the other tab to place components there
			panel = tabOneContents;
		}

		con.fill = GridBagConstraints.BOTH;
		for (MathObjectAttribute mAtt : o.getAttributes()){
			if ( ( docPanel.isInStudentMode() && mAtt.isStudentEditable() ) ||
					( ! docPanel.isInStudentMode() && mAtt.isUserEditable()) )
			{// only show editing dialog if in teacher mode (not student)
				//or if the attribute has been left student editable
				adjusters.add(getAdjuster(mAtt, docPanel, panel));
				if (mAtt instanceof StringAttribute){// make string panels stretch vertically
					con.weighty = 1;
				}
				else{// make all others stretch very little horizontally
					con.weighty = .01;
				}
				panel.add(adjusters.get(adjusters.size() - 1), con);
				con.gridy++;
			}
		}
		if ( panel.getComponentCount() == 0){
			panel.add(new JLabel("No actions for this object"), con);
		}
		panel.revalidate();
		this.pack();
		this.update();
		this.setSize(this.getWidth() + 30, this.getHeight());
	}

	public static AdjustmentPanel getAdjuster(MathObjectAttribute mAtt,
			DocViewerPanel docPanel, JPanel panel){
		AdjustmentPanel p;
		if (mAtt instanceof BooleanAttribute){
			p = new BooleanAdjustmentPanel((BooleanAttribute)mAtt, docPanel, panel);
		}
		else if (mAtt instanceof StringAttribute){
			p = new StringAdjustmentPanel((StringAttribute)mAtt, docPanel, panel);
		}
		else if (mAtt instanceof ColorAttribute){
			p = new ColorAdjustmentPanel((ColorAttribute)mAtt, docPanel, panel );
		}
		else if (mAtt instanceof EnumeratedAttribute){
			p = new EnumeratedAdjuster((EnumeratedAttribute)mAtt, docPanel, panel);
		}
		else{
			p = new GenericAdjustmentPanel(mAtt, docPanel, panel);
		}
		return p;
	}
	
	private JPanel createExpressionModifier(){
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridBagLayout());
		ImageIcon pic;

		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 4;
		con.weightx = 1;
		con.weighty = .02;
		newPanel.add(new JLabel("Apply to both sides"), con);
		pic = getIconForAction(ExpressionObject.ADD_TO_BOTH_SIDES);
		OCButton add = new OCButton(pic, ExpressionObject.ADD_TO_BOTH_SIDES, 1, 1, 0, 1, newPanel){
			public void associatedAction(){
				buttonAction(ExpressionObject.ADD_TO_BOTH_SIDES);
			}
		};
		pic = getIconForAction(ExpressionObject.SUBTRACT_FROM_BOTH_SIDES);
		OCButton subtract = new OCButton(pic, ExpressionObject.SUBTRACT_FROM_BOTH_SIDES, 1, 1, 1, 1, newPanel){
			public void associatedAction(){
				buttonAction(ExpressionObject.SUBTRACT_FROM_BOTH_SIDES);
			}
		};
		pic = getIconForAction(ExpressionObject.MULTIPLY_BOTH_SIDES);
		OCButton multiply = new OCButton(pic, ExpressionObject.MULTIPLY_BOTH_SIDES, 1, 1, 2, 1, newPanel){
			public void associatedAction(){
				buttonAction(ExpressionObject.MULTIPLY_BOTH_SIDES);
			}
		};
		pic = getIconForAction(ExpressionObject.DIVIDE_BOTH_SIDES);
		OCButton divide = new OCButton(pic, ExpressionObject.DIVIDE_BOTH_SIDES, 1, 1, 3, 1, newPanel){
			public void associatedAction(){
				buttonAction(ExpressionObject.DIVIDE_BOTH_SIDES);
			}
		};
		OCButton down = new OCButton(ExpressionObject.OTHER_OPERATIONS, 4, 1, 0, 2, newPanel){
			public void associatedAction(){
				buttonAction(ExpressionObject.OTHER_OPERATIONS);
			}
		};
		return newPanel;
	}

	private JPanel createGraphNavigator(){
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridBagLayout());
		ImageIcon pic;

		pic = getIconForAction(GraphObject.MOVE_UP);
		OCButton up = new OCButton(pic, GraphObject.MOVE_UP, 1, 1, 1, 0, newPanel){
			public void associatedAction(){
				buttonAction(GraphObject.MOVE_UP);
			}
		};
		pic = getIconForAction(GraphObject.MOVE_LEFT);
		OCButton left = new OCButton(pic, GraphObject.MOVE_LEFT, 1, 1, 0, 1, newPanel){
			public void associatedAction(){
				buttonAction(GraphObject.MOVE_LEFT);
			}
		};
		pic = getIconForAction(GraphObject.MOVE_RIGHT);
		OCButton right = new OCButton(pic, GraphObject.MOVE_RIGHT, 1, 1, 2, 1, newPanel){
			public void associatedAction(){
				buttonAction(GraphObject.MOVE_RIGHT);
			}
		};
		pic = getIconForAction(GraphObject.MOVE_DOWN);
		OCButton down = new OCButton(pic, GraphObject.MOVE_DOWN, 1, 1, 1, 2, newPanel){
			public void associatedAction(){
				buttonAction(GraphObject.MOVE_DOWN);
			}
		};

		pic = getIconForAction(GraphObject.DEFAULT_GRID);
		OCButton zoomDefault = new OCButton(pic, GraphObject.DEFAULT_GRID, 1, 1, 1, 1, newPanel){
			public void associatedAction(){
				buttonAction(GraphObject.DEFAULT_GRID);
			}
		};
		
		return newPanel;
	}
	
	private void buttonAction(String s){
		object.performAction(s);
		// the order of these next two lines is important,
		// some properties, such as with the graph are adjusted
		// when the document redraws itself
		if ( ! object.actionWasCancelled()){
			docPanel.addUndoState();
		}
		docPanel.repaint();
		ObjectPropertiesFrame.this.update();
		ObjectPropertiesFrame.this.repaint();
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
		if ( ! docPanel.isInStudentMode() ||
				(docPanel.isInStudentMode() && primaryAttribute.isStudentEditable())){
			for ( AdjustmentPanel aPanel : adjusters){
				if ( aPanel.getAttribute().equals(primaryAttribute)){
					aPanel.focusAttributField();
				}
			}
		}
	}

	public ImageIcon getIconForAction(String actionName){
		String filename = null;
		if (actionName.equals(MathObject.MAKE_SQUARE)){
			filename = "makeSquare.png";
		}
		else if (actionName.equals(MathObject.ADJUST_SIZE_AND_POSITION)){
			filename = "adjustSizeAndPosition.png";
		}
		else if (actionName.equals(TriangleObject.MAKE_ISOSCELES_TRIANGLE)){
			filename = "makeIsosceles.png";
		}
		else if (actionName.equals(TriangleObject.MAKE_RIGHT_TRIANGLE)){
			filename = "makeRightTriangle.png";
		}
		else if (actionName.equals(PolygonObject.FLIP_HORIZONTALLY)){
			filename = "flipHorizontally.png";
		}
		else if (actionName.equals(PolygonObject.FLIP_VERTICALLY)){
			filename = "flipVertically.png";
		}
		else if (actionName.equals(GraphObject.ZOOM_IN)){
			filename = "smallZoomIn.png";
		}
		else if (actionName.equals(GraphObject.ZOOM_OUT)){
			filename = "smallZoomOut.png";
		}
		else if (actionName.equals(GraphObject.DEFAULT_GRID)){
			filename = "defaultGrid.png";
		}
		else if (actionName.equals(GraphObject.MOVE_LEFT)){
			filename = "moveGraphLeft.png";
		}
		else if (actionName.equals(GraphObject.MOVE_RIGHT)){
			filename = "moveGraphRight.png";
		}
		else if (actionName.equals(GraphObject.MOVE_UP)){
			filename = "moveGraphUp.png";
		}
		else if (actionName.equals(GraphObject.MOVE_DOWN)){
			filename = "moveGraphDown.png";
		}
		else if (actionName.equals(Grouping.BRING_TO_BOTTOM)){
			filename = "alignBottom.png";
		}
		else if (actionName.equals(Grouping.BRING_TO_TOP)){
			filename = "alignTop.png";
		}
		else if (actionName.equals(Grouping.BRING_TO_LEFT)){
			filename = "alignLeft.png";
		}
		else if (actionName.equals(Grouping.BRING_TO_RIGHT)){
			filename = "alignRight.png";
		}
		else if (actionName.equals(ExpressionObject.ADD_TO_BOTH_SIDES)){
			filename = "addition.png";
		}
		else if (actionName.equals(ExpressionObject.SUBTRACT_FROM_BOTH_SIDES)){
			filename = "subraction.png";
		}
		else if (actionName.equals(ExpressionObject.DIVIDE_BOTH_SIDES)){
			filename = "division.png";
		}
		else if (actionName.equals(ExpressionObject.MULTIPLY_BOTH_SIDES)){
			filename = "multiplication.png";
		}
		else{
			return null;
		}

		try {
			filename = "img/" + filename;
			BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
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
