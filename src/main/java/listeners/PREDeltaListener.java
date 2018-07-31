package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;

public class PREDeltaListener implements AdjustmentListener {
	
	final Label label;
	final String string;
	final InteractiveCloudify secparent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	

	
	
	public PREDeltaListener(InteractiveCloudify secparent,  final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.scrollbar = scrollbar;
		this.secparent = secparent;
		scrollbar.addMouseListener( new CloudCovistoStandardMouseListener( secparent, ValueChange.MSER ) );
		scrollbar.setBlockIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		scrollbar.setUnitIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}



	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		CovistoMserPanel.delta = utility.ScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

			scrollbar.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(CovistoMserPanel.delta, min, max, scrollbarSize));

			label.setText(string +  " = "  + secparent.nf.format(CovistoMserPanel.delta));

	
	
	}
	
	

}
