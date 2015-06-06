package com.smartg.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JLabel;

import com.smartg.java.vfs.VFile;
import com.smartg.java.vfs.VfsUtil;

public class UrlLabel extends JLabel {

    private static final long serialVersionUID = -1402729180101013321L;

    VFile file;

    int index = 3;
    boolean valid;
    String[] split;
    int[] from, to;
    int[] lengths;

    protected void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g.create();
//	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	FontMetrics fontMetrics = g2d.getFontMetrics();
	String text = getText();
	if (!valid) {
	    split(fontMetrics, text);
	}
	if (index >= 0 && index < from.length) {
	    g.clearRect(from[index], 0, lengths[index], getHeight());
	}
	int descent = fontMetrics.getDescent();
	g2d.drawString(text, 5, getHeight() - descent);
	if (index >= 0 && index < from.length) {
	    g2d.setColor(Color.red);
	    g2d.drawString(split[index], from[index], getHeight() - descent);
	}
    }

    private void split(FontMetrics fm, String text) {
	text = text.replace(File.separatorChar, '=');
	split = text.split("=");
	lengths = new int[split.length];
	from = new int[split.length];
	to = new int[split.length];

	int separatorLength = fm.stringWidth(File.separator);

	for (int i = 0; i < split.length; i++) {
	    lengths[i] = fm.stringWidth(split[i]) + separatorLength;
	}

	from[0] = 5;
	to[0] = 5 + lengths[0];
	for (int i = 1; i < lengths.length; i++) {
	    from[i] = to[i - 1];
	    to[i] = from[i] + lengths[i];
	}
    }

    protected VFile getFile() {
	return file;
    }

    public void setFile(VFile file) {
	this.file = file;
	if (file != null) {
	    String protocol = VfsUtil.getProtocol(file, "");
	    String absolutePath = file.getAbsolutePath();
	    if (absolutePath.charAt(absolutePath.length() - 1) != File.separatorChar) {
		absolutePath += File.separator;
	    }
	    setText(protocol + absolutePath);
	} else {
	    setText("");
	}
	valid = false;
    }
}
