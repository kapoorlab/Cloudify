package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import cloudFinder.ExecuteDOG;
import pluginTools.InteractiveCloudify;

public class PREApplyDog3DListener implements ActionListener {
	
	final InteractiveCloudify secparent;
	

	
	public PREApplyDog3DListener(InteractiveCloudify secparent) {
        
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

	

		ExecuteDOG dosnake = new ExecuteDOG(secparent);
		dosnake.execute();

	}

}
