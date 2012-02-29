package doc_gui.attribute_panels;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import doc.attributes.BooleanAttribute;
import doc_gui.DocViewerPanel;

public class BooleanAdjustmentPanel extends AdjustmentPanel<BooleanAttribute>{

	private JCheckBox checkbox;

	public BooleanAdjustmentPanel(BooleanAttribute mAtt,
			DocViewerPanel dvp, JPanel p) {
		super(mAtt, dvp, p);
	}

	@Override
	public void updateData() {
		checkbox.setSelected(mAtt.getValue());
	}

	@Override
	public void addPanelContent() {
		setLayout(new GridLayout(0,1));
		checkbox = new JCheckBox(mAtt.getName());

		checkbox.setSelected(mAtt.getValue());
		checkbox.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED){
					mAtt.setValue(true);
					docPanel.repaint();
					docPanel.addUndoState();
					docPanel.updateObjectToolFrame();
				}
				else{
					mAtt.setValue(false);
					docPanel.repaint();
					docPanel.addUndoState();
					docPanel.updateObjectToolFrame();
				}
			}

		});
		add(checkbox);

	}

	@Override
	public void applyPanelValueToObject() {
		// TODO Auto-generated method stub
		mAtt.setValue(true);
		docPanel.repaint();
	}

	@Override
	public void focusAttributField() {
		// TODO Auto-generated method stub
		checkbox.requestFocus();
	}

}