package doc_gui.attribute_panels;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import doc.attributes.BooleanAttribute;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;

public class BooleanAdjustmentPanel extends AdjustmentPanel<BooleanAttribute>{

	private JCheckBox checkbox;

	public BooleanAdjustmentPanel(BooleanAttribute mAtt,
			NotebookPanel notebookPanel, JPanel p) {
		super(mAtt, notebookPanel, p);
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
				if (e.getStateChange() == ItemEvent.SELECTED){
					mAtt.setValue(true);
					if ( notebookPanel != null){
						notebookPanel.getCurrentDocViewer().repaintDoc();
						notebookPanel.getCurrentDocViewer().addUndoState();
						notebookPanel.getCurrentDocViewer().updateObjectToolFrame();
					}
				}
				else{
					mAtt.setValue(false);
					if ( notebookPanel != null){
						notebookPanel.getCurrentDocViewer().repaintDoc();
						notebookPanel.getCurrentDocViewer().addUndoState();
						notebookPanel.getCurrentDocViewer().updateObjectToolFrame();
					}
				}
			}

		});
		add(checkbox);

	}

	@Override
	public void applyPanelValueToObject() {
		mAtt.setValue(true);
		notebookPanel.getCurrentDocViewer().repaintDoc();
	}

	@Override
	public void focusAttributField() {
		checkbox.requestFocus();
	}

}