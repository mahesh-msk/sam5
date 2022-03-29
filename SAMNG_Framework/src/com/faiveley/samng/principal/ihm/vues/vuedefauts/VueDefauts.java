package com.faiveley.samng.principal.ihm.vues.vuedefauts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.actions.fichier.SauverRapportDefautsAction;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * @author Cosmin Udroiu
 */
public class VueDefauts extends ViewPart implements ICapturable {
	public static final String ID = "SAMNG_Framework.Logger.Viewer";

	private Composite mainComposite;
	private Text logText;
	private Color backgroundColor;

	/**
	 * D�claration des actions
	 */
	private CapturerVueAction capturerVueAction ;
	private ImprimerVueAction imprimerVueAction ;
	private SauverRapportDefautsAction sauverVueAction;

	// ToolBar
	private IActionBars bars; 

	@Override
	public void createPartControl(Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		setPartName(Messages.getString("VueDefauts.1"));
		parent.setLayout(new FillLayout());
		this.mainComposite = new Composite(parent, SWT.NONE);
		this.mainComposite.setLayout(new FillLayout());
		this.logText = new Text(this.mainComposite, SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		this.logText.setText("");
		this.logText.setEditable(false);
		this.backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		this.logText.setBackground(this.backgroundColor);

		initFromLogFile();

		makeActions();
		this.bars = getViewSite().getActionBars();
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		ajoutActionToolBar(sauverVueAction);
	}

	/**
	 * Ajout des actions
	*/
	public void makeActions() {
		// R�cup�ration de la fenetre active
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		// Ajout des actions
		this.capturerVueAction = new CapturerVueAction(window,Messages.getString("VueDefauts.2") );
		this.capturerVueAction.setEnabled(capturerVueAction.isEnabled());

		this.imprimerVueAction = new ImprimerVueAction(window,Messages.getString("VueDefauts.3") );
		this.imprimerVueAction.setEnabled(imprimerVueAction.isEnabled());

		this.sauverVueAction = new SauverRapportDefautsAction(window,Messages.getString("VueDefauts.4"));
		this.sauverVueAction.setEnabled(sauverVueAction.isEnabled());
	}

	/**
	 * M�thode d'ajout d'une action dans le menu
	 * @param action
	 */
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
	}

	/**
	 * M�thode d'ajout d'une action dans la toolbar
	 * @param action
	 */
	public void ajoutActionToolMenuBar(Action action) {
		this.bars.getMenuManager().add(action);
	}

	public void ajoutSeparateurToolBar() {
		this.bars.getToolBarManager().add(new Separator());
	}

	@Override
	public void setFocus() {}

	public void initFromLogFile() {
		String curLine;

		try {
			FileInputStream fileInputStream = new FileInputStream(RepertoiresAdresses.logs_parser_log_TXT);
			BufferedReader inFile = new BufferedReader(new InputStreamReader(fileInputStream));
			List<String> lineMessages = new ArrayList<String>();
			
			while ((curLine = inFile.readLine()) != null) {
				lineMessages.add(curLine);
			}
			
			for (String msgLine: lineMessages) {
				this.logText.append(msgLine);
				this.logText.append("\r\n");
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void reloadFromLogFile() {
		this.logText.setText("");
		initFromLogFile();
	}
	
	@Override
	public void dispose() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		this.backgroundColor.dispose();
		super.dispose();
	}

	public Composite getContenu() {
		return this.mainComposite;
	}
}
