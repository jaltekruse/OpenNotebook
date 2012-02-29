package doc_gui.attribute_panels;

import javax.swing.JPanel;

import doc.attributes.MathObjectAttribute;
import doc_gui.DocViewerPanel;

public abstract class AdjustmentPanel<K extends MathObjectAttribute> extends JPanel{

	protected K mAtt;
	protected DocViewerPanel docPanel;
	protected JPanel parentPanel;

	protected boolean showingDialog;

	public AdjustmentPanel(final K mAtt, DocViewerPanel dvp, JPanel p){
		this.mAtt = mAtt;
		docPanel = dvp;
		parentPanel = p;
		addPanelContent();
	}

	public abstract void addPanelContent();
	
	public abstract void focusAttributField();
	
	public K getAttribute(){
		return mAtt;
	}

	public abstract void updateData();

	public abstract void applyPanelValueToObject();
}