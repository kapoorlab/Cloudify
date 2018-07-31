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
import zGUI.CovistoZselectPanel;


public class PreZlocListener implements TextListener {


	final InteractiveCloudify secparent;
	boolean pressed;


	public PreZlocListener(InteractiveCloudify secparent, boolean pressed) {

		
		
		this.pressed = pressed;
		this.secparent = secparent;
		
		
	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();

		
		tc.addKeyListener(new KeyListener() {
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
				if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !pressed) {
					pressed = true;
					if (CovistoZselectPanel.thirdDimension > CovistoZselectPanel.thirdDimensionSize) {
						IJ.log("Max frame number exceeded, moving to last frame instead");
						CovistoZselectPanel.thirdDimension = CovistoZselectPanel.thirdDimensionSize;
					} else
						CovistoZselectPanel.thirdDimension = Integer.parseInt(s);
					CloudShowView show = new CloudShowView(secparent);
					show.shownewZ();
					
					CovistoZselectPanel.zText.setText("Current Z = " + CovistoZselectPanel.thirdDimension);
					CovistoZselectPanel.zgenText.setText("Current Z / T = " + CovistoZselectPanel.thirdDimension);
					secparent.updatePreview(ValueChange.THIRDDIMmouse);
					
					
				}
				CovistoZselectPanel.zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(CovistoZselectPanel.thirdDimension, CovistoZselectPanel.thirdDimensionsliderInit, 
						CovistoZselectPanel.thirdDimensionSize, CovistoZselectPanel.scrollbarSize));
				CovistoZselectPanel.zslider.repaint();
				CovistoZselectPanel.zslider.validate();

			}
		});
		
		

	}
}