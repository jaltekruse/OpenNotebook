package doc_gui.attribute_panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import doc.attributes.EnumeratedAttribute;
import doc_gui.DocViewerPanel;

public class EnumeratedAdjuster extends AdjustmentPanel<EnumeratedAttribute>{

	public static final int COMBO_BOX = 1, RADIO_BUTTON_ROW = 2, RADIO_BUTTON_COLOUMN = 3;

	private int displayFormat = 1;

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
	public EnumeratedAdjuster(EnumeratedAttribute mAtt, DocViewerPanel dvp, 
			JPanel parentPanel, int displayFormat){
		super(mAtt, dvp, parentPanel);
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
	public EnumeratedAdjuster(EnumeratedAttribute mAtt, DocViewerPanel dvp, 
			JPanel parentPanel){
		super(mAtt, dvp, parentPanel);
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
					docPanel.addUndoState();
					docPanel.repaint();
				}

			});
			this.add(valueChoices, con);
		}
		else if ( displayFormat == RADIO_BUTTON_ROW){
			
		}
		else if ( displayFormat == RADIO_BUTTON_COLOUMN){

		}
	}

	@Override
	public void focusAttributField() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyPanelValueToObject() {
		// TODO Auto-generated method stub

	}



}
