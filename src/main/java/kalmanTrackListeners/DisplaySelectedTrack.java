package kalmanTrackListeners;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import ij.gui.ImageCanvas;
import pluginTools.InteractiveCloudify;

public class DisplaySelectedTrack {
	
	
	
	public static void Select(final InteractiveCloudify parent) {
		
		

		if (parent.mvl != null)
			parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

			

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				parent.Clickedpoints[0] = x;
				parent.Clickedpoints[1] = y;

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
				
					
					//Still to write this function
				//	CurvatureTableDisplay.displayclicked(parent, parent.rowchoice);

					if (!parent.jFreeChartFrameIntensityA.isVisible())
						parent.jFreeChartFrameIntensityA = utility.ChartMaker.display(parent.chartIntensityA, new Dimension(500, 500));

				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

	}
		
		
		
		
	}


