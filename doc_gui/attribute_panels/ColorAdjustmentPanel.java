package doc_gui.attribute_panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import doc.attributes.ColorAttribute;
import doc.mathobjects.MathObject;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;


public class ColorAdjustmentPanel extends AdjustmentPanel<ColorAttribute>{

	private JLabel colorLabel;
	private JCheckBox checkbox;
	private boolean justChangedColor;
	private static final JColorChooser colorChooser = new JColorChooser();;
	private JButton setColor;

	public ColorAdjustmentPanel(ColorAttribute mAtt,
			NotebookPanel notebookPanel, JPanel p) {
		super(mAtt, notebookPanel, p);
		justChangedColor = false;
	}

	@Override
	public void updateData() {
		colorLabel.setBackground(mAtt.getValue());
		if ( checkbox != null){
			if (((ColorAttribute)mAtt).getValue() != null){
				checkbox.setSelected(true);
			}
			else{
				checkbox.setSelected(false);
			}
		}
	}

	@Override
	public void addPanelContent() {
		setLayout(new GridBagLayout());

		if ( mAtt.getName().equals(MathObject.FILL_COLOR)){
			checkbox = new JCheckBox("fill");

			if (mAtt.getValue() != null)
				checkbox.setSelected(true);
			else
				checkbox.setSelected(false);

			checkbox.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED){
						if (justChangedColor){
							justChangedColor = false;
							return;
						}
						if (mAtt.getValue() == null){
							mAtt.setValue(Color.WHITE);
						}
						colorLabel.setBackground(mAtt.getValue());
						ColorAdjustmentPanel.this.repaint();
						notebookPanel.getCurrentDocViewer().addUndoState();
						notebookPanel.getCurrentDocViewer().repaintDoc();
						notebookPanel.getCurrentDocViewer().updateObjectToolFrame();

					}
					else{
						mAtt.setValue(null);
						colorLabel.setBackground(mAtt.getValue());
						ColorAdjustmentPanel.this.repaint();
						notebookPanel.getCurrentDocViewer().addUndoState();
						notebookPanel.getCurrentDocViewer().repaintDoc();
						notebookPanel.getCurrentDocViewer().updateObjectToolFrame();
					}
				}

			});
		}
		colorLabel = new JLabel("    ");
		colorLabel.setBorder(new LineBorder(Color.BLACK));
		colorLabel.setOpaque(true);
		colorLabel.setBackground(mAtt.getValue());
		colorLabel.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {
				Color c = JColorChooser.showDialog(
						ColorAdjustmentPanel.this,
						"Choose color",
						((ColorAttribute)mAtt).getValue());
				((ColorAttribute)mAtt).setValue(c);
				colorLabel.setBackground(mAtt.getValue());
				if ( checkbox != null){
					if (((ColorAttribute)mAtt).getValue() != null){
						justChangedColor = true;
						checkbox.setSelected(true);
					}
					else{
						checkbox.setSelected(false);
					}
				}
				notebookPanel.getCurrentDocViewer().addUndoState();
				notebookPanel.getCurrentDocViewer().repaintDoc();
			}

			public void mouseReleased(MouseEvent arg0) {}
		});

		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.weightx = 1;
		con.gridx = 0;
		con.gridy = 0;
		con.insets = new Insets(0, 0, 0, 3);
		if (mAtt.getName().equals(MathObject.FILL_COLOR)){
			add(checkbox, con);
		}
		else{
			add(new JLabel(mAtt.getName()), con);
		}
		con.gridy = 0;
		con.gridx = 1;
		con.gridheight = 1;
		add(colorLabel, con);
//		con.gridx = 2;
//		add(setColor, con);
	}

	@Override
	public void applyPanelValueToObject() {
		// this is not applicable for this class, as it is set with a popup
	}

	@Override
	public void focusAttributField() {
		setColor.requestFocus();
	}

}

