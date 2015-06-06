package com.smartg.java.vfs;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

import com.smartg.java.vfs.Comparators.SortType;

public class FileList extends JList<VFile> {

    private static final long serialVersionUID = -4224646056183093814L;

    static FileSystemView fsv = FileSystemView.getFileSystemView();

    public static enum ViewType {
	List, Details, SmallIcons, MiddleIcons, BigIcons
    }

    ListCellRenderer<VFile> detailsRenderer = new DetailsRenderer();
    ListCellRenderer<VFile> listRenderer = new ListeRenderer();

    volatile VFile parentFile;

    final IComparator<VFile> byName = new Comparators.ByNameComparator();
    final IComparator<VFile> byDate = new Comparators.ByDateComparator();
    final IComparator<VFile> bySize = new Comparators.BySizeComparator();

    FileListModel model = new FileListModel(byDate);

    Calendar calendar = Calendar.getInstance();
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    DateFormat dateFormat = DateFormat.getDateInstance();

    SortType sortType = SortType.ByName;

    boolean busy;
    boolean useCheckBox;

    public FileList() {
	this(null);
    }

    public FileList(VFile parent) {
	setModel(model);
	setParentFile(parent);
	setCellRenderer(detailsRenderer);
	model.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setBusy(false);
	    }
	});
    }

    public void setUseCheckBox(boolean useCheckBox) {
	this.useCheckBox = useCheckBox;
    }

    public VFile getParentFile() {
	return parentFile;
    }

    public void setParentFile(final VFile parent) {
	if (this.parentFile != null && parent != null) {
	    if (!this.parentFile.getAbsolutePath().equals(parent.getAbsolutePath())) {
		this.parentFile = parent;
		model.clear();
		model.setParent(parent);
	    }
	} else {
	    this.parentFile = parent;
	    model.clear();
	    model.setParent(parent);
	}
    }

    ViewType viewType;

    public ViewType getViewType() {
	return viewType;
    }

    public void setView(ViewType viewType) {
	if (this.viewType == viewType) {
	    return;
	}
	this.viewType = viewType;

	switch (viewType) {
	case Details:
	    setCellRenderer(detailsRenderer);
	    setLayoutOrientation(JList.VERTICAL);
	    break;
	case List:
	    setCellRenderer(listRenderer);
	    setLayoutOrientation(JList.VERTICAL_WRAP);
	    break;

	case SmallIcons:
	    setCellRenderer(listRenderer);
	    setLayoutOrientation(JList.HORIZONTAL_WRAP);
	    break;
	case MiddleIcons:
	case BigIcons:
	}
    }

    protected void setBusy(boolean busy) {
	this.busy = busy;
	repaint();
    }

    class ListeRenderer implements ListCellRenderer<VFile> {

	String right = "";
	String name = "";

	DefaultListCellRenderer dlcr = new DLCR2();
	JCheckBox checkBox = new JCheckBox();
	Box hbox = Box.createHorizontalBox();

	public ListeRenderer() {
	    hbox.add(checkBox);
	    hbox.add(dlcr);
	}

	public Component getListCellRendererComponent(JList<? extends VFile> list, VFile value, int index, boolean isSelected, boolean cellHasFocus) {
	    DefaultListCellRenderer renderer = (DefaultListCellRenderer) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    if (value != null) {
		renderer.setIcon(value.getIcon());
		name = value.getName();
		if (sortType == SortType.BySize) {
		    String s = getFileSize(value);
		    right = " (" + s + ")";
		} else if (sortType == SortType.ByDate) {
		    calendar.setTimeInMillis(value.lastModified());
		    String s = dateFormat.format(calendar.getTime());
		    right = " (" + s + ")";
		} else {
		    right = "";
		}
		renderer.setText(name + "             ");
	    }
	    if (busy) {
		renderer.setForeground(Color.GRAY);
	    } else {
		renderer.setForeground(Color.BLACK);
	    }
	    if (useCheckBox) {
		checkBox.setSelected(isSelected);
		return hbox;
	    }
	    return renderer;
	}

	class DLCR2 extends DefaultListCellRenderer {

	    private static final long serialVersionUID = 563682581687818732L;

	    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getWidth();
		int h = getHeight();

		FontMetrics fontMetrics = g.getFontMetrics();
		int descent = fontMetrics.getDescent();

		int dw = fontMetrics.stringWidth(right);
		int left = w - dw;

		g.setColor(getBackground());
		g.fillRect(left, 0, dw, h);

		g.setColor(getForeground());
		g.drawString(right, w - dw, h - descent);
	    }

	}
    }

    String getFileSize(VFile node) {
	if (!node.isDirectory()) {
	    long size = Math.round(node.length() / 1024.0);
	    boolean len = node.length() > 0;
	    if (size == 0 && len) {
		size = 1;
	    }
	    return numberFormat.format(size) + " KB";
	}
	return "";
    }

    class DetailsRenderer implements ListCellRenderer<VFile> {

	String date = "";
	String size = "";

	DefaultListCellRenderer dlcr = new DLCR();
	JCheckBox checkBox = new JCheckBox();
	Box hbox = Box.createHorizontalBox();

	public DetailsRenderer() {
	    hbox.add(checkBox);
	    hbox.add(dlcr);
	}

	public Component getListCellRendererComponent(JList<? extends VFile> list, VFile value, int index, boolean isSelected, boolean cellHasFocus) {
	    DefaultListCellRenderer renderer = (DefaultListCellRenderer) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    renderer.setIcon(null);
	    if (value != null) {
		renderer.setIcon(value.getIcon());
		long lastModified = value.lastModified();
		calendar.setTimeInMillis(lastModified);
		date = dateFormat.format(calendar.getTime());
		size = getFileSize(value);
	    }
	    if (busy) {
		renderer.setForeground(Color.GRAY);
	    } else {
		renderer.setForeground(Color.BLACK);
	    }
	    if (useCheckBox) {
		checkBox.setSelected(isSelected);
		return hbox;
	    }
	    return renderer;
	}

	class DLCR extends DefaultListCellRenderer {

	    private static final long serialVersionUID = -3647281471368811598L;

	    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getWidth() - 10;
		int h = getHeight();

//		String text = getText();
		FontMetrics fontMetrics = g.getFontMetrics();
		int descent = fontMetrics.getDescent();
		Icon icon = getIcon();
		if (icon != null) {
		    icon.getIconWidth();
		}

		int dateWidth = 80;
		int sizeWidth = 80;

		int sizeStart = w - sizeWidth;
		int dateStart = sizeStart - dateWidth;

		g.setColor(getBackground());
		g.fillRect(dateStart, 0, w - dateStart, h);
		g.setColor(getForeground());

		int dw = fontMetrics.stringWidth(date);
		g.drawString(date, dateStart + dateWidth - dw, h - descent);

		int sw = fontMetrics.stringWidth(size);
		g.drawString(size, sizeStart + sizeWidth - sw, h - descent);
	    }
	}
    }

    public void setSortBy(SortType type) {
	if (sortType == type) {
	    return;
	}
	sortType = type;
	IComparator.Order order = model.getComparator().getOrder();

	switch (type) {
	case ByName:
	    byName.setOrder(order);
	    model.setComparator(byName);
	    break;
	case ByDate:
	    byDate.setOrder(order);
	    model.setComparator(byDate);
	    break;
	case BySize:
	    bySize.setOrder(order);
	    model.setComparator(bySize);
	    break;
	}
	setBusy(true);
    }

    public void setOrder(IComparator.Order order) {
	if (model.getOrder() != order) {
	    setBusy(true);
	    model.setOrder(order);
	}
    }

    public IComparator.Order getOrder() {
	return model.getComparator().getOrder();
    }

    public VFilenameFilter getFilenameFilter() {
	return model.getFilenameFilter();
    }

    public void setFilenameFilter(VFilenameFilter filenameFilter) {
	model.setFilenameFilter(filenameFilter);
    }

    static class VParentFile extends VProxyFile {
	protected VParentFile(VFile file) {
	    super(file);
	}

	public String getDisplayName() {
	    return "..";
	}

	public String toString() {
	    return "..";
	}

	public Icon getIcon() {
	    return null;
	}
    }
}
