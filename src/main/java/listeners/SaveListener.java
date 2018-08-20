package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pluginTools.InteractiveCloudify;

public class SaveListener implements ActionListener {

	final InteractiveCloudify parent;

	public SaveListener(final InteractiveCloudify parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Savefunction();

	}

	public void Savefunction() {

		String ID = parent.selectedID;
		File fichier = new File(parent.saveFile + "//" + parent.addToName + "TrackID" + ID + "Channel 1" + ".txt");

		FileWriter fw;
		try {
			fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("\tTime (px)\t Intensity Cell \t Intensity Cloud  \n");

			for (int index = 0; index < parent.resultIntensityA.size(); ++index) {

				if (ID.equals(parent.resultIntensityA.get(index).getA())) {

					int time = (int) parent.resultIntensityA.get(index).getB()[0];
					double intensityCell = parent.resultIntensityA.get(index).getB()[1];
					double intensityCloud = parent.resultIntensityB.get(index).getB()[1];

					bw.write("\t" + time + "\t" + "\t" + intensityCell + "\t" + "\t" + intensityCloud +

							"\n");

				}

			}
			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File fichiertwo = new File(parent.saveFile + "//" + parent.addToName + "TrackID" + ID + "Channel 2" + ".txt");

		FileWriter fwtwo;
		try {
			fwtwo = new FileWriter(fichiertwo);
			BufferedWriter bwtwo = new BufferedWriter(fwtwo);

			bwtwo.write("\tTime (px)\t Intensity Cell \t Intensity Cloud  \n");

			for (int index = 0; index < parent.resultIntensityASec.size(); ++index) {

				if (ID.equals(parent.resultIntensityASec.get(index).getA())) {

					int time = (int) parent.resultIntensityASec.get(index).getB()[0];
					double intensityCell = parent.resultIntensityASec.get(index).getB()[1];
					double intensityCloud = parent.resultIntensityBSec.get(index).getB()[1];

					bwtwo.write("\t" + time + "\t" + "\t" + intensityCell + "\t" + "\t" + intensityCloud +

							"\n");

				}

			}
			bwtwo.close();
			fwtwo.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
