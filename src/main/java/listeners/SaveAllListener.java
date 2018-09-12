package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pluginTools.InteractiveCloudify;

public class SaveAllListener implements ActionListener {

	final InteractiveCloudify parent;

	public SaveAllListener(final InteractiveCloudify parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Savefunction();

	}

	public void Savefunction() {

for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
		File fichier = new File(parent.saveFile + "//" + parent.addToName + "TrackID" + ID + "Channel 1" + ".txt");

		FileWriter fw;
		try {
			fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("\tTime (px)\t Total Intensity Cell \t Average Intensity Cell  \t AreaCell \t Intensity Cloud  \t Area Cloud   \n");

			for (int index = 0; index < parent.resultIntensityA.size(); ++index) {

				if (ID.equals(parent.resultIntensityA.get(index).getA())) {

					int time = (int) parent.resultIntensityA.get(index).getB()[0];
					double intensityCell = parent.resultIntensityA.get(index).getB()[1];
					double averageintensityCell = parent.resultIntensityA.get(index).getB()[3];
					double areaCell = parent.resultIntensityA.get(index).getB()[5];
					
					double intensityCloud = parent.resultIntensityB.get(index).getB()[1];
					double areaCloud = parent.resultIntensityB.get(index).getB()[2];

					bw.write("\t" + time + "\t" + "\t" + intensityCell + "\t" + "\t" + averageintensityCell  + "\t" + "\t" + areaCell  + "\t" + "\t"   + intensityCloud + "\t" + "\t"   + areaCloud +

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

			bwtwo.write("\tTime (px)\t Total Intensity Cell \t Average Intensity Cell  \t AreaCell \t Intensity Cloud \t Area Cloud  \n");

			for (int index = 0; index < parent.resultIntensityA.size(); ++index) {

				if (ID.equals(parent.resultIntensityA.get(index).getA())) {

					int time = (int) parent.resultIntensityA.get(index).getB()[0];
					double intensityCell = parent.resultIntensityA.get(index).getB()[2];
					double averageintensityCell = parent.resultIntensityA.get(index).getB()[4];
					double areaCell = parent.resultIntensityA.get(index).getB()[5];
					double intensityCloud = parent.resultIntensityBSec.get(index).getB()[1];
					double areaCloud = parent.resultIntensityB.get(index).getB()[2];
					bwtwo.write("\t" + time + "\t" + "\t" + intensityCell + "\t" + "\t" + averageintensityCell  + "\t" + "\t" + areaCell  + "\t" + "\t"   + intensityCloud + "\t" + "\t"   + areaCloud +

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
}
