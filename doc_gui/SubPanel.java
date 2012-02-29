/*
* This file is part of an application developed by Open Education Inc.
* The source code of the entire project is the exclusive property of
* Open Education Inc. If you have received this file in error please
* inform the project leader at altekrusejason@gmail.com to report where
* the file was found and delete it immediately.
*/

package doc_gui;


import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class SubPanel extends JPanel {
	
	/**
	*
	*/
	private static final long serialVersionUID = 1L;
	private TopLevelContainerOld topLevelContainerOld;
	
	public SubPanel(TopLevelContainerOld topLevelComp) {
		this.setLayout(new GridBagLayout());
		this.topLevelContainerOld = topLevelComp;
	}
	
	public TopLevelContainerOld getTopLevelContainer(){
		return topLevelContainerOld;	
	}
}

