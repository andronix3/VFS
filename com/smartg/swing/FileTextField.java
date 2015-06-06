/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of imagero Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartg.swing;

import java.io.File;

import javax.swing.JFormattedTextField;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position.Bias;

import com.smartg.java.vfs.VFile;
import com.smartg.java.vfs.VfsUtil;

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
