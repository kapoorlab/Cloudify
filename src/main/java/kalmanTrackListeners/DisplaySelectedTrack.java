package kalmanTrackListeners;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import cloudDisplayer.DisplayTrack;
import cloudFinder.Distance;
import cloudTracker.TrackModel;
import ij.gui.ImageCanvas;
import net.imglib2.util.Pair;
import pluginTools.InteractiveCloudify;
import utility.ChartMaker;
import zGUI.CovistoZselectPanel;

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
					displayclicked(parent, parent.rowchoice);
					
					if (!parent.jFreeChartFrameIntensityA.isVisible())
						parent.jFreeChartFrameIntensityA = utility.ChartMaker.display(parent.chartIntensityA, new Dimension(500, 500));
					if (!parent.jFreeChartFrameIntensityB.isVisible())
						parent.jFreeChartFrameIntensityB = utility.ChartMaker.display(parent.chartIntensityB, new Dimension(500, 500));
					
					if (!parent.jFreeChartFrameIntensityASec.isVisible())
						parent.jFreeChartFrameIntensityASec = utility.ChartMaker.display(parent.chartIntensityASec, new Dimension(500, 500));
					if (!parent.jFreeChartFrameIntensityBSec.isVisible())
						parent.jFreeChartFrameIntensityBSec = utility.ChartMaker.display(parent.chartIntensityBSec, new Dimension(500, 500));
					
					
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
		
       public static void Mark(final InteractiveCloudify parent) {

		
		if (parent.ml != null)
			parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0 && parent.table.getRowCount() > 0) {
					NumberFormat f = NumberFormat.getInstance();
					for (int row = 0; row < parent.tablesize; ++row) {
						String CordX = (String) parent.table.getValueAt(row, 1);
						String CordY = (String) parent.table.getValueAt(row, 2);

						String CordZ = (String) parent.table.getValueAt(row, 3);

						double dCordX = 0, dCordZ = 0, dCordY = 0;
						try {
							dCordX = f.parse(CordX).doubleValue();

							dCordY = f.parse(CordY).doubleValue();
							dCordZ = f.parse(CordZ).doubleValue();
						} catch (ParseException e1) {

						}
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& CovistoZselectPanel.thirdDimension == (int) dCordZ && parent.ndims > 3) {

							parent.rowchoice = row;
							distmin = dist;

						}
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims <= 3) {

							parent.rowchoice = row;
							distmin = dist;

						}

					}

					parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == parent.rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelSecond.repaint();
					parent.panelSecond.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}
       
       public static void displayclicked(InteractiveCloudify parent, int trackindex) {

   		// Make something happen
   		parent.row = trackindex;
   		String ID = (String) parent.table.getValueAt(trackindex, 0);
   		if(parent.resultimp!=null)
			parent.resultimp.close();
   		DisplayTrack display = new DisplayTrack(parent, parent.Globalmodel);
		display.getImp();
		
		
   		ArrayList<Pair<String, double[]>> currentresultIntASec = new ArrayList<Pair<String, double[]>>();

   		for (Pair<String, double[]> currentInt : parent.resultIntensityASec) {

   			if (ID.equals(currentInt.getA())) {

   				currentresultIntASec.add(currentInt);

   			}

   		}

   		ArrayList<Pair<String, double[]>> currentresultIntBSec = new ArrayList<Pair<String, double[]>>();
   		for (Pair<String, double[]> currentIntB : parent.resultIntensityBSec) {

   			if (ID.equals(currentIntB.getA())) {

   				currentresultIntBSec.add(currentIntB);

   			}

   		}
   		
   		ArrayList<Pair<String, double[]>> currentresultIntA = new ArrayList<Pair<String, double[]>>();

   		for (Pair<String, double[]> currentInt : parent.resultIntensityA) {

   			if (ID.equals(currentInt.getA())) {

   				currentresultIntA.add(currentInt);

   			}

   		}

   		ArrayList<Pair<String, double[]>> currentresultIntB = new ArrayList<Pair<String, double[]>>();
   		for (Pair<String, double[]> currentIntB : parent.resultIntensityB) {

   			if (ID.equals(currentIntB.getA())) {

   				currentresultIntB.add(currentIntB);

   			}

   		}

   		if (parent.imp != null) {
   			parent.imp.setOverlay(parent.overlay);
   			parent.imp.updateAndDraw();
   		}

   		if(parent.IntensityAdataset!=null)
   		parent.IntensityAdataset.removeAllSeries();
   		parent.IntensityAdataset.addSeries(ChartMaker.drawCurvePoints(currentresultIntA));

   		parent.chartIntensityA = utility.ChartMaker.makeChart(parent.IntensityAdataset, "Cell - cloud intensity evolution (Ch1)", "Time", "Intensity");
   		
   	

   		parent.jFreeChartFrameIntensityA.dispose();
   		parent.jFreeChartFrameIntensityA.repaint();
   		
   		
   		if(parent.IntensityBdataset!=null)
   	   		parent.IntensityBdataset.removeAllSeries();
   	   		parent.IntensityBdataset.addSeries(ChartMaker.drawCurvePoints(currentresultIntB));

   	   		parent.chartIntensityB = utility.ChartMaker.makeChart(parent.IntensityBdataset, "Only cloud intensity evolution (Ch1)", "Time", "Intensity");
   	   		
   	   	

   	   		parent.jFreeChartFrameIntensityB.dispose();
   	   		parent.jFreeChartFrameIntensityB.repaint();
   	   		
   	   		
   	 	if(parent.IntensityAdatasetSec!=null)
   	   		parent.IntensityAdatasetSec.removeAllSeries();
   	   		parent.IntensityAdatasetSec.addSeries(ChartMaker.drawCurvePoints(currentresultIntA));

   	   		parent.chartIntensityASec = utility.ChartMaker.makeChart(parent.IntensityAdatasetSec, "Cell - cloud intensity evolution (Ch2)", "Time", "Intensity");
   	   		
   	   	

   	   		parent.jFreeChartFrameIntensityASec.dispose();
   	   		parent.jFreeChartFrameIntensityASec.repaint();
   	   		
   	   		
   	   		if(parent.IntensityBdatasetSec!=null)
   	   	   		parent.IntensityBdatasetSec.removeAllSeries();
   	   	   		parent.IntensityBdatasetSec.addSeries(ChartMaker.drawCurvePoints(currentresultIntBSec));

   	   	   		parent.chartIntensityBSec = utility.ChartMaker.makeChart(parent.IntensityBdatasetSec, "Only cloud intensity evolution (Ch2)", "Time", "Intensity");
   	   	   		
   	   	   	

   	   	   		parent.jFreeChartFrameIntensityBSec.dispose();
   	   	   		parent.jFreeChartFrameIntensityBSec.repaint();
   	   	   		
   	   		
   	   		
   	   		

   	}

		
		
	}


