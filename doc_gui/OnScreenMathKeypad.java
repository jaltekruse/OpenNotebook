package doc_gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class OnScreenMathKeypad extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private boolean trigInv;
	private OCButton sin, cos, tan, trigInverse;
	private JComboBox angleUnitSelect;
	private NotebookPanel notebook;
	public static final int PRESS = Integer.MAX_VALUE - 1, RELEASE = Integer.MAX_VALUE - 2;
	
	private static Robot robot;
	static{
		try{
			robot = new Robot(); 
		}catch( Exception e){
			// system robot is not accessible
		}
	}


	OnScreenMathKeypad(final NotebookPanel notebook) {
		this.notebook = notebook;
		this.setFocusable(false);
		trigInv = false;
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(100, 100));
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		new OCButton("^", 1, 1, 1, 2, this);
		new OCButton("^-1", 1, 1, 1, 1,this);
		new OCButton(".", 1, 1, 3, 6, this);
		new OCButton("+", 1, 2, 4, 3,this);
		new OCButton("-", 1, 1, 4, 2, this);
		new OCButton("*", 1, 1, 3, 2, this);
		new OCButton("/", 1, 1, 2, 2, this);
		new OCButton("(", 1, 1, 3, 1, this);
		new OCButton(")", 1, 1, 4, 1, this);
		new OCButton("=", "variable assignment", 1, 1, 6, 0, this);
		new OCButton("ln(", 1, 1, 0, 6, this);
		new OCButton("log(", 1, 1, 0, 3, this);
		new OCButton("sqrt(", 1, 1, 0, 4, this);
		new OCButton("e", "euler's number", 1, 1, 0, 5, this);
		new OCButton("0", 2, 1, 1, 6, this);
		new OCButton("1", 1, 1, 1, 5, this);
		new OCButton("2", 1, 1, 2, 5, this);
		new OCButton("3", 1, 1, 3, 5, this);
		new OCButton("4", 1, 1, 1, 4, this);
		new OCButton("5", 1, 1, 2, 4, this);
		new OCButton("6", 1, 1, 3, 4, this);
		new OCButton("7", 1, 1, 1, 3, this);
		new OCButton("8", 1, 1, 2, 3, this);
		new OCButton("9", 1, 1, 3, 3, this);
		new OCButton(",", 1, 1, 2, 1, this);
		new OCButton("x", 1, 1, 5, 1, this);
		new OCButton("y", 1, 1, 5, 2, this);
		new OCButton("z", 1, 1, 5, 3, this);
		new OCButton("a", "variable for storage", 1, 1, 5, 4, this);
		new OCButton("b", "variable for stroage", 1, 1, 5, 5, this);
		new OCButton("c", 1, 1, 5, 6, this);
		new OCButton("\u03C0", 1, 1, 5, 0, this);
		new OCButton("i", 1, 1, 6, 1, this);
		new OCButton("j", 1, 1, 6, 2, this);
		new OCButton("k", 1, 1, 6, 3, this);
		new OCButton("r", 1, 1, 6, 4, this);
		new OCButton("s", 1, 1, 6, 5, this);
		new OCButton("t", 1, 1, 6, 6, this);
		new OCButton("\u03B8", 1, 1, 2, 7, this );
		new OCButton("m", 1, 1, 3, 7, this);
		new OCButton("n", 1, 1, 4, 7, this);
		new OCButton("p", 1, 1, 5, 7, this);
		new OCButton("q", 1, 1, 6, 7, this);
		new OCButton("randomInt(", 2, 1, 0, 7, this);
		
		//OCButton y = new OCButton("y", 1, 1, 7, 4, this);
		sin = new OCButton("sin(", 1, 1, 1, 0, this);
		cos = new OCButton("cos(", 1, 1, 2, 0, this);
		tan = new OCButton("tan(", 1, 1, 3, 0, this);
		trigInverse = new OCButton("Inv", "Make trig functions inverse.", 1, 1, 4, 0, this){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void associatedAction(){
					changeTrigButtons();
			}
		};

		new OCButton("<-", "backspace", 1, 1, 0, 2, this) {

			public void associatedAction() {
				if ( ! OCButton.textComponentHasFocus()){
					return;
				}
				final JTextComponent currField = OCButton.getFocusedComponent();
				if (currField != null) {
					String currText = currField.getText();
					final int caretPos = currField.getCaretPosition();

					if (currText.equals("")) {
						// do nothing
					}

					if (caretPos > 0) {
						if (currField.getSelectionStart() == currField
								.getSelectionEnd()) {
							currText = currField.getText();
							String tempText = currText.substring(0,
									caretPos - 1);
							tempText += currText.substring(caretPos, currText
									.length());
							currField.setText(tempText);
							currField.requestFocus();
							currField.setCaretPosition(caretPos - 1);
						} else {
							final int selectStart = currField.getSelectionStart();
							final int selectEnd = currField.getSelectionEnd();
							String tempText = currText
									.substring(0, selectStart);
							tempText += currText.substring(selectEnd, currText
									.length());
							currField.setText(tempText);
							currField.requestFocus();
							currField.setCaretPosition(selectStart);
						}
					}
				}
			}
		};

		String[] angleUnits = {"rad", "deg", "grad"};
		angleUnitSelect = new JComboBox(angleUnits);
		angleUnitSelect.setFocusable(false);

		angleUnitSelect.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
				int units = angleUnitSelect.getSelectedIndex() + 1;
				if (units == 1){
				}

				else if (units == 2){
				}
				else if (units == 3){
				}
			}
		});

		GridBagConstraints bCon = new GridBagConstraints();
		bCon.gridx = 0;
		bCon.gridy = 0;
		bCon.insets = new Insets(2,2,2,2);

		this.add(angleUnitSelect, bCon);

		new OCButton("CL", "Clear all in the current field.", 1, 1, 0, 1, this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void associatedAction() {
				
				if ( ! OCButton.textComponentHasFocus()){
					return;
				}
				final JTextComponent currField = OCButton.getFocusedComponent();
				currField.setText("");
			}
		};

		new OCButton("Enter", "Evaluate the current field.", 1, 2, 4, 5, this){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void associatedAction(){
				if ( robot != null){
					robot.keyPress(KeyEvent.VK_ENTER);
					robot.keyRelease(KeyEvent.VK_ENTER);
				}
			}
		}; 
	}

	private void changeTrigButtons(){
		trigInv = !trigInv;
		if (trigInv){
			this.remove(sin);
			this.remove(cos);
			this.remove(tan);
			this.remove(trigInverse);
			sin = new OCButton("sin-1(", "inverse sine", 1, 1, 1, 0, this);
			cos = new OCButton("cos-1(", "inverse cosine", 1, 1, 2, 0, this);
			tan = new OCButton("tan-1(", "inverse tangent", 1, 1, 3, 0, this);
			trigInverse = new OCButton("Reg", "Return to regular trig functions.", 1, 1, 4, 0, this){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void associatedAction(){
					changeTrigButtons();
				}
			};
			this.revalidate();
			this.repaint();
		}
		else{
			this.remove(sin);
			this.remove(cos);
			this.remove(tan);
			this.remove(trigInverse);
			sin = new OCButton("sin(", 1, 1, 1, 0, this);
			cos = new OCButton("cos(", 1, 1, 2, 0, this);
			tan = new OCButton("tan(", 1, 1, 3, 0, this);
			trigInverse = new OCButton("Inv", "Make trig functions inverse.", 1, 1, 4, 0, this){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void associatedAction(){
					changeTrigButtons();
				}
			};
			this.revalidate();
			this.repaint();
		}
	}
}