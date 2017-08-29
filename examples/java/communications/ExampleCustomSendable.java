package examples.communications;

import edu.flash3388.flashlib.communications.Sendable;

/*
 * This class shows the requirements of creating a custom Sendable class.
 * 
 * The first step would be to extend the Sendable class and implement the required methods and a constructor.
 * A typical Sendable should have 2 constructors: one for user creation, another for SendableCreator creation.
 * The user-creation constructor should receive any custom parameters necessary and maybe the sendable name, 
 * if wanted. The type should be a class constant. 
 * The SendableCreator-creation constructor should receive the name of the sendable and that's it. The type should 
 * be a class constant and the id will be set automatically to match. Any required parameters should be received 
 * after initialization and read using 'newData'.
 * 
 * It is usually a good idea to create 2 Sendable classes, one is responsible for data on one side, 
 * another on the other side. For example, the Flashboard's DashboardDoubleProperty is responsible for
 * sending double data, where as the other side has a DoubleProperty class which receives it and displays it
 * as a label.
 * 
 * For data receiving we have newData, which receives a byte array which was sent from the corresponding 
 * remote Sendable. It is a good idea to classify each data with a start byte to indicate the type of data.
 * So the byte array consists of a type byte and the rest are data bytes.
 * 
 * For sending data we have dataForTransmition which returns a byte array to be sent. This method will be
 * called only if hasChanged returns true. So it is useful to contain a boolean condition to when a data should be
 * sent. Like in data received, when sending the first byte should indicate the type of data to the remote Sendable 
 * if there are several types of data.
 */
public class ExampleCustomSendable extends Sendable{

	/*
	 * The type of the sendable
	 */
	public static final byte TYPE = 0x0;
	
	/*
	 * Different types of data which are received and sent through this sendable
	 */
	public static final byte DATA_TYPE_1 = 0x1;
	public static final byte DATA_TYPE_2 = 0x2;
	
	/*
	 * Boolean variable which indicate changes to types 1 and 2 data
	 */
	private boolean type1Changed = false, type2Changed = false;
	
	/*
	 * SendableCreator constructor. Receives only the name of the sendable.
	 */
	public ExampleCustomSendable(String name) {
		super(name, TYPE);
	}

	/*
	 * The data receiving method.
	 * We check the first byte to indicate the type of data received and handle accordingly.
	 */
	@Override
	public void newData(byte[] data) {
		if(data[0] == DATA_TYPE_1){
			//handle data from type 1
		}else if(data[0] == DATA_TYPE_2){
			//handle data from type 2
		}
	}
	/*
	 * If a change occurred, return a byte array with the new data. 
	 */
	@Override
	public byte[] dataForTransmition() {
		if(type1Changed){//if type 1 was changed and needs an update
			type1Changed = false;
			return new byte[]{DATA_TYPE_1};//this array should also have the data 
		}
		if(type2Changed){//if type 2 was changed and needs an update
			type2Changed = false;
			return new byte[]{DATA_TYPE_2};//this array should also have the data 
		}
		return null;//if there is nothing to do so return null
	}
	/*
	 * If our boolean variables indicate changes to type 1 or 2 of data
	 */
	@Override
	public boolean hasChanged() {
		return type1Changed || type2Changed;//if one of the boolean variables return true
	}

	/*
	 * When the manager has established connection to the remote side, this will be called.
	 * If this sendable is attached and connection was already established, than call this.
	 * 
	 * Should be used to reset any data
	 */
	@Override
	public void onConnection() {
		type1Changed = true;
		type2Changed = true;
	}
	/*
	 * If connection with the remote side was lost
	 */
	@Override
	public void onConnectionLost() {
	}
}
