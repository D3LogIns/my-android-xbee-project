package XBee.Configurator;


public class AuxiliarMethods {
	
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

	public String convertByteToString(byte[] b){
		
		StringBuilder textBuilder = new StringBuilder();
		
		int middle=(b.length/2)-1; 
		
		for(int i=0; i<b.length; i++){
			textBuilder.append(Integer.toHexString(b[i] & 0xFF));
			if(i==middle)
				textBuilder.append(" ");
		}
		return textBuilder.toString().toUpperCase();
	}
	
	

}
