/*******************************************************************************
 * Copyright (c) 2006 BestSolution Systemhaus GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of:
 * 1. The Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 2. LGPL v2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 3. MPL v1.1 which accompanies this distribution, and is available at
 * http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * Contributors:
 *     Tom Schind <tom.schindl@bestsolution.at> - Initial API and implementation
 *******************************************************************************/
package com.faiveley.samng.principal.ihm.vues;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;


public class FixedColumnTableViewer extends FixedColumnTable {
	
	public FixedColumnTableViewer(Composite parent, int style) {
		super(parent, SWT.NONE);

		FormLayout layout = new FormLayout();
		setLayout(layout);
		this.fixedTableViewer = new TableViewer(this, style | SWT.VIRTUAL);
		this.fixedTableViewer.setUseHashlookup(true);
		this.fixedTable = this.fixedTableViewer.getTable();
		//this.fixedTable.computeTrim(arg0, arg1, arg2, arg3)
		
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.bottom = new FormAttachment(100,
				-1 * this.fixedTable.getHorizontalBar().getSize().y);
		this.fixedTable.setLayoutData(data);

		this.scrollingTableViewer = new TableViewer(this, style | SWT.VIRTUAL);
		this.scrollingTableViewer.setUseHashlookup(true);
		this.scrollingTable = this.scrollingTableViewer.getTable();
		this.scrollingTable.getHorizontalBar().setEnabled(true);
		this.scrollingTable.getHorizontalBar().setVisible(true);
		
		data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(this.fixedTable,
				this.fixedTable.getVerticalBar().getSize().x * -1);
		
		data.right = new FormAttachment(100);
		data.bottom = new FormAttachment(100);

		this.scrollingTable.setLayoutData(data);
		this.scrollingTable.moveAbove(this.fixedTable);

		setBackground(this.fixedTable.getBackground());
		hookTables();
		
	}	
}
