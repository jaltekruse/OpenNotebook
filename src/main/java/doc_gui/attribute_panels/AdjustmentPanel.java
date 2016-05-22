package doc_gui.attribute_panels;

import javax.swing.JPanel;

import doc.attributes.MathObjectAttribute;
import doc_gui.DocViewerPanel;
import doc_gui.NotebookPanel;

public abstract class AdjustmentPanel<K extends MathObjectAttribute> extends JPanel{

	protected K mAtt;
	protected NotebookPanel notebookPanel;
	protected JPanel parentPanel;

	protected boolean showingDialog;

	public AdjustmentPanel(final K mAtt, NotebookPanel notebookPanel, JPanel p){
		this.mAtt = mAtt;
		this.notebookPanel = notebookPanel;
		parentPanel = p;
		addPanelContent();
	}

	public abstract void addPanelContent();
	
	public abstract void focusAttributField();
	
	public K getAttribute(){
		return mAtt;
	}
	
	public void setAttribute(K att){
		mAtt = att;
	}

	public abstract void updateData();

	public abstract void applyPanelValueToObject();
}