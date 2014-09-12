package com.samknows.ska.activity;

public class TipoRed {
	
	TipoRed(){
		
	}
	
	public String tipo_red( int tipo){ 
		switch (tipo){

		case 1:
			return "GRPS";
		
		
		case 2:
			return "EDGE";
		
		
		case 3:
			return "UMTS";
		
		
		case 4:
			return "CDMA";
		
	
		case 5:
			return "EVDO_0";
		
		
		case 6:
			return "EVDO_A";
		
		
		case 7:
			return "1xRTT";
		
		
		case 8:
			return "HSDPA";
		
		
		case 9:
			return "HSUPA";
		
		
		case 10:
			return "HSPA";
		
		
		case 11:
			return "iDen";
		
		
		case 12:
			return "EVDO_B";
		
		
		case 13:
			return "LTE";
		
		
		case 14:
			return "eHRPD";
		
		
		case 15:
			return "HSPA+";
		
		
		default:
			return "Desconocida";
		}
	}


}
