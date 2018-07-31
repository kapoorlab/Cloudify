package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;

public class FindMinimaMserListener implements ItemListener {
	
	final InteractiveCloudify secparent;

	
	
	public FindMinimaMserListener(InteractiveCloudify secparent) {
	
		
		this.secparent = secparent;
		
	}


	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			CovistoMserPanel.darktobright = false;
			CovistoMserPanel.brighttodark = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			CovistoMserPanel.darktobright = false;
			CovistoMserPanel.brighttodark = true;
			secparent.updatePreview(ValueChange.MSER);
		}

	}
	

}

