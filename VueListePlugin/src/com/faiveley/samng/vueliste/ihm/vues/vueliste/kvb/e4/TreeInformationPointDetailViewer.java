package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;

import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.Messages;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeInformationPointDetailContentProvider;

public class TreeInformationPointDetailViewer extends TreeViewer {
	protected static final String LABEL_COLUMN_0 = Messages
			.getString("FixedColumnTableViewerPointInformationDetailColumn.0");
	protected static final String LABEL_COLUMN_1 = Messages
			.getString("FixedColumnTableViewerPointInformationDetailColumn.1");
	protected static final String LABEL_COLUMN_2 = Messages
			.getString("FixedColumnTableViewerPointInformationDetailColumn.2");
	protected static final String LABEL_COLUMN_3 = Messages
			.getString("FixedColumnTableViewerPointInformationDetailColumn.3");

	protected String[] columnNames = new String[] { LABEL_COLUMN_1, LABEL_COLUMN_2, LABEL_COLUMN_3 };

	private Event event;

	private boolean expanded = false;
	private Evenement evenement = null;
	private FixedColumnTableViewerDetail fctvd = null;

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public Evenement getEvenement() {
		return evenement;
	}

	public void setEvenement(Evenement evenement) {
		this.evenement = evenement;
	}

	public FixedColumnTableViewerDetail getFctvd() {
		return fctvd;
	}

	public void setFctvd(FixedColumnTableViewerDetail fctvd) {
		this.fctvd = fctvd;
	}

	public TreeInformationPointDetailViewer(Composite parent, int style, FixedColumnTableViewerDetail fctvd,
			Evenement evenement) {
		super(parent, style);

		this.fctvd = fctvd;
		this.evenement = evenement;

		setContentProvider(new TableTreeInformationPointDetailContentProvider());
		setLabelProvider(new LabelProvider());  // Default provider on tree (only column provider will be used)

		// Add columns in the tree
		// First empty column
		int index = 0;
		TreeViewerColumn col = new TreeViewerColumn(this, SWT.CENTER);
		col.getColumn().setResizable(false);

		// click listener to expand XYZ...
		col.getColumn().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				setExpanded(!isExpanded());
				getFctvd().refreshOnglets(getEvenement());
			}
		});
		col.setLabelProvider(new TreeInformationPointDetailColumnProvider(index));
		index++;

		// Pack the columns
		for (String cn : columnNames) {
			
			TreeViewerColumn c = new TreeViewerColumn(this, SWT.CENTER);
			initColumn(c.getColumn(), cn);
			c.setLabelProvider(new TreeInformationPointDetailColumnProvider(index)); 
			index++;

		}

		// Turn on the header and the lines
		getTree().setHeaderVisible(true);
		getTree().setLinesVisible(true);
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	private void initColumn(TreeColumn column, String text) {
		column.setText(text);
		column.pack();
		column.setResizable(false);
	}

	public void setInformationPoint(int index) {
		List<InformationPoint> ips = event.getKVBVariable().getInformationPoints();

		if (ips.size() > index) {
			setInput(event.getKVBVariable().getInformationPoints().get(index));

			TreeColumn firstColumn = getTree().getColumn(0);

			firstColumn.setText(String.format(LABEL_COLUMN_0, String.valueOf(index + 1)));
			firstColumn.pack();
		} else {
			refresh(true);
		}

		getTree().redraw();
	}
}