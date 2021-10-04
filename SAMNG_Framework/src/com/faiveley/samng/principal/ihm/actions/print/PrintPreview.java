package com.faiveley.samng.principal.ihm.actions.print;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.JTextField;

class PrintPreview extends JFrame implements ActionListener {
	private static final long serialVersionUID = 4866864708592994785L;
	private JButton printButton = new JButton(Messages.getString("PrintPreview.print"));
    private JButton printSetupButton = new JButton(Messages.getString("PrintPreview.printsetup"));
    private JCheckBox printModeCheckbox = new JCheckBox(Messages.getString("PrintPreview.fit"));
    private JLabel numPagesLabel = new JLabel();
    private Pageable pageable = null;
    private CardLayout cardlayout = new CardLayout();
    private JPanel previewPanel = new JPanel(cardlayout);
    private JButton backButton = new JButton(Messages.getString("PrintPreview.previous"));
    private JButton forwardButton = new JButton(Messages.getString("PrintPreview.next"));
    private JTextField currentPageText = new JTextField("1");
    private int selectedPage = 0;
	private int numPages = -1;
    private PrintMode printMode = PrintMode.NORMAL;
    private PageFormat pf;
   
    
    public PrintPreview(final JTable table) {
        super(Messages.getString("PrintPreview.title"));
        HashPrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(OrientationRequested.LANDSCAPE);
        pf = PrinterJob.getPrinterJob().getPageFormat(aset);
        pageable = new Pageable() {
        	
        	private void computeNumPages() {
        		int maxNbPages = 1;
        		int minNbPages = 0;
        		int nbPages = 0;
        		Printable printable = table.getPrintable(printMode, null, null);
                Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();     
        		
        		try {
        			// Search an upper bound to the number of pages
        			while (printable.print(graphics, pf, maxNbPages) == Printable.PAGE_EXISTS) {
        				minNbPages = maxNbPages;
        				maxNbPages *= 2;
        			}
        			
        			// Use dichotomy to find the exact number of pages
        			while (maxNbPages >= minNbPages) {
        				int middlePage = (maxNbPages + minNbPages) / 2;
        				if (printable.print(graphics, pf, middlePage) == Printable.PAGE_EXISTS) {
        					nbPages = middlePage;
    						minNbPages = middlePage + 1;
    					} else {
    						maxNbPages = middlePage - 1;
    					}
        			}
        		} catch (PrinterException e) {
					e.printStackTrace();
				}        		        		
        		
        		// The number of pages corresponds to the first not printable 
        		numPages = nbPages + 1;
        	}
        	
            @Override
            public int getNumberOfPages() {
            	if (numPages == -1) {
            		computeNumPages();
            	}
                return numPages;
            }

            @Override
            public PageFormat getPageFormat(int x) {
                return pf;
            }

            @Override
            public Printable getPrintable(int x) {
                return table.getPrintable(printMode, null, null);
            }
        };
    }

    private void createPreview() {        
        createTopPanel();
        getContentPane().add(previewPanel, "Center");
        pageChanged();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createTopPanel() {
        FlowLayout fl = new FlowLayout();
        GridBagLayout gbl = new GridBagLayout();

        JPanel topPanel = new JPanel(gbl);
        JPanel temp = new JPanel(fl);
        
        currentPageText.setColumns(5);
        
        temp.add(printSetupButton);
        temp.add(backButton);
		temp.add(currentPageText);
        temp.add(numPagesLabel);
        temp.add(forwardButton);
        temp.add(printModeCheckbox);
        temp.add(printButton);

        topPanel.add(temp);
        
        printButton.addActionListener(this);
        backButton.addActionListener(this);
        forwardButton.addActionListener(this);
        printSetupButton.addActionListener(this);
        printModeCheckbox.addActionListener(this);
        currentPageText.addActionListener(this);
        
        getContentPane().add(topPanel, "North");
    }

	private void pageChanged() {
		// When changing format, the current page could be out of bounds
		if (selectedPage < 0 || selectedPage >= pageable.getNumberOfPages()) {
			selectedPage = 0;
		}
		currentPageText.setBackground(Color.WHITE);
		currentPageText.setText(Integer.toString(selectedPage + 1));
		numPagesLabel.setText("/ " + pageable.getNumberOfPages());
		previewPanel.removeAll();
		previewPanel.add("", new JScrollPane(new Page(selectedPage)));
		cardlayout.show(previewPanel, "");
		backButton.setEnabled(selectedPage != 0);
		forwardButton.setEnabled(selectedPage != pageable.getNumberOfPages() - 1);
		validate();
	}

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object o = ae.getSource();
        if (o == printButton) {
            try {
                PrinterJob pj = PrinterJob.getPrinterJob();
                pj.defaultPage(pf);
                pj.setPageable(pageable);
                if (pj.printDialog()) {
                    pj.print();
                    dispose();
                }
            } catch (PrinterException e) {
            	e.printStackTrace();
            }
        } else if (o == backButton) {
        	selectedPage--;
        	pageChanged();
        } else if (o == forwardButton) {
        	selectedPage++;
        	pageChanged();
        } else if (o == printSetupButton) {
        	pf = PrinterJob.getPrinterJob().pageDialog(pf);
        	numPages = -1;
        	pageChanged();
        } else if (o == printModeCheckbox) {
        	if (printModeCheckbox.isSelected()) {
        		printMode = PrintMode.FIT_WIDTH;
        	} else {
        		printMode = PrintMode.NORMAL;
        	}
        	numPages = -1;
        	pageChanged();
        }  else if (o == currentPageText) {
        	int askedPage = Integer.valueOf(currentPageText.getText()) - 1; 
        	if (askedPage >= 0 && askedPage <= pageable.getNumberOfPages()) {
        		selectedPage = askedPage;
        		pageChanged();
        	} else {
        		currentPageText.setBackground(Color.RED);
        	}
        }
    }

    class Page extends JLabel {
		private static final long serialVersionUID = -6134346003516763490L;

		public Page(int currentpage) {
        	Dimension size = new Dimension((int) pf.getPaper().getWidth(), (int) pf.getPaper().getHeight());
            if (pf.getOrientation() != PageFormat.PORTRAIT) {
                size = new Dimension(size.height, size.width);
            }
            
        	BufferedImage bufferimage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferimage.getGraphics();
            g.setColor(Color.WHITE);                       
            g.fillRect(0, 0, (int) pf.getWidth(), (int) pf.getHeight());
            try {
                pageable.getPrintable(currentpage).print(g, pf, currentpage);
            } catch (Exception ex) {
            }
            setIcon(new ImageIcon(bufferimage));
        }
    }

	public void openPreview() {
		createPreview();
	}
}