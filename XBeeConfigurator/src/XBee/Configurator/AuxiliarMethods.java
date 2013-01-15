package XBee.Configurator;

import java.util.LinkedList;
import android.content.Context;
import android.util.Log;

public class AuxiliarMethods {

	private String debug = "debug";
	private int MAX_DEVICES=2;

	/*
	 * 
	 * METHODS TO RETRIEVE THE DATA FROM PACKETS SENT BY THE ARDUINO
	 */

	// METHOD TO CONVERT THE SENT DATA INTO STRING
	public String getData(byte[] buffer) {
		final StringBuilder textBuilder = new StringBuilder();
		int textLength = buffer[2];
		int textEndIndex = 3 + textLength;
		for (int x = 3; x < textEndIndex; x++) {

			if (buffer[x] >= 0 && buffer[x] <= 9) {
				textBuilder.append('0');
			}

			textBuilder.append(Integer.toHexString(buffer[x] & 0xFF));

			// if (buffer[x] == 0) {
			// textBuilder.append('0');
			// }

		}
		return textBuilder.toString().toUpperCase();
	}

	public byte[] getDataByte(byte[] buffer) {
		int textLength = buffer[2];
		int textEndIndex = 3 + textLength;
		byte b[] = new byte[textLength];

		for (int x = 3; x < textEndIndex; x++) {
			b[x - 3] = buffer[x];
		}

		return b;
	}

	// METHOD TO RETRIEVE THE DEVICE TYPE FROM THE SENT DATA
	public String getDeviceType(byte[] buffer, Context c, String request) {
		final StringBuilder text = new StringBuilder();
		int textLength = buffer[2];
		int textEndIndex = 3 + textLength;

		if (request.equals("ND"))
			for (int i = 13; i < 18; i++) {
				text.append((char) buffer[i]);
			}
		else
			for (int i = 3; i < textEndIndex; i++)
				text.append((char) buffer[i]);

		return this.getDeviceTypeToString(c, text.toString());
	}

	private String getDeviceTypeToString(Context c, String id) {
		String type = "";

		switch (Integer.parseInt(id)) {
		case 10000:
			type = c.getString(R.string.coordinator);
			break;
		case 20000:
			type = c.getString(R.string.router);
			break;
		case 20110:
			type = c.getString(R.string.routerMotionSensor);
			break;
		case 20120:
			type = c.getString(R.string.routerLuminanceSensor);
			break;
		case 30000:
			type = c.getString(R.string.sensor);
			break;
		case 30010:
			type = c.getString(R.string.motionSensor);
			break;
		case 30020:
			type = c.getString(R.string.luminanceSensor);
			break;
		case 40000:
			type = c.getString(R.string.actuator);
			break;
		case 40010:
			type = c.getString(R.string.actuatorMotion);
			break;
		case 40020:
			type = c.getString(R.string.actuatorLuminance);
			break;
		default:
			type = c.getString(R.string.unknown);
			break;

		}

		return type;
	}

	/*
	 * 
	 * METHODS TO CONVERT A BYTE ARRAY TO INT AND VICE VERSA
	 */

	public int getDataInt(byte[] buffer) {

		int dataLength = buffer[2];
		int dataEndIndex = 3 + dataLength;

		int num = -1;

		int dataStart = 0;

		// LOOKS UP FOR THE DATA START INDEX
		for (int i = 3; i < dataEndIndex; i++) {
			if (buffer[i] != 0) {
				dataStart = i;
				i = dataEndIndex;
			}
		}

		// RETRIEVES THE DATA
		if (dataStart > 0) {
			int value = 0;
			for (int i = dataStart; i < dataEndIndex; i++) {
				value = (value << 8) | (buffer[i] & 0xFF);
			}
			if (value > -1)
				num = value;
		}

		return num;
	}

	public byte[] intToByteArray(int n) {
		byte b[] = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[4 - 1 - i] = (byte) ((n >> 8 * i) & 0xFF);
		}
		return b;
	}

	/*
	 * 
	 * METHOD FOT BYTE TO STRING CONVERSION
	 */
	public String convertByteToString(byte[] b) {

		StringBuilder textBuilder = new StringBuilder();

		int middle = (b.length / 2) - 1;

		for (int i = 0; i < b.length; i++) {
			if (b[i] >= 0 && b[i] <= 15) {
				textBuilder.append("0");
			}
			textBuilder.append(Integer.toHexString(b[i] & 0xFF));
			if (i == middle)
				textBuilder.append(" ");

		}
		return textBuilder.toString().toUpperCase();
	}

	/*
	 * 
	 * METHODS FOR STRING TO BYTE CONVERSION
	 */
	public byte[] convertStringAddressToByte(String s) throws Exception {

		s = s.toLowerCase().replace(" ", "").trim();
		s = checkString(s);
		// s=checkString(s.toLowerCase().replace(" ", "").trim());

		byte[] sh = new byte[4];
		byte[] sl = new byte[4];

		for (int i = 0; i < s.length() / 2; i = i + 2) {

			sh[i / 2] = this.conversionStringToByte(s.substring(i, i + 2));
			sl[i / 2] = this.conversionStringToByte(s.substring(
					(i + s.length() / 2), i + s.length() / 2 + 2));

		}

		byte address[] = new byte[8];
		for (int i = 0; i < 4; i++) {
			address[i] = sh[i];
			address[i + 4] = sl[i];
		}

		return address;
	}

	public byte[] convertStringToByte(String s) throws Exception {

		s = s.replace(" ", "").trim().toLowerCase();
		int l = (int) ((double) s.length() / 2 + .5);
		byte[] b = new byte[l];

		if (s.length() % 2 != 0) {
			s = "0" + s;
		}

		for (int i = 0; i < s.length(); i = i + 2) {
			b[i / 2] = this.conversionStringToByte(s.substring(i, i + 2));
		}

		return b;
	}

	private String checkString(String s) throws Exception {

		if (s.length() < 14) {
			throw new Exception();
		}

		if (s.charAt(0) == '0' && s.charAt(1) != '0' && s.length() == 15) {
			s = "0" + s;
		}

		if (s.length() == 14) {
			s = "00" + s;
		}

		return s;
	}

	private byte conversionStringToByte(String s) throws Exception {

		int n1 = getByteValueForConversion(s.charAt(0));
		int n2 = getByteValueForConversion(s.charAt(1));

		byte b;

		if (n1 != -1 && n2 != -1) {
			b = (byte) (16 * n1 + n2);

		} else
			throw new Exception();

		return b;
	}

	private int getByteValueForConversion(int n) {
		if (n >= 97 && n <= 102) {
			n -= 87; // converte para o valor correspondente se for uma letra
		} else if (n >= 48 && n <= 57) {
			n -= 48; // converte para o valor correspondente se for um numero
		} else {
			n = -1;
		}

		return n;
	}

	/*
	 * OTHER METHODS
	 */

	// Method only to be used in XBeeConfiguratorActivity
	// in the method sendSetActuatorToSensor, to construct
	// the variable 'message'
	public byte[] newMessage(int size, byte[] panId) {
		if (panId != null)
			return new byte[1 + (1 + size) * 8 + panId.length];
		else
			return new byte[1 + (1 + size) * 8];
	}

	
	
//	public byte[] newRemoteConfigMessage(LinkedList<byte[]> newAddrAssociated,
//			int oldSize, byte[] addrDevice,
//			byte[] remotePanId) {
//
//		byte msg[] = new byte[31];
//
//		int newSize = newAddrAssociated.size();
//		
//		// Populates the remote address
//		for (int i = 0; i < 8; i++) {
//			msg[i] = addrDevice[i];
//		}
//
//		// Populates the message with the number of addresses
//		msg[8] = (byte) newSize;
//
//		// Indication if the panID was changed
//		// and populates the message
//		if (remotePanId != null) {
//			msg[9] = 1;
//			// 27->28
//			for (int i = 0; i < 2; i++) {
//				msg[27 + i] = remotePanId[i+2];
//			}
//		}
//
//
//
//		// A DEVICE WAS ADDED OR REMOVED
//		if (newSize > oldSize || (newSize > 0 && newSize < oldSize)) {
//			for (int i = 0; i < newSize; i++) {
//				for (int j = 0; j < 8; j++) {
//					msg[11 + i * 8 + j] = newAddrAssociated.get(i)[j];
//				}
//			}
//		}
//
//		return msg;
//	}
	
	public byte[] newConfigPacket(){
		return new byte[32];
	}
	
	public byte[] packet_put_panId(byte[] msg, byte[] id){
		// Indication if the panID was changed
		// and populates the message
		if (id != null && id.length==2) {
			//Indication
			msg[9] = 1;
			// 28->29
			for (int i = 0; i < 2; i++) {
				msg[28 + i] = id[i];
			}
		}
		
		return msg;
	}
	
	public byte[] packet_put_data(byte[] msg, byte[] data){
		
		if(data!=null && data.length==2){
			//30->31
			for(int i=0; i<0; i++){
				msg[30+i]=data[i];
			}
		}
		
		return msg;
	}
	
	public byte[] packet_put_remoteAddress(byte msg[], byte[] address){
		
		for (int i = 0; i < 8; i++) {
			msg[i] = address[i];
		}
		
		return msg;
	}
	
	public byte[] packet_put_DessAssociation(byte[] msg, LinkedList<byte[]> newAddresses, int oldSize){
		int newSize = newAddresses.size();
		

		
		// A DEVICE WAS ADDED OR REMOVED
		if (newSize > oldSize || (newSize >= 0 && newSize < oldSize)) {
			//Indication
			msg[8]=1;
			//number of devices
			msg[11]=(byte) newSize;
			for (int i = 0; i < newSize; i++) {
				for (int j = 0; j < 8; j++) {
					msg[12 + i * 8 + j] = newAddresses.get(i)[j];
				}
			}
		}
		
		return msg;
	}
}
