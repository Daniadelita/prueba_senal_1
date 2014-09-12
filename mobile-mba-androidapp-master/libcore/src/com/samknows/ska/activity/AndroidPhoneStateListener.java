package com.samknows.ska.activity;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.widget.Toast;

public class AndroidPhoneStateListener extends PhoneStateListener {
	
	 public static int signalStrengthValue;
	 Context context;
	 
	 AndroidPhoneStateListener(Context context){
		 this.context = context;
	 }

     @Override
     public void onSignalStrengthsChanged(SignalStrength signalStrength) {
         super.onSignalStrengthsChanged(signalStrength);
         if (signalStrength.isGsm()) {
             if (signalStrength.getGsmSignalStrength() != 99){
                 signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                 //Toast.makeText(context, "GSM: " + signalStrengthValue, Toast.LENGTH_SHORT).show();
                 //return signalStrength+"";
             }
             else{
                 signalStrengthValue = signalStrength.getGsmSignalStrength();
                 //Toast.makeText(context, "GSM: " + signalStrengthValue, Toast.LENGTH_SHORT).show();
                 //return signalStrength+"";
             }
         } else {
        	 //Toast.makeText(context, "Cdma: " + signalStrengthValue, Toast.LENGTH_SHORT).show();
             signalStrengthValue = signalStrength.getCdmaDbm();

         }
     }
     
     public String valor_potencia(){
    	 return signalStrengthValue+"";
     }

}
