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
			// textBuilder.append(" ");
			// textBuilder.append((char) buffer[x]);
		}
		return textBuilder.toString();
		//return checkStringBuilder(textBuilder);
	}

	private String checkStringBuilder(StringBuilder textBuilder) {
		// TODO Auto-generated method stub

		int cont = 0;
		for (int i = 0; i < textBuilder.length(); i++) {
			if (textBuilder.charAt(i) == 'f')
				cont++;
			else
				cont = 0;

			if (cont == 6)
				for (int j = i; j > i - cont; j--)
					textBuilder.setCharAt(j, ' ');

		}

		return textBuilder.toString().replaceAll(" ", "")
				.replaceAll("ffffff", "");
	}

}
