package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb;

import java.util.List;

import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.Messages;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;

@SuppressWarnings("deprecation")
@Deprecated
public class TableTreeInformationPointDetailViewer extends TableTreeViewer {
	protected static final String LABEL_COLUMN_0 = Messages.getString("FixedColumnTableViewerPointInformationDetailColumn.0");
	protected static final String LABEL_COLUMN_1 = Messages.getString("FixedColumnTableViewerPointInformationDetailColumn.1");
	protected static final String LABEL_COLUMN_2 = Messages.getString("FixedColumnTableViewerPointInformationDetailColumn.2");
	protected static final String LABEL_COLUMN_3 = Messages.getString("FixedColumnTableViewerPointInformationDetailColumn.3");
	
	protected String[] columnNames = new String[] {LABEL_COLUMN_1, LABEL_COLUMN_2, LABEL_COLUMN_3};
	
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

	public TableTreeInformationPointDetailViewer(Composite parent, int style, FixedColumnTableViewerDetail fctvd, Evenement evenement) {
		super(parent, style);
		
		this.fctvd = fctvd;
		this.evenement = evenement;
		
	    setContentProvider(new TableTreeInformationPointDetailContentProvider());
	    setLabelProvider(new TableTreeInformationPointDetailLabelProvider());

		Table table = getTableTree().getTable();
		
		// First empty column
		TableColumn c = new TableColumn(table, SWT.CENTER);
		c.setResizable(false);
		
		// click listener to expand XYZ...
		c.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				setExpanded(!isExpanded());
				getFctvd().refreshOnglets(getEvenement());
			}
		});
		
	    // Pack the columns
		for (String cn : columnNames) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			initColumn(column, cn);
		}

	    // Turn on the header and the lines
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	private void initColumn(TableColumn column, String text) {
		column.setText(text);
		column.pack();
		column.setResizable(false);
	}
	
	public void setInformationPoint(int index) {
		List<InformationPoint> ips = event.getKVBVariable().getInformationPoints();
		
		if (ips.size() > index) {
			setInput(event.getKVBVariable().getInformationPoints().get(index));
			
			TableColumn firstColumn = getTableTree().getTable().getColumn(0);
			
			firstColumn.setText(String.format(LABEL_COLUMN_0, String.valueOf(index + 1)));
			firstColumn.pack();
		} else {
			refresh(true);
		}
		
		getTableTree().getTable().redraw();
	}
}