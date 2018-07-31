package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;

public class FindMinimaListener implements ItemListener {
	
	final InteractiveCloudify secparent;

	
	
	public FindMinimaListener(InteractiveCloudify secparent) {
		
        this.secparent = secparent;
		
	}


	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			CovistoDogPanel.lookForMinima = false;
			CovistoDogPanel.lookForMaxima = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			CovistoDogPanel.lookForMinima = true;
			CovistoDogPanel.lookForMaxima = false;
			secparent.updatePreview(ValueChange.DOG);
		}

	}
	

}

