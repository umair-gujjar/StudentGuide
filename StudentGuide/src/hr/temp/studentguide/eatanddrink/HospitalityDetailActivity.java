package hr.temp.studentguide.eatanddrink;

import com.androidquery.AQuery;

import hr.temp.studentguide.R;
import hr.temp.studentguide.map.MapActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HospitalityDetailActivity extends Activity {

	private LinearLayout descLayout;
	private ImageView image;
	private TextView name, desc, website;
	private HospitalityPlace place;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
    	setContentView(R.layout.activity_hospitality_detail);
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	
    	place = getIntent().getParcelableExtra("HOSPITALITY_PLACE");
    	
    	descLayout = (LinearLayout) findViewById(R.id.detaiLDescLayout);
    	descLayout.setPadding(5, 5, 5, 5);
        image = (ImageView) findViewById(R.id.detailImage);
		name = (TextView) findViewById(R.id.detailName);
		desc = (TextView) findViewById(R.id.detailDescText);
		website = (TextView) findViewById(R.id.detailWebsite);
						
		Bitmap preset = BitmapFactory.decodeResource(getResources(), R.drawable.photo_placeholder);
		
		//Check for network connection
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);	 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		//Android Query downloads an image asynchronously
		//and sets a placeholder image until it's downloaded
		AQuery aq = new AQuery(this);
		if(place.getBigImage() != null && isConnected)
			aq.id(image).image(place.getBigImage(), false, false, 0, 0, preset, AQuery.FADE_IN);
		else
			aq.id(image).image(preset);
		
		name.setText(place.getName());
		if(!place.getDesc().equals("-"))
			desc.setText(place.getDesc());
		if(!place.getWebsite().equals("-"))
			website.setText("Website: " + place.getWebsite().replace("http://", ""));
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hospitality_detail_actions, menu);
	
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_show_on_map:
	        	Intent intent = new Intent(this, MapActivity.class);
				intent.putExtra("HOSPITALITY_PLACE", place);
				startActivity(intent);
	            return true;
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
