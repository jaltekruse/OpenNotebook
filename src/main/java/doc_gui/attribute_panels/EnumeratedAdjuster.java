package doc_gui.attribute_panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import doc.attributes.EnumeratedAttribute;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;

public class EnumeratedAdjuster extends AdjustmentPanel<EnumeratedAttribute>{

	public static final int COMBO_BOX = 1, RADIO_BUTTON_ROW = 2, RADIO_BUTTON_COLOUMN = 3;

	private int displayFormat = COMBO_BOX;

	/**
	 * Constructor for panel to adjust an EnumeratedAttribute.
	 * 
	 * @param mAtt - the attribute that this panel adjusts
	 * @param dvp - the DocViewerPanel that must be refreshed when the value is set
	 * @param parentPanel - the panel this adjuster will be added to
	 * @param displayFormat - an integer to indicate how the options
	 * are displayed to the user valid values are stored as static ints in this class
	 * DROPDOWN, RADION_BUTTON_ROW, RADIO_BUTTON_COLUMN
	 */
	public EnumeratedAdjuster(EnumeratedAttribute mAtt,
			NotebookPanel notebookPanel, JPanel parentPanel, int displayFormat){
		super(mAtt, notebookPanel, parentPanel);
		this.removeAll();
		this.displayFormat = displayFormat;
		addPanelContent();
	}

	/**
	 * 
	 * @param mAtt - the attribute that this panel adjusts
	 * @param dvp - the DocViewerPanel that must be refreshed when the value is set
	 * @param parentPanel - the panel this adjuster will be added to
	 */
	public EnumeratedAdjuster(EnumeratedAttribute mAtt, NotebookPanel notebookPanel, 
			JPanel parentPanel){
		super(mAtt, notebookPanel, parentPanel);
		this.removeAll();
		addPanelContent();
	}

	@Override
	public void addPanelContent() {
		if ( displayFormat == COMBO_BOX){
			setLayout(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints();
			con.fill = GridBagConstraints.HORIZONTAL;
			con.weightx = .1;
			con.gridx = 0;
			con.gridy = 0;
			con.insets = new Insets(0, 10, 0, 0);
			add(new JLabel(mAtt.getName()), con);
			con.insets = new Insets(0, 10, 0, 5);
			con.weightx = 1;
			con.gridx = 1;
			final JComboBox valueChoices = new JComboBox(mAtt.getPossibleValues());
			valueChoices.setSelectedItem(mAtt.getValue());
			valueChoices.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent ev) {
					mAtt.setValue((String)valueChoices.getSelectedItem());
					if ( notebookPanel != null){
						notebookPanel.getCurrentDocViewer().addUndoState();
						notebookPanel.getCurrentDocViewer().repaintDoc();
					}
				}

			});
			this.add(valueChoices, con);
		}
		else if ( displayFormat == RADIO_BUTTON_ROW){
			setLayout(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints();
			con.fill = GridBagConstraints.HORIZONTAL;
			con.weightx = .1;
			con.gridwidth = mAtt.getPossibleValues().length;
			con.gridx = 0;
			con.gridy = 0;
			con.insets = new Insets(2, 2, 2, 2);
			Font f = new Font("Arial", Font.PLAIN, 12);
			Font smallF = new Font("Arial", Font.PLAIN, 11);
			JLabel label = new JLabel(mAtt.getName());
			label.setFont(f);
			add(label, con);
			con.gridwidth = 1;
			con.weightx = 1;
			con.gridy++;
			
			final ButtonGroup group = new ButtonGroup();
			for ( final String s : mAtt.getPossibleValues()){
				final JRadioButton button = new JRadioButton(s);
				button.addActionListener(new ActionListener(){
	
					@Override
					public void actionPerformed(ActionEvent ev) {
						mAtt.setValue(s);
						if ( notebookPanel != null){
							notebookPanel.getCurrentDocViewer().addUndoState();
							notebookPanel.getCurrentDocViewer().repaintDoc();
						}
					}
	
				});
				if ( mAtt.getValue().equals(s)){
					button.setSelected(true);
				}
				button.setFont(smallF);
				this.add(button, con);
				group.add(button);
				con.gridx++;
			}
		}
		else if ( displayFormat == RADIO_BUTTON_COLOUMN){

		}
	}

	@Override
	public void focusAttributField() {

	}

	@Override
	public void updateData() {

	}

	@Override
	public void applyPanelValueToObject() {

	}



}
