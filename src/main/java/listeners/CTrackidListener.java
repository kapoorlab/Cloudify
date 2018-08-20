package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveCloudify;


public class CTrackidListener implements TextListener {

	
	final InteractiveCloudify parent;
	
	public CTrackidListener(final InteractiveCloudify parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
		parent.selectedID = s;
		
	}

}
