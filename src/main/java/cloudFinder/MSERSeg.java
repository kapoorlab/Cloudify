package cloudFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import mserGUI.CovistoMserPanel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class MSERSeg {

	final InteractiveCloudify parent;
	final JProgressBar jpb;

	public MSERSeg(final InteractiveCloudify parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	public void execute() {

		/**
		 * 
		 * 
		 * Create a watershed object that defines the region of interest, then make a
		 * roi object inside that region to get the clouds, finally put it all together
		 * as a cloud object and enumerate it all in a list and a hashmap for that time.
		 */

		String uniqueID = Integer.toString(CovistoZselectPanel.thirdDimension)
				+ Integer.toString(CovistoTimeselectPanel.fourthDimension);

		// Make the watershed object

		StaticMethods.GetPixelList(parent, parent.CurrentViewIntSegoriginalimg);

		Iterator<Integer> setiter = parent.pixellist.iterator();
		parent.overlay.clear();

		ArrayList<CloudObject> Allclouds = new ArrayList<CloudObject>();
		while (setiter.hasNext()) {

			int label = setiter.next();

			if (label != parent.background) {
				// Get the region
				RandomAccessibleInterval<FloatType> Seccurrent = Watershedobject.CurrentDetectionImage(parent,
						parent.CurrentViewIntSegoriginalimg, parent.CurrentViewSecSegoriginalimg, label);

				RandomAccessibleInterval<FloatType> current = Watershedobject.CurrentDetectionImage(parent,
						parent.CurrentViewIntSegoriginalimg, parent.CurrentViewSegoriginalimg, label);

				if (CovistoMserPanel.darktobright) {
					parent.Secnewtree = MserTree.buildMserTree(Seccurrent, CovistoMserPanel.delta,
							CovistoMserPanel.minSize, CovistoMserPanel.maxSize, CovistoMserPanel.Unstability_Score,
							CovistoMserPanel.minDiversity, true);
					parent.newtree = MserTree.buildMserTree(current, CovistoMserPanel.delta, CovistoMserPanel.minSize,
							CovistoMserPanel.maxSize, CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity,
							true);

				}

				else {
					parent.Secnewtree = MserTree.buildMserTree(Seccurrent, CovistoMserPanel.delta,
							CovistoMserPanel.minSize, CovistoMserPanel.maxSize, CovistoMserPanel.Unstability_Score,
							CovistoMserPanel.minDiversity, false);
					parent.newtree = MserTree.buildMserTree(current, CovistoMserPanel.delta, CovistoMserPanel.minSize,
							CovistoMserPanel.maxSize, CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity,
							false);

				}

				parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);

				parent.SecRois = utility.FinderUtils.getcurrentRois(parent.Secnewtree);

				ArrayList<RoiObject> currentLabelObject = new ArrayList<RoiObject>();
				ArrayList<RoiObject> currentSecLabelObject = new ArrayList<RoiObject>();

				for (Roi roi : parent.SecRois) {

					double[] Seccentroid = roi.getContourCentroid();
					double SecIntensity = StaticMethods.getIntensity(parent.CurrentViewSecOrig, parent.CurrentViewSecSegoriginalimg, roi);
					double SecnumPixels = StaticMethods.getNumberofPixels(parent.CurrentViewSecOrig,parent.CurrentViewSecSegoriginalimg, roi);

					double SecmeanIntensity = 0;
					if (SecnumPixels > 0)
						SecmeanIntensity = SecIntensity / SecnumPixels;

					RoiObject currentSecRoiobject = new RoiObject(roi, Seccentroid, SecmeanIntensity, SecIntensity,
							SecnumPixels);

					currentSecLabelObject.add(currentSecRoiobject);
					roi.setStrokeColor(parent.colorDrawSecMser);
					parent.overlay.add(roi);

				}
				for (Roi roi : parent.Rois) {

					double[] centroid = roi.getContourCentroid();

					double Intensity = StaticMethods.getIntensity(parent.CurrentViewOrig,parent.CurrentViewSegoriginalimg, roi);

					double numPixels = StaticMethods.getNumberofPixels(parent.CurrentViewOrig, parent.CurrentViewSegoriginalimg, roi);

					double meanIntensity = 0;

					if (numPixels > 0)
						meanIntensity = Intensity / numPixels;

					RoiObject currentRoiobject = new RoiObject(roi, centroid, meanIntensity, Intensity, numPixels);

					currentLabelObject.add(currentRoiobject);

					roi.setStrokeColor(parent.colorDrawMser);
					parent.overlay.add(roi);

				}

				Pair<RandomAccessibleInterval<FloatType>, RandomAccessibleInterval<FloatType>> BothMissImage = Watershedobject
						.CurrentOrigLabelImage(parent, parent.CurrentViewIntSegoriginalimg, parent.CurrentViewOrig,
								parent.CurrentViewSecOrig, label);
				MeasureProperties CloudandCell = new MeasureProperties(parent, BothMissImage.getA(),
						BothMissImage.getB(), currentLabelObject, currentSecLabelObject, label);
				Allclouds.addAll(CloudandCell.GetCurrentCloud());

			}

		}
		parent.AllClouds.put(uniqueID, Allclouds);

		parent.imp.setOverlay(parent.overlay);
		parent.imp.updateAndDraw();

	}

}
