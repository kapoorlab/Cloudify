package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import dogGUI.CovistoDogPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;



public class PreThresholdListener implements AdjustmentListener {
	final Label label;
	final InteractiveCloudify secparent;
	final String string;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	


	public PreThresholdListener(InteractiveCloudify secparent,  final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		this.label = label;
		this.string = string;

		this.min = min;
		this.max = max;
		this.scrollbar = scrollbar;
		this.scrollbarSize = scrollbarSize;
		this.secparent = secparent;
		scrollbar.addMouseListener( new CloudCovistoStandardMouseListener( secparent, ValueChange.DOG ) );
		scrollbar.setBlockIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		scrollbar.setUnitIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
	
		CovistoDogPanel.threshold = utility.ScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max,
				scrollbarSize);
		scrollbar.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(CovistoDogPanel.threshold, min, max, scrollbarSize));

		label.setText(string +  " = "  + secparent.nf.format(CovistoDogPanel.threshold));
		
		
		
	
	}
}


