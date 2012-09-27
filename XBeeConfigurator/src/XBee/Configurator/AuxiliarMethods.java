package XBee.Configurator;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AuxiliarMethods {
	
	private String debug="debug";

	/*
	 * 
	 * METHODS TO RETRIEVE THE DATA FROM PACKETS SENT BY THE ARDUINO
	 * 
	 */
	
	// METHOD TO CONVERT THE SENT DATA INTO STRING
	public String getData(byte[] buffer) {
		final StringBuilder textBuilder = new StringBuilder();
		int textLength = buffer[2];
		int textEndIndex = 3 + textLength;
		for (int x = 3; x < textEndIndex; x++) {

			textBuilder.append(Integer.toHexString(buffer[x] & 0xFF));

			if (buffer[x] == 0) {
				textBuilder.append('0');
			}

		}
		return textBuilder.toString().toUpperCase();
	}
	
	// METHOD TO RETRIEVE THE DEVICE TYPE FROM THE SENT DATA
	public String getDeviceType(byte[] buffer, Context c){
		final StringBuilder text=new StringBuilder();

		for(int i=13; i<18; i++){
			text.append((char) buffer[i]);
		}
		
		return this.getDeviceTypeToString(c,text.toString());
	}
	
	private String getDeviceTypeToString(Context c, String id){
		String type="";
		
		switch (Integer.parseInt(id)) {
		case 10000:
			type = c.getString(R.string.coordinator);
			break;
		case 20000:
			type = c.getString(R.string.router);
			break;
		case 20110:
			type= c.getString(R.string.routerMotionSensor);
			break;
		case 20120:
			type=c.getString(R.string.routerLuminanceSensor);
			break;
		case 30000:
			type = c.getString(R.string.sensor);
			break;
		case 30010:
			type=c.getString(R.string.motionSensor);
			break;
		case 30020:
			type=c.getString(R.string.luminanceSensor);
			break;
		case 40000:
			type=c.getString(R.string.actuator);
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
	 * 
	 */
	
	public int getDataInt(byte[] buffer) {

		int dataLength = buffer[2];
		int dataEndIndex = 3 + dataLength;

		int num = -1;

		int dataStart = 0;

		// LOOKS UP FOR THE DATA START INDEX
		for (int i = 3; i < dataEndIndex; i++) {
			if(buffer[i]!=0){
				dataStart=i;
				i=dataEndIndex;
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
	
	public byte[] intToByteArray(int n){
		byte b[]=new byte[4];
		for(int i=0; i<4; i++){
			b[4-1-i]=(byte) ((n >> 8*i) & 0xFF);
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

		s = s.replace(" ", "").trim();
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
		System.out.println(s.length());
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
	

}
