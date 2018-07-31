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
			
			
			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

			parent.CurrentViewIntSegoriginalimg  = utility.CovistoSlicer.getCurrentView(parent.IntSegoriginalimg,
					 CovistoZselectPanel.thirdDimension,
						CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
						CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(parent.originalSecimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(parent.Segoriginalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
		} else {

			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

			parent.CurrentViewIntSegoriginalimg  = utility.CovistoSlicer.getCurrentView(parent.IntSegoriginalimg,
					 CovistoZselectPanel.thirdDimension,
						CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
						CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(parent.originalSecimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(parent.Segoriginalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
		}

		
	}

	
	
	public void shownewT() {

		if (CovistoTimeselectPanel.fourthDimension > CovistoTimeselectPanel.fourthDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			CovistoTimeselectPanel.fourthDimension = CovistoTimeselectPanel.fourthDimensionSize;
			
			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

			parent.CurrentViewIntSegoriginalimg  = utility.CovistoSlicer.getCurrentView(parent.IntSegoriginalimg,
					 CovistoZselectPanel.thirdDimension,
						CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
						CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(parent.originalSecimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(parent.Segoriginalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
		} else {

			parent.CurrentViewOrig = utility.CovistoSlicer.getCurrentView(parent.originalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

			parent.CurrentViewIntSegoriginalimg  = utility.CovistoSlicer.getCurrentView(parent.IntSegoriginalimg,
					 CovistoZselectPanel.thirdDimension,
						CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
						CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(parent.originalSecimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
			parent.CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(parent.Segoriginalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
		}

		
		
	

		
	}
	
}
