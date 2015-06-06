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

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.smartg.java.vfs.VFile;

public class JExplorer extends JPanel {


    private static final long serialVersionUID = 7351581817791299401L;

    public final FileTree tree;
    public final FileList list;
    UrlLabel columnHeaderView;

    public JExplorer(VFile root) {
	tree = new FileTree(root);
	list = new FileList(root);
	JScrollPane treeScroll = new JScrollPane(tree);
	JScrollPane listScroll = new JScrollPane(list);
	columnHeaderView = new UrlLabel();
	listScroll.setColumnHeaderView(columnHeaderView);

	JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treeScroll, listScroll);
	tree.addTreeSelectionListener(new TreeSelectionListener() {
	    public void valueChanged(TreeSelectionEvent e) {
		FileNode node = (FileNode) tree.getLastSelectedPathComponent();
		if (node != null) {
		    VFile file = node.file;
		    list.setParentFile(file);
		    columnHeaderView.setFile(file);
		}
	    }
	});

	list.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    VFile selectedValue = list.getSelectedValue();
		    if (selectedValue != null) {
			if (selectedValue.isDirectory()) {
			    tree.setSelectedFile(selectedValue);
			}
		    }
		}
	    }
	});

	list.addKeyListener(new KeyAdapter() {
	    public void keyTyped(KeyEvent e) {
		int keyChar = e.getKeyChar();
		if (keyChar == KeyEvent.VK_ENTER) {
		    VFile selectedValue = list.getSelectedValue();
		    if (selectedValue != null) {
			if (selectedValue.isDirectory()) {
			    tree.setSelectedFile(selectedValue);
			}
		    }
		}
	    }
	});

	setLayout(new BorderLayout());
	add(split);
    }
}
