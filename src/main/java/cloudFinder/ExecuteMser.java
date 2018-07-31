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

		

			for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {

				for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {

					CovistoZselectPanel.thirdDimension = z;
					CovistoTimeselectPanel.fourthDimension = t;

				

					// UnsignedByteType image created here
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					

					processSlice(z, t);

				}

			}


			return null;
		}

		protected void processSlice(int z, int t) {

			CloudFinderMSER<UnsignedByteType> ComputeDOG = new CloudFinderMSER<UnsignedByteType>(parent, parent.jpb,
					 z, t);
			ComputeDOG.process();

			

		}

		@Override
		protected void done() {
			try {

				parent.apply3D = false;
				get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

		}
	
	
}
