package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;

public class FindMaximaMserListener implements ItemListener {
	
	final InteractiveCloudify secparent;

	
	
	public FindMaximaMserListener(InteractiveCloudify secparent) {
		
		this.secparent = secparent;
	}


	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			CovistoMserPanel.darktobright = false;
			CovistoMserPanel.brighttodark = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			CovistoMserPanel.darktobright = true;
			CovistoMserPanel.brighttodark = false;
			secparent.updatePreview(ValueChange.MSER);
		}

	}
	

}

