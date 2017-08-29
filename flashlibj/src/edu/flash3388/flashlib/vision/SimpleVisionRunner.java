package edu.flash3388.flashlib.vision;
import edu.flash3388.flashlib.util.beans.observable.ObservableProperty;
import edu.flash3388.flashlib.util.beans.observable.SimpleObservableProperty;

/**
 * A simple synchronized implementation of {@link VisionRunner}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleVisionRunner extends VisionRunner{

	private Analysis analysis;
	private ObservableProperty<Object> newFrame = new SimpleObservableProperty<Object>();
	
	public SimpleVisionRunner(String name) {
		super(name);
	}
	public SimpleVisionRunner() {
		this("SimpleVisionRunner");
	}
	
	@Override
	public boolean hasAnalysis() {
		return analysis != null;
	}

	@Override
	public Analysis getAnalysis() {
		return analysis;
	}

	@Override
	public ObservableProperty<Object> frameProperty() {
		return newFrame;
	}

	@Override
	protected Object getNextFrame() {
		Object frame = newFrame.getValue();
		if(!newFrame.isBound())
			newFrame.setValue(null);
		return frame;
	}
	@Override
	protected void newAnalysis(Analysis an) {
		analysis = an;
	}
}
