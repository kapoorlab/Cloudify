package listeners;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import dogGUI.CovistoDogPanel;
import mserGUI.CovistoMserPanel;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;


public class PreSigmaListener implements AdjustmentListener {
	final Label label;
	final InteractiveCloudify secparent;
	final String string;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar sigmaScrollbar1;



	public PreSigmaListener(InteractiveCloudify secparent, final Label label,final String string, final float min, final float max, final int scrollbarSize,
			final JScrollBar sigmaScrollbar1) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.string = string;
		this.scrollbarSize = scrollbarSize;
		this.sigmaScrollbar1 = sigmaScrollbar1;
		this.secparent = secparent;
		sigmaScrollbar1.addMouseListener( new CloudCovistoStandardMouseListener( secparent, ValueChange.DOG ) );
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		    CovistoDogPanel.sigma = utility.ScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		
		
			sigmaScrollbar1.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(CovistoDogPanel.sigma, min, max, scrollbarSize));

			label.setText(string +  " = "  + secparent.nf.format(CovistoDogPanel.sigma));
		
	
	}
}
