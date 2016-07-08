/* PngSerializer.java
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
package cz.natur.cuni.mirai.math.algebra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.TeXIcon;

import cz.natur.cuni.mirai.math.model.MathFormula;

public class PngSerializer {

	public static void save(MathFormula formula, String filename) throws IOException {
		TeXIcon renderer = new TeXIcon(TeXConstants.STYLE_DISPLAY, 18);
		TeXSerializer serializer = new TeXSerializer();
		String serializedFormula = serializer.serialize(formula);
		TeXFormula texFormula = new TeXFormula(serializedFormula);
		renderer.setTeXFormula(texFormula);
		BufferedImage image = new BufferedImage(
				renderer.getIconWidth(), renderer.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		renderer.paintIcon(new JLabel(), g2, 0, 0); // component can't be null
		// fill a file path below
		File file = new File(filename);
		try {
			ImageIO.write(image, "png", file.getAbsoluteFile());
		} catch (IOException ex) {}		
	}
}
