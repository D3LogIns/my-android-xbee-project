package XBee.Configurator;

public class Languages {

		public Languages(String lang){
			
			if(lang.equals("en")){
				
			}else if(lang.equals("pt")){
				
			}
			
		}
		
		
		
		public Languages() {
		
		}
		
		public String getPreferences(String language){
			if(language.equals("PT"))
				return "Defini��es";
			else
				return "Preferences";
		}


		public String getXbeeDetails_MyAdrees_PT(){
			return "O meu endere�o";
		}
		
		public String getXBeeDetails_MyType_PT(){
			return "O meu tipo";
		}
		
		public String getMessageAlert_DeviceNotsDetected_PT(){
			return "N�o foram encontrados dispositivos";
		}
		
		public String getMessageAlert_CoordinatorNotFound_PT(){
			return "O Coordenador n�o foi encontrado!\n A aplica��o ser� terminada";
		}
		
		public String getMessageAlert_PanIdNotAcceptable_PT(){
			return "PAN ID inv�lido, Os valores t�m de estar entre 1 e 50000.";
		}
		
		public String getMessageAlert_ValueTooHigh(String language){
			if (language.equals("PT"))
				return "O valor � demasiado alto, insira valores menores";
			else
				return "Value is too high, please insert lower values.";
			
		}
		
}
