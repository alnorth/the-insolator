package uk.co.alasdairnorth.insolator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Calculator<LatLonDateParameter, InsolationOutput> insolationCalculator;

	public MainWindow() {
		super("The Insolator v2.1");
		this.insolationCalculator = InsolationCalculator.getInsolationCalculator();
		setUpGui();
	}
	
	private void setUpGui() {
		
		SpinnerModel minYearModel = new SpinnerNumberModel(-2000, -1000000, 1000000, 1);
		final JSpinner minYearSpinner = new JSpinner(minYearModel);
		
		SpinnerModel maxYearModel = new SpinnerNumberModel(2000, -1000000, 1000000, 1);
		final JSpinner maxYearSpinner = new JSpinner(maxYearModel);
		
		Calendar now = Calendar.getInstance();
		SpinnerModel compareToYearModel = new SpinnerNumberModel(now.get(Calendar.YEAR), -1000000, 1000000, 1);
		final JSpinner compareToYearSpinner = new JSpinner(compareToYearModel);
		
		SpinnerModel latModel = new SpinnerNumberModel(0d, -90d, 90d, Math.pow(1, -10));
		final JSpinner latSpinner = new JSpinner(latModel);
		
		SpinnerModel lonModel = new SpinnerNumberModel(0d, -180d, 180d, Math.pow(1, -10));
		final JSpinner lonSpinner = new JSpinner(lonModel);
		
		String[] exportOptions = {"Daily average", "Monthly average", "Yearly average", "Yearly summer/winter average", "Millenial average", "Millenial summer/winter average"};
		final JComboBox exportOptionBox = new JComboBox(exportOptions);
		
		final JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		final JButton exportButton = new JButton("Export data");
		exportButton.setPreferredSize(new Dimension(125, 25));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				File outputFile = fileSaveDialog();
				if(outputFile != null) {
					exportButton.setEnabled(false);
					
					SpinnerNumberModel minModel = (SpinnerNumberModel) minYearSpinner.getModel();
					int minYear = minModel.getNumber().intValue();
					
					SpinnerNumberModel maxModel = (SpinnerNumberModel) maxYearSpinner.getModel();
					int maxYear = maxModel.getNumber().intValue();
					
					SpinnerNumberModel compareToModel = (SpinnerNumberModel) compareToYearSpinner.getModel();
					int compareToYear = compareToModel.getNumber().intValue();
					
					SpinnerNumberModel latModel = (SpinnerNumberModel) latSpinner.getModel();
					double lat = latModel.getNumber().doubleValue();
					
					SpinnerNumberModel lonModel = (SpinnerNumberModel) lonSpinner.getModel();
					double lon = lonModel.getNumber().doubleValue();
					
					int exportTypeIndex = exportOptionBox.getSelectedIndex();
					
					Thread exportThread = new ExportThread(minYear, maxYear, compareToYear, lat, lon, exportTypeIndex, outputFile, insolationCalculator, progressBar, exportButton);
					exportThread.start();
				}
			}
		});
		
		JPanel values = new JPanel();		
		values.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		values.setLayout(new GridLayout(0,2));
		values.setPreferredSize(new Dimension(300,150));
				
		values.add(new JLabel("Start year:"));
		values.add(minYearSpinner);
		values.add(new JLabel("End year:"));
		values.add(maxYearSpinner);
		values.add(new JLabel("Compare to year:"));
		values.add(compareToYearSpinner);
		
		values.add(new JLabel("Latitude:"));
		values.add(latSpinner);
		values.add(new JLabel("Longitude:"));
		values.add(lonSpinner);
		values.add(new JLabel("Export type:"));
		values.add(exportOptionBox);
		
		JPanel button = new JPanel();
		button.setLayout(new GridLayout(0, 1));
		button.add(exportButton);
		button.add(progressBar);
		
		JPanel main = new JPanel();
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		main.setLayout(new BorderLayout());
		main.add(values, BorderLayout.CENTER);
		main.add(button, BorderLayout.PAGE_END);
						
		setResizable(false);
		Container cp = getContentPane();
		cp.add(main);
	}
	
	private File fileSaveDialog() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Export calculated data");
		
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile();
		else return null;
	}
	
}
