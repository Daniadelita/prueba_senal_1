package com.samknows.ska.activity.components;

import com.samknows.libcore.R;
import com.samknows.measurement.SKApplication;
import com.samknows.measurement.SKApplication.eNetworkTypeResults;
import com.samknows.measurement.activity.components.StatRecord;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StatView extends ScrollView {
	private Context ctx;
    public StatView(Context ctx) {
        super(ctx);
        this.ctx=ctx;
     
    }
    
    public void setData(StatRecord sr){
    	//setActiveNetworkType(sr.active_network_type);
    	//setTestsLocation(sr.tests_location);
    	setUploadLocation(sr.upload_location);
    	setUploadResult(sr.upload_result);
    	setDownloadLocation(sr.download_location);
    	setDownloadResult(sr.download_result);
    	setLatencyLocation(sr.latency_location);
    	setLatencyResult(sr.latency_result);
    	setPacketlossLocation(sr.packetloss_location);
    	setPacketlossResult(sr.packetloss_result);
    	setJitterLocation(sr.jitter_location);
    	setJitterResult(sr.jitter_result);
    	setTimestamp(sr.time_stamp);
    	
    	if (   (findViewById(R.id.download_archive_panel).getVisibility() == View.GONE)
    	    && (findViewById(R.id.upload_archive_panel).getVisibility() == View.GONE)
    	    && (findViewById(R.id.latency_archive_panel).getVisibility() == View.GONE)
    	    && (findViewById(R.id.packetloss_archive_panel).getVisibility() == View.GONE)
    	    && (findViewById(R.id.jitter_archive_panel).getVisibility() == View.GONE)
    	   )
    	{
    		// No results AT ALL to show!
    	    ((TextView)findViewById(R.id.download_label_text_view)).setText("No results");
    	    ((TextView)findViewById(R.id.download_result)).setText("No data found");
    	    findViewById(R.id.download_archive_panel).setVisibility(View.VISIBLE);
    	}


    }
    
    
    // active metrics setter

    public void setDownloadLocation(String text){
    	TextView tv = (TextView) findViewById(R.id.download_location);
		tv.setText(text);
    }
    public void setDownloadResult(String text){
    	TextView tv = (TextView) findViewById(R.id.download_result);
		tv.setText(text);
		if (text.contains("Fail")){
			tv.setTextColor(Color.RED);
		}
		
		if (text.equals("")){
			TableLayout tl = (TableLayout) findViewById(R.id.download_archive_panel);
			tl.setVisibility(View.GONE);
		}
		
    }
    
    public void setActiveNetworkType(String text){    	
    	if (text.equals("(Network)")) {
    		text = "(" + ctx.getString(R.string.wifi) + ")";
    	} else if (text.equals("Network")) {
    		text = ctx.getString(R.string.wifi);
    	}
    }
    

    
    public void setUploadLocation(String text){
    	TextView tv = (TextView) findViewById(R.id.upload_location);
		tv.setText(text);
    }
    public void setUploadResult(String text){
    	TextView tv = (TextView) findViewById(R.id.upload_result);
		tv.setText(text);
		if (text.contains("Fail")){
			tv.setTextColor(Color.RED);
		}
		if (text.equals("")) {
			TableLayout tl = (TableLayout) findViewById(R.id.upload_archive_panel);
			tl.setVisibility(View.GONE);
		}
    }
    public void setLatencyLocation(String text){
    	TextView tv = (TextView) findViewById(R.id.latency_location);
		tv.setText(text);
    }
    public void setLatencyResult(String text){
    	TextView tv = (TextView) findViewById(R.id.latency_result);
		tv.setText(text);
		if (text.contains("Fail")){
			tv.setTextColor(Color.RED);
		}
		if (text.equals("")) {
			TableLayout tl = (TableLayout) findViewById(R.id.latency_archive_panel);
			tl.setVisibility(View.GONE);
		}
    }
    public void setPacketlossLocation(String text){
    	TextView tv = (TextView) findViewById(R.id.packetloss_location);
		tv.setText(text);
    }
    public void setPacketlossResult(String text){
    	TextView tv = (TextView) findViewById(R.id.packetloss_result);
		tv.setText(text);
		if (text.contains("Fail")){
			tv.setTextColor(Color.RED);
		}
		if (text.equals("")) {
			TableLayout tl = (TableLayout) findViewById(R.id.packetloss_archive_panel);
			tl.setVisibility(View.GONE);
		}
    }
    public void setJitterLocation(String text){
    	TextView tv = (TextView) findViewById(R.id.jitter_location);
		tv.setText(text);
    }
    public void setJitterResult(String text){
    	TextView tv = (TextView) findViewById(R.id.jitter_result);
		tv.setText(text);
		if (text.contains("Fail")){
			tv.setTextColor(Color.RED);
		}
		if (text.equals("")) {
			TableLayout tl = (TableLayout) findViewById(R.id.jitter_archive_panel);
			tl.setVisibility(View.GONE);
		}
    }
 
    public void setTimestamp(String text){
    	//TextView tv = (TextView) findViewById(R.id.timestamp);
		//tv.setText(text);
    }
    
public void setPassiveMetric(int table_id,int text_view,String text, String type){
		
		if (type.equals("boolean")){
			ImageView iv = new ImageView(ctx);
			iv.setImageResource(R.drawable.check);
			iv.setPadding(5,5,5,5);
			TableRow tr = (TableRow) findViewById(table_id);
			tr.addView(iv);
		}
		else
		{
	    	TextView tv = (TextView) findViewById(text_view);
			tv.setText(text);
			
			if (text.equals("")){
				
				TableLayout tl=(TableLayout) tv.getParent().getParent();
				tl.setVisibility(View.GONE);
			}
		}
    }
    
    public void setRightPageIndicator(boolean setting)
    {

    }
    

}
