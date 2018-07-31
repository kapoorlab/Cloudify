package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;


public class PreZListener implements AdjustmentListener {
		final Label label;
		final String string;
		final float min, max;
		final int scrollbarSize;
        final InteractiveCloudify secparent;
		final JScrollBar deltaScrollbar;



		public PreZListener(InteractiveCloudify secparent, final Label label, final String string, final float min, final float max,
				final int scrollbarSize, final JScrollBar deltaScrollbar) {
			this.label = label;
			this.string = string;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;

			this.deltaScrollbar = deltaScrollbar;
			this.secparent = secparent;
				deltaScrollbar.addMouseListener(new CloudCovistoStandardMouseListener(secparent, ValueChange.THIRDDIMmouse));
			
				
				deltaScrollbar.setBlockIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
				deltaScrollbar.setUnitIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		}

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			
			CovistoZselectPanel.thirdDimension = (int) Math.round(utility.CovistoSlicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize));
			
			deltaScrollbar
			.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(CovistoZselectPanel.thirdDimension, min, max, scrollbarSize));



			label.setText(string +  " = "  + CovistoZselectPanel.thirdDimension);
			
			
			
			
			CovistoZselectPanel.inputFieldZ.setText(Integer.toString((int)CovistoZselectPanel.thirdDimension));
			secparent.panelFirst.validate();
			secparent.panelFirst.repaint();
			
			
			CloudShowView show = new CloudShowView(secparent);
			show.shownewZ();
			
		}
		
	
	
}