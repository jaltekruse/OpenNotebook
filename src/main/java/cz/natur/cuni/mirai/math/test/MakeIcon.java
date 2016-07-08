package cz.natur.cuni.mirai.math.test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;

import cz.natur.cuni.mirai.math.icon.SymbolIcon;
import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.meta.MetaSymbol;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;

public class MakeIcon {

	public static void texIcons() {
		TeXFormula formula = new TeXFormula(
				"{\\triangleleft}_{\\triangleleft}+"+
				"{\\triangleleft}^{\\triangleleft}+"+
				"{\\frac{\\triangleleft}{\\triangleright}}+"+
				"\\sqrt{\\triangleleft}+\\sqrt[{\\triangleright}]{\\triangleleft}+"+
				"{\\frac{\\sum_{\\triangleright={\\triangleright}}^{\\triangleright}{\\triangleleft}}"+
				"{\\prod_{\\triangleright={\\triangleright}}^{\\triangleright}{\\triangleleft}}}+"+
				"{\\frac{\\int_{\\triangleright}^{\\triangleright}{\\triangleleft} {d\\triangleright}}"+
				"{\\int_{\\nbsp}{\\triangleleft} {d\\triangleright}}}"+
				"lim_{\\triangleright\\rightarrow{\\triangleright}}{\\triangleleft}");
		Icon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 25);
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		icon.paintIcon(new JLabel(), g2, 0, 0); // component can't be null
		File file = new File("functions.png");
		try {
			ImageIO.write(image, "png", file.getAbsoluteFile());
		} catch (IOException ex) {
			//
		}		
	}

	public static void symbolIcons() {
		//SymbolIcon.setType(ToolbarFolder.SMALL);
		MetaModel metaModel = new MetaModel("mathlib.xml");
		MetaGroup groups[] = metaModel.getGroups(MetaModel.OPERATORS);
		MetaComponent symbols[] = groups[0].getComponents();
		for(int i=0;i<symbols.length;i++) {
			SymbolIcon icon = new SymbolIcon((MetaSymbol)symbols[i]);
			BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			icon.paintIcon(new JLabel(), g2, 0, 0);
			File file = new File(symbols[i].getIcon());
			try {
				System.out.println(file);
				ImageIO.write(image, "png", file.getAbsoluteFile());
			} catch (IOException x) {
				x.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		//texIcons();
		symbolIcons();
	}
}
