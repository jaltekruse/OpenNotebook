/*
* This file is part of an application developed by Open Education Inc.
* The source code of the entire project is the exclusive property of
* Open Education Inc. If you have received this file in error please
* inform the project leader at altekrusejason@gmail.com to report where
* the file was found and delete it immediately.
*/

package doc_gui;

import java.awt.Container;
import java.awt.Point;

import javax.swing.JMenuBar;

public interface TopLevelContainerOld {

public GlassPane getGlassPanel();

public Point getLocationOnScreen();

public Container getContentPane();

public JMenuBar getJMenuBar();

public int getHeight();

public int getWidth();
}