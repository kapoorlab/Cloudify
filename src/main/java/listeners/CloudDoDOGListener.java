package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods;
import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import watershedGUI.CovistoWatershedPanel;

public class CloudDoDOGListener implements ItemListener {
	
	final InteractiveCloudify  parent;
	
	public CloudDoDOGListener( InteractiveCloudify parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = true;
			parent.showMSER = false;
			
			CovistoDogPanel.sigmaslider.setVisible(true);
			CovistoDogPanel.thresholdslider.setVisible(true);
			CovistoDogPanel.findmaxima.setVisible(true);
			CovistoDogPanel.findminima.setVisible(true);
			
			CovistoMserPanel.deltaS.setVisible(false);
			CovistoMserPanel.Unstability_ScoreS.setVisible(false);
			CovistoMserPanel.minDiversityS.setVisible(false);
			CovistoMserPanel.minSizeS.setVisible(false);
			CovistoMserPanel.maxSizeS.setVisible(false);
			CovistoMserPanel.findminimaMser.setVisible(false);
			CovistoMserPanel.findmaximaMser.setVisible(false);
			
			CovistoWatershedPanel.displayWater.setVisible(false);
			CovistoWatershedPanel.displayBinary.setVisible(false);
			CovistoWatershedPanel.displayDist.setVisible(false);
			CovistoWatershedPanel.autothreshold.setVisible(false);
			CovistoWatershedPanel.thresholdWaterslider.setVisible(false);
			
		
		}
		
	}
	}
	