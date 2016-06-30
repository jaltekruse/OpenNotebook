package doc_gui;

import javax.swing.Icon;
import javax.swing.JComponent;

public class ImageSwitcherButton extends OCButton{
	
	public ImageSwitcherButton(Icon bi, String tip, int gridwidth,
			int gridheight, int gridx, int gridy, JComponent comp) {
		super(bi, tip, gridwidth, gridheight, gridx, gridy, comp);
	}
	
	public void updateImage(){}

}