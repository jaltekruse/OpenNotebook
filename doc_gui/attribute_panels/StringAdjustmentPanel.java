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

import doc.attributes.AttributeException;
import doc.attributes.StringAttribute;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;


public class StringAdjustmentPanel extends AdjustmentPanel<StringAttribute>{

	private JTextArea textArea;
	private JScrollPane scrollPane;
	private boolean enterJustPressed;

	public StringAdjustmentPanel(StringAttribute mAtt,
			NotebookPanel notebookPanel, JPanel p) {
		super(mAtt, notebookPanel, p);
		enterJustPressed = false;
	}

	@Override
	public void addPanelContent() {
		// to prevent the panel from looking too small, estimate the amount of lines needed to
		// show all of the text
		int numLines = 0;
		if ( getGraphics() != null)
		{// one of these panels is constructed in the background to initialize some resources
			// at that time there is no graphics object to reference to measure text 
			numLines = 	getGraphics().getFontMetrics().stringWidth(mAtt.getValue())/120;
		}
		else{
			numLines = mAtt.getValue().length() / 20;
		}
		if ( numLines < 2){
			numLines = 2;
		}
		textArea = new JTextArea(numLines, 12);
		textArea.setEditable(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(mAtt.getValue());

		textArea.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				applyPanelValueToObject();
			}

		});

		// need to decide if having this panel set with the enter key is worth
		// not being able to include line breaks
		textArea.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ENTER){
					applyPanelValueToObject();
					enterJustPressed = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && enterJustPressed)
				{// had issue with hitting enter to confirm dialog, dialog disappeared but enter key release event
					// was still received here if an object was selected, hence the boolean set in the keypressed
					// method and checked here
					String s = textArea.getText();
					int caretPos = textArea.getCaretPosition() - 1;
					s = s.substring(0, textArea.getCaretPosition() - 1) + s.substring(textArea.getCaretPosition());
					textArea.setText(s);
					textArea.setCaretPosition(caretPos);
					enterJustPressed = false;
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});
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
		scrollPane = new JScrollPane(textArea);
		scrollPane.setWheelScrollingEnabled(false);
		con.insets = new Insets(0, 5, 2, 0);
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

	}

	@Override
	public void updateData() {
		textArea.setText(mAtt.getValue().toString());
	}

	@Override
	public void applyPanelValueToObject() {
		try {
			if ( mAtt.getParentObject() == null && ! mAtt.getValue().equals(textArea.getText())){
				mAtt.setValue(textArea.getText());
				notebookPanel.getCurrentDocViewer().addUndoState();
			}
			else{
				if ( ! mAtt.getValue().equals(textArea.getText()) &&
						mAtt.getParentObject().setAttributeValue(mAtt.getName(), textArea.getText()))
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
		textArea.requestFocus();
	}

}