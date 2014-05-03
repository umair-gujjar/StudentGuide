package hr.temp.studentguide.main;

import hr.temp.studentguide.R;
import hr.temp.studentguide.eatanddrink.EatAndDrinkActivity;
import hr.temp.studentguide.map.MapActivity;
import hr.temp.studentguide.transport.BusTimetablesActivity;

import com.androidquery.util.AQUtility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
    }
    
    @Override
	protected void onDestroy(){      
	        super.onDestroy();
	        
	    //clean the file cache when root activity exit
	    //the resulting total cache size will be less than 3M   
	    if(isTaskRoot()){
	        AQUtility.cleanCacheAsync(this);
	    }
	}

    public void gotoMap(View v)
    {
    	Intent intent = new Intent(this, MapActivity.class);
    	startActivity(intent);
    }
    
    public void gotoTransport(View v)
    {
    	Intent intent = new Intent(this, BusTimetablesActivity.class);
    	startActivity(intent);
    }
    
    public void gotoEat(View v)
    {
    	Intent intent = new Intent(this, EatAndDrinkActivity.class);
    	intent.putExtra(EatAndDrinkActivity.ACTIVITY_TYPE, EatAndDrinkActivity.EAT_ACTIVITY);
    	startActivity(intent);
    }
    
    public void gotoDrink(View v)
    {
    	Intent intent = new Intent(this, EatAndDrinkActivity.class);
    	intent.putExtra(EatAndDrinkActivity.ACTIVITY_TYPE, EatAndDrinkActivity.DRINK_ACTIVITY);
    	startActivity(intent);
    }
    
}