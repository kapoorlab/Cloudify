package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveCloudify;
import pluginTools.InteractiveCloudify.ValueChange;
import timeGUI.CovistoTimeselectPanel;


public class PreTlocListener implements TextListener {
	
	
	final InteractiveCloudify secparent;
	
	boolean pressed;

	
	public PreTlocListener(InteractiveCloudify secparent, boolean pressed) {
		
      
      this.pressed = pressed;
      
      this.secparent = secparent;
	
	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	   
		 tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		if (CovistoTimeselectPanel.fourthDimension > CovistoTimeselectPanel.fourthDimensionSize) {
							IJ.log("Max frame number exceeded, moving to last frame instead");
							CovistoTimeselectPanel.fourthDimension = CovistoTimeselectPanel.fourthDimensionSize;
						} else
							CovistoTimeselectPanel.fourthDimension = Integer.parseInt(s);
			    		CloudShowView show = new CloudShowView(secparent);
					show.shownewT();
					CovistoTimeselectPanel.timeText.setText("Current T = " + CovistoTimeselectPanel.fourthDimension);
					
					secparent.updatePreview(ValueChange.FOURTHDIMmouse);
					
					
					 }
			    	CovistoTimeselectPanel.timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(CovistoTimeselectPanel.fourthDimension, CovistoTimeselectPanel.fourthDimensionsliderInit,
			    			CovistoTimeselectPanel.fourthDimensionSize, CovistoTimeselectPanel.scrollbarSize));
			    	CovistoTimeselectPanel.timeslider.repaint();
			    	CovistoTimeselectPanel.timeslider.validate();

			    }
			});
	

	

}

}
