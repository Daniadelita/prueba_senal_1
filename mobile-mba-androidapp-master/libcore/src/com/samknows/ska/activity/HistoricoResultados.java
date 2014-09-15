package com.samknows.ska.activity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import com.samknows.libcore.R;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistoricoResultados extends Activity {
	
	TableLayout tabla;
	TableLayout cabecera;
	TableRow.LayoutParams layoutFila;
	TableRow.LayoutParams layoutId;
	TableRow.LayoutParams layoutTexto;
	String [][] cadenota;	
	int y=0;
	List<String> lista = new ArrayList<String>();
	String strLinea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.activity_historico_resultados);
		tabla = (TableLayout) findViewById(R.id.tablitas1);
		layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
		layoutId = new TableRow.LayoutParams(70,TableRow.LayoutParams.WRAP_CONTENT);
		layoutTexto = new TableRow.LayoutParams(160,TableRow.LayoutParams.WRAP_CONTENT);
			
		TableRow fila;
		
		
		
		TextView fecha;
		TextView descarga;
		TextView carga;
		TextView potencia;
		
		try
		{
		    File ruta_sd = Environment.getExternalStorageDirectory();
		    FileInputStream fstream = new FileInputStream(ruta_sd.getAbsolutePath()+ "/registro_datos.txt");
            DataInputStream entrada = new DataInputStream(fstream);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
            
            while ((strLinea = buffer.readLine()) != null)   {            	            	             	 
            	lista.add(strLinea);
            	y++;
            }
            
            cadenota = new String [y][6];
            y=0;
            
            for(String cadena:lista){
            	
    			cadenota[y]=cadena.split(",");    			
    			System.out.println("y[" + y + "][0]: " + cadenota [y][0] );
    			System.out.println("y[" + y + "][1]: " + cadenota [y][1] );
    			System.out.println("y[" + y + "][2]: " + cadenota [y][2] );
    			System.out.println("y[" + y + "][3]: " + cadenota [y][3] );
    			System.out.println("y[" + y + "][4]: " + cadenota [y][4] ); 
    			
    			y++;
    		}
            System.out.println ("y: " + y);
            entrada.close();
            TableRow.LayoutParams layoutId = new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,1);
            
            for (int x=0; x<y;x++){
            	if(cadenota[x][5].equalsIgnoreCase("1")){
            		fila = new TableRow(this);
            	//	fila.setLayoutParams(layoutFila);
            		
            		fecha = new TextView (this);
            		fecha.setCompoundDrawablesWithIntrinsicBounds(R.drawable.derecha, 0, 0, 0);
            		fecha.setLayoutParams(layoutId);
            		fecha.setGravity(Gravity.CENTER);
            		fecha.setTextSize(13);
            	//	fecha.setPadding(10, 0, 0, 0);
            		
            		
            		descarga = new TextView (this);
            		descarga.setLayoutParams(layoutId);
            		descarga.setGravity(Gravity.CENTER);
            		descarga.setTextSize(13);
        		//	descarga.setPadding(10, 0, 0, 0);
            		
            		
            		carga = new TextView (this);
            		carga.setLayoutParams(layoutId);
            		carga.setGravity(Gravity.CENTER);
            		carga.setTextSize(13);
        		//	carga.setPadding(10, 0, 0, 0);
            		
            		
            		potencia = new TextView (this);
            		potencia.setLayoutParams(layoutId);
            		potencia.setGravity(Gravity.CENTER);
            		potencia.setTextSize(13);
        		//	potencia.setPadding(10, 0, 0, 0);
            		
            		
            		fecha.setText(cadenota[x][0] + "\n" + cadenota [x][1]);
            		descarga.setText(cadenota[x][2]);
            		carga.setText(cadenota[x][3]); 
            		potencia.setText(cadenota[x][4] + " dBm");
            		
            		if((x%2==0))
            			fila.setBackgroundColor(Color.parseColor("#999999"));
            		else
            			fila.setBackgroundColor(Color.parseColor("#919191"));
            		fila.addView(fecha);
            		fila.addView(descarga);
            		fila.addView(carga);
            		fila.addView(potencia);
            		
            	
            		tabla.addView(fila);
            	}
            	else{
            		
            	}
            }
            
            
            
            
            
        }catch (Exception e){ //Catch de excepciones
            System.err.println("Ocurrio un error: " + e.getMessage());
        }
		
	}


}
