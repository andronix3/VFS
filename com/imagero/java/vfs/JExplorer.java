package com.imagero.java.vfs;

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

import com.imagero.java.io.FileNode;

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
