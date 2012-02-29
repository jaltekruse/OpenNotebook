package doc_gui.attribute_panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import doc.attributes.AttributeException;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc_gui.DocViewerPanel;

public class ListAdjuster extends JPanel{

	protected ListAttribute lAtt;
	protected DocViewerPanel docPanel;
	protected JPanel parentPanel;
	protected boolean expanded;
	protected AdjustmentPanel last;

	protected boolean showingDialog;

	private static final String NOT_EXPANDED_PIC = "notExpandedList.png", EXPANDED_PIC = "expandedList.png";
	
	public ListAdjuster(final ListAttribute lAtt, DocViewerPanel dvp, JPanel p){
		this.lAtt = lAtt;
		this.setLayout(new GridBagLayout());
		expanded = true;
		docPanel = dvp;
		parentPanel = p;
		addPanelContent();
	}

	public void addPanelContent(){
		this.removeAll();
		
		GridBagConstraints con = new GridBagConstraints();
		con.gridx = 0;
		con.gridy = 0;
		con.fill = GridBagConstraints.NONE;
		
		// this code below ended up not working as well as I thought it would
		// I really need to revalidate the parent frame, but I don't want to limit its
		// functionality in different uses, so for now I'm just going to disable it
//		if (expanded){
//			JButton expandButton = new JButton(docPanel.getIcon(EXPANDED_PIC));
//			expandButton.addActionListener(new ExpandedButtonListener());
//			this.add(expandButton, con);
//		}
//		else{
//			JButton notExpandedButton = new JButton(docPanel.getIcon(NOT_EXPANDED_PIC));
//			notExpandedButton.addActionListener(new NotExpandedButtonListener());
//			this.add(notExpandedButton, con);
//			con.gridx = 1;
//			con.weightx = 1;
//			con.weighty = 1;
//			con.fill = GridBagConstraints.HORIZONTAL;
//			this.add(new JLabel(lAtt.getName()), con);
//			this.revalidate();
//			return;
//		}
		con.gridx = 1;
		con.weightx = 1;
		con.weighty = 1;
		con.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel(lAtt.getName()), con);
		con.gridx = 0;
		con.gridy++;
		con.gridwidth = 2;
		GridBagConstraints pcon = new GridBagConstraints();
		pcon.gridx = 0;
		pcon.gridy = 0;
		pcon.fill = GridBagConstraints.BOTH;
		pcon.weightx = 1;
		pcon.weighty = 1;
		int index = 0;
		for ( MathObjectAttribute mAtt : (Vector<MathObjectAttribute>) lAtt.getValues()){
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
			last = ObjectPropertiesFrame.getAdjuster(mAtt, docPanel, p);
			p.add(last);
			p.add(Box.createRigidArea(new Dimension(3,0)));
			JButton button = new JButton("x");
			button.setMargin(new Insets(0, 0, 0, 0));
			button.addActionListener(new CloseButtonListener(index));
			button.setMaximumSize(new Dimension(15,15));
			p.add(button);
			p.add(Box.createRigidArea(new Dimension(3,0)));
			this.add(p, con);
			con.gridy++;
			index++;
		}
		con.fill = GridBagConstraints.HORIZONTAL;
		JButton addButton = new JButton("Add new");
		addButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					lAtt.addNewValue();
					if ( lAtt.getParentObject() != null)
					{// forces the object property frame to be layed out again
						docPanel.setFocusedObject(lAtt.getParentObject());
					}
					docPanel.updateObjectToolFrame();
					docPanel.repaint();
				} catch (AttributeException e) {
					// TODO Auto-generated catch block
					if (!showingDialog){
						showingDialog = true;
						JOptionPane.showMessageDialog(null,
								e.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
						showingDialog = false;
					}
				}
			}
			
		});
		this.add(addButton, con);
		this.revalidate();
	}
	
	private class CloseButtonListener implements ActionListener{

		private int index;
		
		public CloseButtonListener(int i){
			index = i;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			lAtt.removeValue(index);
			if ( lAtt.getParentObject() != null)
			{// forces the object property frame to be layed out again
				docPanel.setFocusedObject(lAtt.getParentObject());
			}
			docPanel.updateObjectToolFrame();
			docPanel.repaint();
		}
		
	}
	
	private class ExpandedButtonListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			expanded = false;
			addPanelContent();
			docPanel.repaint();
		}
		
	}
	
	private class NotExpandedButtonListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			expanded = true;
			addPanelContent();
			docPanel.repaint();
		}
		
	}
	
	public void focusAttributField(){
		last.focusAttributField();
	}
	
	public ListAttribute getList(){
		return lAtt;
	}

	public void updateData(){
		
	}

	public void applyPanelValueToObject(){
		
	}
}
