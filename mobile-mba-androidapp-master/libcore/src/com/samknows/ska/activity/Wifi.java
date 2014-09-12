package com.samknows.ska.activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class Wifi {
	Context contextowifi;
	WifiManager wifi;
	
	Wifi(Context contexto1){
		contextowifi= contexto1; //envio el contexto desde el main activity
		wifi = (WifiManager) contextowifi.getSystemService(Context.WIFI_SERVICE); //inicializo wifi
	}
	
	public String estado_wifi(){		
		try{
			
			WifiInfo info = wifi.getConnectionInfo();
			String address = info.getMacAddress();
			return address;
		}
		catch(Exception e){
			e.printStackTrace();
			Toast.makeText(contextowifi, "No existe conexcion WiFi",Toast.LENGTH_LONG).show();
			return "vacio";
		}		
	}
}
