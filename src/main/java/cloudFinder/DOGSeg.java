package cloudFinder;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import dogGUI.CovistoDogPanel;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class DOGSeg extends SwingWorker<Void, Void> {

	final InteractiveCloudify parent;
	final JProgressBar jpb;

	public DOGSeg(final InteractiveCloudify parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {
			final DogDetection.ExtremaType type;
			if (CovistoDogPanel.lookForMaxima)
				type = DogDetection.ExtremaType.MINIMA;
			else
				type = DogDetection.ExtremaType.MAXIMA;
			CovistoDogPanel.sigma2 = utility.ScrollbarUtils.computeSigma2(CovistoDogPanel.sigma, parent.sensitivity);
			final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(parent.CurrentViewOrig),
					parent.interval, new double[] { 1, 1 }, CovistoDogPanel.sigma, CovistoDogPanel.sigma2, type, CovistoDogPanel.threshold, true);
			parent.overlay.clear();
			parent.peaks = newdog.getSubpixelPeaks();
		return null;
	}

	@Override
	protected void done() {
			parent.overlay.clear();

			parent.Rois = utility.FinderUtils.getcurrentRois(parent.peaks, CovistoDogPanel.sigma, CovistoDogPanel.sigma2);

			parent.CurrentCloudobject = new ArrayList<CloudObject>();
			for (int index = 0; index < parent.peaks.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawDog);
				parent.overlay.add(or);
			}

			for (Roi currentroi : parent.Rois) {

				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = CloudObject.getIntensity(currentroi, parent.CurrentViewOrig);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				CloudObject currentobject = new CloudObject(currentroi, geocenter, numberofpixels, intensity, averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
				parent.CurrentCloudobject.add(currentobject);
			}
			for (Map.Entry<String, ArrayList<CloudObject>> entry : parent.ZTRois.entrySet()) {

				ArrayList<CloudObject> current = entry.getValue();
				for (CloudObject currentroi : current) {

					if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension && currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

						currentroi.roi.setStrokeColor(parent.colorSnake);
						parent.overlay.add(currentroi.roi);
						
					}

				}
			}
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
	
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}
