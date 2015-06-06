package com.smartg.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.imagero.java.util.Debug;
import com.smartg.java.io.DirectoryNode;
import com.smartg.java.io.FileNode;
import com.smartg.java.util.Stack;
import com.smartg.java.vfs.VFile;

public class FileTree extends JTree {

    private static final long serialVersionUID = 4446276117292063030L;

    Stack<FileNode> jobs = new Stack<FileNode>();

    Timer updateTimer = new Timer(10, new ActionListener() {

	public void actionPerformed(ActionEvent e) {
	    if (selectedNode != null) {
		FileNode dn = selectedNode;
		Enumeration<VFile> nodeUpdater = dn.getNodeUpdater();
		if (nodeUpdater != null) {
		    if (nodeUpdater.hasMoreElements()) {
			int p = 0;
			int cnt = 10;
			int[] indices = new int[cnt];
			for (int i = 0; i < cnt; i++) {
			    if (nodeUpdater.hasMoreElements()) {
				VFile next = nodeUpdater.nextElement();
				if (dn.add(next)) {
				    indices[p++] = dn.getChildCount() - 1;
				}
			    } else {
				break;
			    }
			}
			indices = Arrays.copyOf(indices, p);
			DefaultTreeModel model = (DefaultTreeModel) getModel();
			model.nodesWereInserted(dn, indices);
		    } else {
			selectedNode = null;
			dn.setNeedUpdate(false);
			if (dn.getChildCount() == 1) {
			    FileNode child = (FileNode) dn.getChildAt(0);
			    expandPath(new TreePath(child.getPath()));
			}
		    }
		} else {
		    selectedNode = null;
		}
	    } else {
		if (!jobs.isEmpty()) {
		    selectedNode = jobs.pop();
		} else {
		    updateTimer.stop();
		}
	    }
	}
    });

    FileNode selectedNode;

    @Override
    public void setCellRenderer(TreeCellRenderer x) {
    }

    public void configureCellRendererComponent(DefaultTreeCellRenderer renderer, Font font) {
	super.setCellRenderer(new Renderer(renderer, font));
    }

    public FileTree(VFile dir) {
	updateTimer.setRepeats(true);

	super.setCellRenderer(new Renderer());

	FileNode root = new DirectoryNode(dir);
	setModel(new DefaultTreeModel(root));
	addTreeExpansionListener(new SingleChildExpander());
	addTreeWillExpandListener(new TreeWillExpandListener() {
	    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		final TreePath path = e.getPath();
		Object lastPathComponent = path.getLastPathComponent();
		if (lastPathComponent instanceof DirectoryNode) {
		    final DirectoryNode node = (DirectoryNode) lastPathComponent;
		    if (node.isNeedUpdate()) {
			if (selectedNode != null) {
			    jobs.push(selectedNode);
			}
			selectedNode = node;
			updateTimer.restart();
		    }
		}
	    }

	    public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {

	    }
	});
	selectedNode = root;
	updateTimer.restart();

    }

    @Override
    public void setModel(TreeModel newModel) {
	super.setModel(newModel);
    }

    public void setRoot(VFile f) {
	DefaultTreeModel model = (DefaultTreeModel) getModel();
	model.setRoot(new DirectoryNode(f));
    }

    public void setSelectedFile(VFile f) {
	TreePath path = createPath(f);
	setSelectionPath(path);
    }

    private TreePath createPath(VFile f) {
	// Debug.println("CreatePath:");
	// Debug.println(f.getAbsolutePath());
	DefaultTreeModel model = (DefaultTreeModel) getModel();
	FileNode root = (FileNode) model.getRoot();
	ArrayList<VFile> v = addToVector(f);
	Debug.println("");

	int size = v.size();
	FileNode node = root;
	int rootIndex = -1;
	for (int i = 0; i < size; i++) {
	    VFile vf = v.get(i);
	    if (root.file.equals(vf)) {
		rootIndex = i;
		break;
	    }
	}

	if (rootIndex >= 0) {
	    ArrayList<FileNode> v2 = new ArrayList<FileNode>();
	    v2.add(root);
	    int start = rootIndex + 1;
	    addPathElements(v, node, v2, start);
	    FileNode[] tps = new FileNode[v2.size()];
	    v2.toArray(tps);
	    return new TreePath(tps);
	}
	VFile vf = v.get(0);
	int rootChildCount = root.getChildCount();
	for (int i = 0; i < rootChildCount; i++) {
	    FileNode child = (FileNode) root.getChildAt(i);
	    int k = child.indexOf(vf);
	    if (k >= 0) {
		ArrayList<FileNode> v2 = new ArrayList<FileNode>();
		v2.add(root);
		v2.add(child);
		addPathElements(v, child, v2, 0);
		FileNode[] tps = new FileNode[v2.size()];
		v2.toArray(tps);
		return new TreePath(tps);
	    }
	}
	return null;
    }

    private void addPathElements(ArrayList<VFile> v, FileNode node, ArrayList<FileNode> dest, int start) {
	int size = v.size();
	for (int i = start; i < size; i++) {
	    VFile vf = v.get(i);
	    int k = node.indexOf(vf);
	    if (k >= 0) {
		node = (FileNode) node.getChildAt(k);
		dest.add(node);
	    }
	}
    }

    private ArrayList<VFile> addToVector(VFile f) {
	ArrayList<VFile> v = new ArrayList<VFile>();
	while (f != null) {
	    v.add(0, f);
	    Debug.print(f.getName());
	    f = f.getParentFile();
	    if (f != null) {
		Debug.print("\t");
	    }
	}
	return v;
    }

    private final class SingleChildExpander implements TreeExpansionListener {
	public void treeExpanded(TreeExpansionEvent e) {
	    TreePath path = e.getPath();
	    Object lastPathComponent = path.getLastPathComponent();
	    DirectoryNode dnode = (DirectoryNode) lastPathComponent;
	    int childCount = dnode.getChildCount();
	    if (childCount == 1) {
		DirectoryNode child = (DirectoryNode) dnode.getChildAt(0);
		expandPath(createPath(child.file));
	    }
	}

	public void treeCollapsed(TreeExpansionEvent e) {

	}
    }

    static class Renderer implements TreeCellRenderer {

	Font font = new Font("Dialog", Font.BOLD, 11);

	DefaultTreeCellRenderer renderer;

	public Renderer() {
	    this(new DefaultTreeCellRenderer(), null);
	}

	public Renderer(DefaultTreeCellRenderer renderer, Font font) {
	    this.renderer = renderer;
	    this.font = font;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

	    renderer = (DefaultTreeCellRenderer) renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    
	    if (node instanceof FileNode) {
		FileNode fnode = (FileNode) node;
		Icon icon = fnode.getIcon();
		renderer.setIcon(icon);
	    } else {
		renderer.setIcon(null);
	    }
	    if (font != null) {
		renderer.setFont(font);
	    }
	    return renderer;
	}
    }
}
