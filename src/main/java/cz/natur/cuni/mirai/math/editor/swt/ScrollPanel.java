/* ScrollPanel.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */
package cz.natur.cuni.mirai.math.editor.swt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

public class ScrollPanel extends Composite {
	protected Composite embedded = null;
	protected ScrollBar horizontalSlider = null;
	protected ScrollBar verticalSlider = null;

	protected Frame awtFrame = null;
	protected Panel awtPanel = null;
	protected JScrollPane swingScrollPane = null;

	public ScrollPanel(Composite parent, int style) {
		super(parent, style);		
		boolean horizontal = (style & SWT.H_SCROLL) != 0;
		boolean vertical = (style & SWT.V_SCROLL) != 0;

		GridLayout layout = new GridLayout();
		layout.marginWidth=0;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing=0;
		this.setLayout(layout);

		embedded = new Composite(this, SWT.EMBEDDED);
		embedded.setLayoutData(new GridData(GridData.FILL_BOTH));

		awtFrame = SWT_AWT.new_Frame(embedded);
		awtFrame.setBackground(Color.white);
		awtPanel = new Panel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;
			public void update(java.awt.Graphics g) {
				paint(g);
			}
		};
		awtPanel.setBackground(Color.white);
		awtFrame.add(awtPanel);

		swingScrollPane = new JScrollPane();
		swingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		swingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		swingScrollPane.setDoubleBuffered(false);
		swingScrollPane.setBackground(Color.white);

		awtPanel.add(swingScrollPane, BorderLayout.CENTER);

		if (vertical) {
			verticalSlider = getVerticalBar();
			verticalSlider.setIncrement(50);
			verticalSlider.setPageIncrement(250);

			BoundedRangeModel model = swingScrollPane.getVerticalScrollBar().getModel();
			verticalSlider.setMinimum(model.getMinimum());
			verticalSlider.setMaximum(model.getMaximum());
			verticalSlider.setThumb(model.getExtent());

			verticalSlider.addSelectionListener(new VerticalSliderListener());
			swingScrollPane.getVerticalScrollBar().addAdjustmentListener(
					new JScrollBarListener(verticalSlider,
					swingScrollPane.getVerticalScrollBar().getModel()));
		}

		if (horizontal) {
			horizontalSlider = getHorizontalBar();
			verticalSlider.setIncrement(50);

			BoundedRangeModel model = swingScrollPane.getHorizontalScrollBar().getModel();
			horizontalSlider.setMinimum(model.getMinimum());
			horizontalSlider.setMaximum(model.getMaximum());
			horizontalSlider.setThumb(model.getExtent());

			horizontalSlider.addSelectionListener(new HorizontalSliderListener());
			swingScrollPane.getHorizontalScrollBar().addAdjustmentListener(
					new JScrollBarListener(horizontalSlider, 
					swingScrollPane.getHorizontalScrollBar().getModel()));
		}
	}

	public Frame getFrame() {
		return awtFrame;
	}

	public JScrollPane getJScrollPane() {
		return swingScrollPane;
	}

	private class HorizontalSliderListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			swingScrollPane.getHorizontalScrollBar().
				setValue(horizontalSlider.getSelection());
			swingScrollPane.repaint();
		}
	}

	private class VerticalSliderListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			swingScrollPane.getVerticalScrollBar().
				setValue(verticalSlider.getSelection());
			swingScrollPane.repaint();
		}
	}

	private final class JScrollBarListener implements AdjustmentListener {
		private BoundedRangeModel model;
		private ScrollBar slider;

		private JScrollBarListener(ScrollBar slider, BoundedRangeModel model) {
			this.slider = slider;
			this.model = model;
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {	
			if (slider.getDisplay() == null) {
				return;
			}

			slider.getDisplay().syncExec(new Runnable(){
				public void run() {
					int sel = model.getValue();
					int min = model.getMinimum();
					int max = model.getMaximum();
					int thumb = model.getExtent();

					//System.out.println("setValues("+sel+",?,"+max+","+thumb);

					slider.setValues(sel, min, max, thumb, 
						slider.getIncrement(), slider.getPageIncrement());
				}				
			});
		}
	}

}