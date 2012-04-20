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
				return "Definições";
			else
				return "Preferences";
		}


		public String getXbeeDetails_MyAdrees_PT(){
			return "O meu endereço";
		}
		
		public String getXBeeDetails_MyType_PT(){
			return "O meu tipo";
		}
		
		public String getMessageAlert_DeviceNotsDetected_PT(){
			return "Não foram encontrados dispositivos";
		}
		
		public String getMessageAlert_CoordinatorNotFound_PT(){
			return "O Coordenador não foi encontrado!\n A aplicação será terminada";
		}
		
		public String getMessageAlert_PanIdNotAcceptable_PT(){
			return "PAN ID inválido, Os valores têm de estar entre 1 e 50000.";
		}
		
		public String getMessageAlert_ValueTooHigh(String language){
			if (language.equals("PT"))
				return "O valor é demasiado alto, insira valores menores";
			else
				return "Value is too high, please insert lower values.";
			
		}
		
}
