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

			bw.write("\tTime (px)\t Total Intensity Cell \t Average Intensity Cell  \t AreaCell \t Total Intensity Cloud  \t Mean Intensity Cloud \t AreaCloud \n");


			for (int index = 0; index < parent.resultIntensity.size(); ++index) {

				if (ID.equals(parent.resultIntensity.get(index).getA())) {

					int time = (int) parent.resultIntensity.get(index).getB().thirdDimension;
					double intensityCell = parent.resultIntensity.get(index).getB().totalIntensityChA;
					double averageintensityCell = 0;
					double areaCell = parent.resultIntensity.get(index).getB().Nucleiarea;
					
					double intensityCloud = parent.resultIntensity.get(index).getB().CloudIntensityChA;
					double areaCloud = parent.resultIntensity.get(index).getB().Cloudarea;
					double meanintensityCloud = 0 ;
					if(areaCloud!= 0)
					 meanintensityCloud = intensityCloud / areaCloud;
					if(areaCell!=0)
						averageintensityCell = intensityCell / areaCell;
					bw.write("\t" + time + "\t" + "\t" + parent.nf.format(intensityCell) + "\t" + "\t" + parent.nf.format(averageintensityCell)  + "\t" + "\t"  + "\t" + "\t"
		                     + parent.nf.format(areaCell)  + "\t" + "\t"   + parent.nf.format(intensityCloud) +  "\t" + "\t" +
								"\t" + "\t"   + parent.nf.format(meanintensityCloud) + "\t" + "\t"  + "\t" + "\t"  + parent.nf.format(areaCloud) +
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

			bwtwo.write("\tTime (px)\t Total Intensity Cell \t Average Intensity Cell  \t AreaCell \t Total Intensity Cloud  \t Mean Intensity Cloud \t AreaCloud \n");


			for (int index = 0; index < parent.resultIntensity.size(); ++index) {

				if (ID.equals(parent.resultIntensity.get(index).getA())) {

					int time = (int) parent.resultIntensity.get(index).getB().thirdDimension;
					double intensityCell = parent.resultIntensity.get(index).getB().totalIntensityChB;
					double averageintensityCell = 0;
					double areaCell = parent.resultIntensity.get(index).getB().Nucleiarea;
					
					double intensityCloud = parent.resultIntensity.get(index).getB().CloudIntensityChB;
					double areaCloud = parent.resultIntensity.get(index).getB().Cloudarea;
					double meanintensityCloud = 0 ;
					if(areaCloud!= 0)
					 meanintensityCloud = intensityCloud / areaCloud;
					if(areaCell!=0)
						averageintensityCell = intensityCell / areaCell;
					bwtwo.write("\t" + time + "\t" + "\t" + parent.nf.format(intensityCell) + "\t" + "\t" + parent.nf.format(averageintensityCell)  + "\t" + "\t"  + "\t" + "\t"
		                     + parent.nf.format(areaCell)  + "\t" + "\t"   + parent.nf.format(intensityCloud) +
		                      "\t" + "\t" +"\t" + "\t"   + parent.nf.format(meanintensityCloud) + "\t" + "\t" + "\t" + "\t"   + parent.nf.format(areaCloud) +
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
