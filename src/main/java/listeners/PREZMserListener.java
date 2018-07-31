package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import cloudFinder.ExecuteMser;
import pluginTools.InteractiveCloudify;

public class PREZMserListener implements ActionListener {
	
	final InteractiveCloudify secparent;

	
	public PREZMserListener(InteractiveCloudify secparent) {
		

	this.secparent = secparent;
	
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

	

		ExecuteMser dosnake = new ExecuteMser(secparent);
		dosnake.execute();

	}

}
