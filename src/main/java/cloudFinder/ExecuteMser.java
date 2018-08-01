package cloudFinder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;


import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class ExecuteMser extends SwingWorker<Void, Void> {

		final InteractiveCloudify parent;

		public ExecuteMser(final InteractiveCloudify parent) {

			this.parent = parent;

		}

		@Override
		protected Void doInBackground() throws Exception {

			parent.apply3D = true;

		
			int percent = 0;
			for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {

				for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {
					
					parent.pixellist.clear();
					++percent;
					CovistoZselectPanel.thirdDimension = z;
					CovistoTimeselectPanel.fourthDimension = t;
					utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, 100 * percent/ CovistoZselectPanel.thirdDimensionSize, "Computing: " + z + " / " + CovistoZselectPanel.thirdDimensionSize);
					// UnsignedByteType image created here
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					

					processSlice(z, t);

				}

			}


			return null;
		}

		protected void processSlice(int z, int t) {

			CloudFinderMSER<FloatType> ComputeMSER = new CloudFinderMSER<FloatType>(parent, parent.jpb,
					 z, t);
			ComputeMSER.process();

			

		}

		@Override
		protected void done() {
			try {

				parent.apply3D = false;
				utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb,"Done");
				get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		}
	
	
}
