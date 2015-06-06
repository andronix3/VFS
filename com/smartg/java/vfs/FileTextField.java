package com.smartg.java.vfs;

import java.io.File;

import javax.swing.JFormattedTextField;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position.Bias;

public class FileTextField extends JFormattedTextField {

    private static final long serialVersionUID = -4835972878596925359L;
    VFile file;
    int start;

    public FileTextField() {
	NavFilter filter = new NavFilter();
	setNavigationFilter(filter);
    }

    protected VFile getFile() {
	return file;
    }

    protected void setFile(VFile file) {
	this.file = file;
	if (file != null) {
	    String protocol = VfsUtil.getProtocol(file, "");
	    start = protocol.length();
	    String absolutePath = file.getAbsolutePath();
	    if(absolutePath.charAt(absolutePath.length() - 1) != File.separatorChar) {
		absolutePath += File.separator;
	    }
	    setText(protocol + absolutePath);
	} else {
	    setText("");
	}
    }

    class NavFilter extends NavigationFilter {

	public void moveDot(FilterBypass fb, int dot, Bias bias) {
	    if (dot < start) {
		dot = start;
	    }
	    fb.moveDot(dot, bias);
	}

	public void setDot(FilterBypass fb, int dot, Bias bias) {
	    int from = getCaret().getDot();
	    String text = getText();
//	    System.out.println("" + from + " -> " + dot);

	    if (from < dot) {
		int right = text.indexOf(File.separatorChar, dot) + 1;
		if (right < dot) {
		    right = dot;
		    if (right < start) {
			right = start;
		    }
		}
//		System.out.println("right");
		fb.setDot(right, bias);
		return;
	    }
	    if (from > dot) {
		int left = text.lastIndexOf(File.separatorChar, Math.max(0, dot - 1)) + 1;
		if (left < start) {
		    left = start;
		}
//		System.out.println("left");
		fb.setDot(left, bias);
		return;
	    }
//	    System.out.println("NOOP");
	}
    }
}
