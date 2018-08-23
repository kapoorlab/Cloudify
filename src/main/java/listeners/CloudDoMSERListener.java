package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods;
import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;
import watershedGUI.CovistoWatershedPanel;

public class CloudDoMSERListener implements ItemListener {
	
	final InteractiveCloudify  parent;
	
	public CloudDoMSERListener( InteractiveCloudify parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = false;
			parent.showMSER = true;
			CovistoDogPanel.sigmaslider.setVisible(false);
			CovistoDogPanel.thresholdslider.setVisible(false);
			CovistoDogPanel.findmaxima.setVisible(false);
			CovistoDogPanel.findminima.setVisible(false);
			
			
			
			
			CovistoMserPanel.deltaS.setVisible(true);
			CovistoMserPanel.Unstability_ScoreS.setVisible(true);
			CovistoMserPanel.minDiversityS.setVisible(true);
			CovistoMserPanel.minSizeS.setVisible(true);
			CovistoMserPanel.maxSizeS.setVisible(true);
			CovistoMserPanel.findminimaMser.setVisible(true);
			CovistoMserPanel.findmaximaMser.setVisible(true);
			
			
			CovistoWatershedPanel.displayWater.setVisible(false);
			CovistoWatershedPanel.displayBinary.setVisible(false);
			CovistoWatershedPanel.displayDist.setVisible(false);
			CovistoWatershedPanel.autothreshold.setVisible(false);
			CovistoWatershedPanel.thresholdWaterslider.setVisible(false);
			parent.updatePreview(ValueChange.MSER);
			
		}

	}
	

}
