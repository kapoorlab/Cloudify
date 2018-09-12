package kalmanTrackListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import pluginTools.InteractiveCloudify;
import snakeSegmentation.SingleAllTrack;
import trackIT.TrackResult;

public class LinkobjectListener implements ActionListener {
	
	final InteractiveCloudify parent;
	
	public LinkobjectListener(final InteractiveCloudify parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				go();

			}

		});

	}
	

	public void go() {

		parent.Tracklist.clear();
		parent.Finalresult.clear();
		
		TrackResult track = new TrackResult(parent);
		track.execute();
		
		

	}

}
