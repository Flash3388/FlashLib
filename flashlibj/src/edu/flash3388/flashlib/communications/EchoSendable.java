package edu.flash3388.flashlib.communications;

/**
 * A testing sendable that acts as an echo server or client. Uses its name and sends it to its remote counter part who
 * returns it back.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class EchoSendable extends Sendable{
	
	private byte[] origindata;
	private byte[] byteData = new byte[0];
	private boolean rec = false;
	
	public EchoSendable(String name, byte type) {
		super(name, type);
		origindata = name.getBytes();
	}
	public EchoSendable(String name, int id, byte type){
		super(name, id, type);
	}

	@Override
	public void newData(byte[] data) {
		if(data.length != byteData.length)
			byteData = new byte[data.length];
		System.arraycopy(data, 0, byteData, 0, 
				data.length);

		rec = true;
	}
	@Override
	public byte[] dataForTransmition() {
		byte[] data = new byte[byteData.length];
		System.arraycopy(byteData, 0, data, 0, data.length);
		rec = false;
		return data;
	}
	@Override
	public boolean hasChanged() {
		return rec;
	}
	@Override
	public void onConnection() {
		if(origindata == null){
			rec = false;
		}else{
			rec = true;
			if(byteData.length != origindata.length)
				byteData = new byte[origindata.length];
			System.arraycopy(origindata, 0, byteData, 0, origindata.length);
		}
	}
	@Override
	public void onConnectionLost() {}
}
