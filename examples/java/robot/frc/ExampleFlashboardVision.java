package examples.robot.frc;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.vision.Analysis;
import edu.flash3388.flashlib.vision.GrayFilter;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.vision.VisionProcessing;

/*
 * A simple example showcasing the use of flashboard's vision control
 */
public class ExampleFlashboardVision extends IterativeFRCRobot{

	Vision vision;
	
	@Override
	protected void initRobot() {
		
		/*
		 * Get the vision object. If flashboard was not initialized, this will be null
		 */
		vision = Flashboard.getVision();
		
		/*
		 * Create a new vision processing object and add a filter to it
		 */
		VisionProcessing proc = new VisionProcessing();
		proc.addFilter(new GrayFilter(100, 180));
		
		/*
		 * Add the processing object to the vision. This will send the processing to the flashboard. But, remember
		 * that any local custom filters will not be recognized by the flashboard!
		 * Note that the remote might have its own processing objects, so this might not be processing number
		 * 0.
		 */
		vision.addProcessing(proc);
		
		/*
		 * Select the processing object to use, will update the flashboard.
		 */
		vision.selectProcessing(0);
		
		/*
		 * Set the timeout for an analysis object to be considered up-to-date. Affects the return value of 
		 * hasNewAnalysis(). We gave it the value of 500ms.
		 */
		vision.setNewAnalysisTimeout(500);
		
		/*
		 * Starts the flashboard
		 */
		Flashboard.start();
	}

	@Override
	protected void teleopInit() {
		/*
		 * Start the vision. 
		 */
		vision.start();
	}
	@Override
	protected void teleopPeriodic() {
		/*
		 * Returns true if an analysis was received and the time since its receive did not exceed a timeout
		 */
		if(vision.hasNewAnalysis()){
			/*
			 * Get the analysis object
			 */
			Analysis an = vision.getAnalysis();
			/*
			 * Print the analysis object to the DriverStation console 
			 */
			System.out.println(an.toString());
		}
	}

	@Override
	protected void autonomousInit() {}
	@Override
	protected void autonomousPeriodic() {}

	@Override
	protected void disabledInit() {
		/*
		 * Stop the vision. 
		 */
		vision.stop();
	}
	@Override
	protected void disabledPeriodic() {
	}
}
