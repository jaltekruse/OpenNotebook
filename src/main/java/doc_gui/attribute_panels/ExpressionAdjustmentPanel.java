package doc_gui.attribute_panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cz.natur.cuni.mirai.math.algebra.MiraiParser;
import cz.natur.cuni.mirai.math.editor.JMathField;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathModel;
import cz.natur.cuni.mirai.math.model.MathSequence;
import doc.attributes.AttributeException;
import doc.attributes.ExpressionAttribute;
import doc.attributes.StringAttribute;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;


public class ExpressionAdjustmentPanel extends AdjustmentPanel<ExpressionAttribute>{

	JMathField mathField;
	private static final MathModel mathModel = new MathModel(new MetaModel("Octave.xml"));
	//private JScrollPane scrollPane;
	//private boolean enterJustPressed;

	public ExpressionAdjustmentPanel(ExpressionAttribute mAtt,
															 NotebookPanel notebookPanel, JPanel p) {
		super(mAtt, notebookPanel, p);
		//enterJustPressed = false;
	}

	@Override
	public void addPanelContent() {
		setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.weightx = .5;
		con.gridx = 0;
		con.gridwidth = 1;
		con.gridy = 0;
		con.insets = new Insets(2, 5, 0, 5);
		add(new JLabel(mAtt.getName()), con);

		JButton keyboard = new JButton(ObjectPropertiesFrame.SMALL_KEYBOARD_IMAGE);
		keyboard.setToolTipText("Show On-Screen Keyboard");
		keyboard.setMargin(new Insets(3, 3, 3, 3));
		keyboard.setFocusable(false);
		con.insets = new Insets(2, 2, 2, 2);
		con.gridx = 1;
		con.weightx = .1;
		con.weighty = .1;
		con.gridheight = 1;
		add(keyboard, con);
		keyboard.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				notebookPanel.getCurrentDocViewer().setOnScreenKeyBoardVisible(true);
			}
		});

		JButton applyChanges = new JButton("Apply");
		applyChanges.setToolTipText("Value can also be applied by hitting Enter");
		applyChanges.setMargin(new Insets(5, 3, 5, 3));
		applyChanges.setFocusable(false);
		con.gridx = 2;
		con.weightx = .1;
		con.weighty = .1;
		con.gridheight = 1;
		add(applyChanges, con);
		applyChanges.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyPanelValueToObject();
			}
		});

		con.fill = GridBagConstraints.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		con.gridwidth = 3;
		con.gridy = 1;
		con.gridx = 0;
		con.gridheight = 3;
		mathField = new JMathField();
		add(mathField, con);
		MathFormula formula = new MathFormula(mathModel.getMetaModel());
		formula.setRootComponent(new MathSequence(MiraiParser.parse(mathModel, mAtt.getValue())));
		mathField.setFormula(formula);
		mathField.update();
		con.gridy++;
		//scrollPane = new JScrollPane(textArea);
		//scrollPane.setWheelScrollingEnabled(false);
		con.insets = new Insets(0, 5, 2, 0);
		/*
		add(scrollPane, con);
		scrollPane.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Point componentPoint = SwingUtilities.convertPoint(
						scrollPane,
						e.getPoint(),
						parentPanel);
				parentPanel.dispatchEvent(new MouseWheelEvent(parentPanel,
						e.getID(),
						e.getWhen(),
						e.getModifiers(),
						componentPoint.x,
						componentPoint.y,
						e.getClickCount(),
						e.isPopupTrigger(),
						e.getScrollType(),
						e.getScrollAmount(),
						e.getWheelRotation()));
			}

		});
		*/

	}

	@Override
	public void updateData() {
		MathFormula formula = new MathFormula(mathModel.getMetaModel());
		formula.setRootComponent(new MathSequence(MiraiParser.parse(mathModel, mAtt.getValue())));
		mathField.setFormula(formula);
		mathField.update();
	}

	@Override
	public void applyPanelValueToObject() {
		String formulaLatex = mathField.getFormula().getRootComponent().toString();
		try {
			if ( mAtt.getParentObject() == null && ! mAtt.getValue().equals(formulaLatex)){
				mAtt.setValue(formulaLatex);
				notebookPanel.getCurrentDocViewer().addUndoState();
			}
			else{
				if ( ! mAtt.getValue().equals(formulaLatex) &&
						mAtt.getParentObject().setAttributeValue(mAtt.getName(), formulaLatex))
				{// if setting the value was successful
					notebookPanel.getCurrentDocViewer().addUndoState();
				}
			}
			notebookPanel.getCurrentDocViewer().repaintDoc();
		} catch (AttributeException e) {
			// TODO Auto-generated catch block
			if (!showingDialog){
				showingDialog = true;
				JOptionPane.showMessageDialog(null,
						e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				showingDialog = false;
			}
		}
	}

	@Override
	public void focusAttributField() {
		//mathField.requestFocus();
	}

}
