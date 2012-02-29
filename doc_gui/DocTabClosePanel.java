/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui;


import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DocTabClosePanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NotebookPanel notebookPanel;
	private DocViewerPanel viewerPanel;
	private JLabel docName;
	
	public DocTabClosePanel(NotebookPanel n, DocViewerPanel dvp){
		viewerPanel = dvp;
		notebookPanel = n;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		docName = new JLabel();
		if ( viewerPanel.getDoc().getName().length() > 15){
			docName.setText(viewerPanel.getDoc().getName().substring(0, 15) + "...");
		}
		else{
			docName.setText(viewerPanel.getDoc().getName());
		}
		
		this.add(docName);
		this.add(Box.createRigidArea(new Dimension(3,0)));
		
		JButton close = new JButton("x");
		close.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				notebookPanel.closeDocViewer(viewerPanel);
			}
			
		});
		close.setMargin(new Insets(0, 0, 0, 0));
		close.setMaximumSize(new Dimension(15,15));
		
		this.add(close);
		
		this.setOpaque(false);
	}
	
	public void updateField(){
		if ( viewerPanel.getDoc().getName().length() > 15){
			docName.setText(viewerPanel.getDoc().getName().substring(0, 15) + "...");
		}
		else{
			docName.setText(viewerPanel.getDoc().getName());
		}
	}

}
