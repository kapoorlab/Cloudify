package listeners;

import ij.IJ;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class CloudShowView {

	
	final InteractiveCloudify parent;
	
	
	public CloudShowView(final InteractiveCloudify parent) {
		
		this.parent = parent;
		
	}
	
	
	public void shownewZ() {

		if (CovistoZselectPanel.thirdDimension > CovistoZselectPanel.thirdDimensionSize) {
			IJ.log("Max Z stack exceeded, moving to last Z instead");
			CovistoZselectPanel.thirdDimension = CovistoZselectPanel.thirdDimensionSize;
			
			
			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg, (int)CovistoZselectPanel.thirdDimension,
					(int)CovistoZselectPanel.thirdDimensionSize, (int)CovistoTimeselectPanel.fourthDimension, (int)CovistoTimeselectPanel.fourthDimensionSize);
			
		} else {

			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg, (int)CovistoZselectPanel.thirdDimension,
					(int)CovistoZselectPanel.thirdDimensionSize, (int)CovistoTimeselectPanel.fourthDimension, (int)CovistoTimeselectPanel.fourthDimensionSize);
			
		}

		
	}

	
	
	public void shownewT() {

		if (CovistoTimeselectPanel.fourthDimension > CovistoTimeselectPanel.fourthDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			CovistoTimeselectPanel.fourthDimension = CovistoTimeselectPanel.fourthDimensionSize;
			
			
			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,(int) CovistoZselectPanel.thirdDimension,
					(int)CovistoZselectPanel.thirdDimensionSize,(int) CovistoTimeselectPanel.fourthDimension, (int)CovistoTimeselectPanel.fourthDimensionSize);
			
		} else {

			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,(int) CovistoZselectPanel.thirdDimension,
					(int)CovistoZselectPanel.thirdDimensionSize, (int)CovistoTimeselectPanel.fourthDimension, (int)CovistoTimeselectPanel.fourthDimensionSize);
			
		}

		
		
	

		
	}
	
}
