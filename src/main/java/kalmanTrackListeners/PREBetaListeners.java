package kalmanTrackListeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import kalmanGUI.CovistoKalmanPanel;
import listeners.CloudCovistoStandardMouseListener;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;

public class PREBetaListeners implements AdjustmentListener {
	
	final Label label;
	final String string;
	final InteractiveCloudify parent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	
	
	public PREBetaListeners(final InteractiveCloudify parent, final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		
		this.parent = parent;
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.scrollbar = scrollbar;
		
		scrollbar.addMouseListener( new CloudCovistoStandardMouseListener( parent, ValueChange.BETA ) );
		
	}
	
	
	
	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		CovistoKalmanPanel.beta = utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			scrollbar.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(CovistoKalmanPanel.beta, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.nf.format(CovistoKalmanPanel.beta));
		
	
	}
	
	

}
