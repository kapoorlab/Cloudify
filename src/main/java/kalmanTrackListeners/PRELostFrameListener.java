package kalmanTrackListeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import kalmanGUI.CovistoKalmanPanel;
import pluginTools.InteractiveCloudify;

public class PRELostFrameListener implements TextListener {

	public InteractiveCloudify parent;
	
	public PRELostFrameListener(final InteractiveCloudify parent) {
		
		this.parent = parent;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
	    	CovistoKalmanPanel.maxframegap = Integer.parseInt(s);
		
		
	}
	
}
