package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fileListeners.ChooseOrigMap;
import fileListeners.ChooseSecOrigMap;
import fileListeners.ChooseSegAMap;
import fileListeners.ChooseSegBMap;
import fileListeners.ChooseSegCMap;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import loadfile.CovistoOneChFileLoader;
import loadfile.CovistoThreeChForceFileLoader;
import loadfile.CovistoTwoChForceFileLoader;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class CloudifyFileChooser extends JPanel {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  public JFrame Cardframe = new JFrame("Nuclear cloud aggregate tracker");
	  public JPanel panelCont = new JPanel();
	  public ImagePlus impOrig, impOrigSec, impSegA, impSegB, impSegC;
	  public File impOrigfile, impOrigSecfile, impSegAfile, impSegBfile, impSegCfile;
	  public JPanel panelFirst = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public JPanel Panelsuperfile = new JPanel();
	  public JPanel Panelfileoriginal = new JPanel();
	  public JPanel Paneldone = new JPanel();
	  public JPanel Panelrun = new JPanel();
	  public final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public final String[] imageNames, blankimageNames;
	  public JComboBox<String> ChooseImage;
	  public JComboBox<String> ChoosesuperImage;
	  public JComboBox<String> ChooseoriginalImage;
	  public JComboBox<String> ChoosesecImage;
	  public JButton Done =  new JButton("Finished choosing files, start CTrack");
	  public boolean superpixel = false;
	  public boolean simple = false;
	  public boolean curvesuper = true;
	  public boolean curvesimple = false;
	  public boolean twochannel = false;
	  

	  
	  
	  public String chooseSegstring = "Segmentation Images (Cells and Clouds)"; 
	  public Border chooseSeg = new CompoundBorder(new TitledBorder(chooseSegstring),
				new EmptyBorder(c.insets));
	  public String chooseoriginalfilestring = "Choose original Image (C1 and C2)";
	  public Border chooseoriginalfile = new CompoundBorder(new TitledBorder(chooseoriginalfilestring),
				new EmptyBorder(c.insets));
	  public String donestring = "Done Selection";
	  public Border LoadCtrack = new CompoundBorder(new TitledBorder(donestring),
				new EmptyBorder(c.insets));
	
	  
	  public CloudifyFileChooser() {
		
		
		  
		   panelFirst.setLayout(layout);
		   
		   Paneldone.setLayout(layout);
	       CardLayout cl = new CardLayout();
			
			panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
			imageNames = WindowManager.getImageTitles();
			blankimageNames = new String[imageNames.length + 1];
			blankimageNames[0] = " " ;
			
			for(int i = 0; i < imageNames.length; ++i)
				blankimageNames[i + 1] = imageNames[i];
			
			ChooseImage = new JComboBox<String>(blankimageNames);
			ChooseoriginalImage = new JComboBox<String>(blankimageNames);
			ChoosesecImage = new JComboBox<String>(blankimageNames);
			ChoosesuperImage = new JComboBox<String>(blankimageNames);
			
			
			
			CovistoTwoChForceFileLoader original = new CovistoTwoChForceFileLoader(chooseoriginalfilestring, blankimageNames);
			
			Panelfileoriginal = original.TwoChannelOption();
			
			
			panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			CovistoThreeChForceFileLoader segmentation = new CovistoThreeChForceFileLoader(chooseSegstring, blankimageNames);
			Panelfile = segmentation.ThreeChannelOption();
			
			
			panelFirst.add(Panelfile, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Paneldone.setBorder(LoadCtrack);
			panelFirst.add(Paneldone, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			// Listeneres 
			
			
			original.ChooseImage.addActionListener(new ChooseOrigMap(this, original.ChooseImage));
			original.ChoosesecImage.addActionListener(new ChooseSecOrigMap(this, original.ChoosesecImage));
			segmentation.ChooseImage.addActionListener(new ChooseSegAMap(this, segmentation.ChooseImage));
			segmentation.ChoosesecImage.addActionListener(new ChooseSegBMap(this, segmentation.ChoosesecImage));
			segmentation.ChoosethirdImage.addActionListener(new ChooseSegCMap(this, segmentation.ChoosethirdImage));
			
			Done.addActionListener(new DoneListener());
			panelFirst.setVisible(true);
			cl.show(panelCont, "1");
			Cardframe.add(panelCont, "Center");
			Panelsuperfile.setEnabled(true);
			ChoosesuperImage.setEnabled(true);
		
			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
		}
		
	  public class DoneListener implements ActionListener{
		  
		  
		  @Override
			public void actionPerformed(ActionEvent e) {
			  
			  
			  try {
				DoneCurr(Cardframe);
			} catch (ImgIOException e1) {

				// TODO Auto-generated catch block

			
			}
		  }
		  
		  
		  
	  }
	  
	
	  public void DoneCurr(Frame parent) throws ImgIOException{
			
			// Tracking and Measurement is done with imageA 
	        
		    org.apache.log4j.BasicConfigurator.configure();
			
			RandomAccessibleInterval<FloatType> imageOrig = new ImgOpener().openImgs(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType()).iterator().next();
			RandomAccessibleInterval<FloatType> imageOrigSec = new ImgOpener().openImgs(impOrigSec.getOriginalFileInfo().directory + impOrigSec.getOriginalFileInfo().fileName, new FloatType()).iterator().next();
			RandomAccessibleInterval<IntType> imageSegA = new ImgOpener().openImgs(impSegA.getOriginalFileInfo().directory + impSegA.getOriginalFileInfo().fileName , new IntType()).iterator().next();
			RandomAccessibleInterval<FloatType> imageSegB = new ImgOpener().openImgs(impSegB.getOriginalFileInfo().directory + impSegB.getOriginalFileInfo().fileName , new FloatType()).iterator().next();
			RandomAccessibleInterval<FloatType> imageSegC = new ImgOpener().openImgs(impSegC.getOriginalFileInfo().directory + impSegC.getOriginalFileInfo().fileName , new FloatType()).iterator().next();
			
			
			WindowManager.closeAllWindows();
			
			new InteractiveCloudify(imageOrig, imageOrigSec, imageSegA, imageSegB, imageSegC,impOrig.getOriginalFileInfo().fileName,impOrigSec.getOriginalFileInfo().fileName    ).run(null);
			close(parent);
			
			
		}
	  protected final void close(final Frame parent) {
			if (parent != null)
				parent.dispose();

			
		}

	
	
	
}
