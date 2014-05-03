package hr.temp.studentguide.map;

import hr.temp.studentguide.R;
import hr.temp.studentguide.database.Database;
import hr.temp.studentguide.eatanddrink.HospitalityDetailActivity;
import hr.temp.studentguide.eatanddrink.HospitalityPlace;
import hr.temp.studentguide.map.MapLocations.MapLocation;
import hr.temp.studentguide.map.MapLocations.Stop;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import org.apache.commons.lang3.StringUtils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MapActivity extends ActionBarActivity {

	private static Database database;
	private GoogleMap map;
	private MarkerOptions markerOptions;
    private LatLng latLng;
    private String[] spinnerText = { "Default", "Tram stops", "Universities", "Student canteens" };
    private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
  
        getActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        database = new Database(this);
        setUpMap();
    }
    
    private void setUpMap() {

        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
       
            if (map != null) {
            	Intent intent = getIntent();
            	if(intent != null) {
	            	if(intent.hasExtra("HOSPITALITY_PLACE")) {
		            	HospitalityPlace place = intent.getParcelableExtra("HOSPITALITY_PLACE");
		            	LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
		            	@SuppressWarnings("unused")
						Marker marker = map.addMarker(new MarkerOptions()
						.position(location)
						.title(place.getName())
						.snippet(place.getAddress()));
		            	map.animateCamera(CameraUpdateFactory.newLatLng(location));
	            	}
            	}
            }
        }
    } 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_activity_actions, menu);
        
        //Setup searchview
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = null;
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem); 
        
        if(searchView != null) {
        	searchView.setSearchableInfo(searchManager
        			.getSearchableInfo(getComponentName()));
        	searchView.setQueryHint("Find location");
        	searchView.setIconifiedByDefault(false);
        	
        	TextView textView = (TextView) searchView.findViewById(R.id.search_src_text);
        	textView.setTextColor(Color.BLACK);
        }
        
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String location) {
				
				if(location != null && !location.equals("")) {
					new GeocoderTask().execute(location);
				}
				
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {			
				
				return false;
			}
		};
		searchView.setOnQueryTextListener(queryTextListener);
		
		//Setup locations spinner
		MenuItem spinnerItem = menu.findItem(R.id.map_spinner);
		Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem).findViewById(R.id.spinner);
		spinner.setAdapter(new SpinnerAdapter(this, R.layout.map_spinner_row, spinnerText));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
	    {
	        @Override
	        public void onItemSelected(AdapterView<?> adapter, View view,
	                int position, long rowId) 
	        {
	        	MarkerLoader markerLoader = new MarkerLoader(context);
	        	if(position == 1) {  		
	        		markerLoader.execute(MapLocations.TRAM_STOPS);
	        	}
	        	else if(position == 2) {
	        		markerLoader.execute(MapLocations.UNIVERSITIES);
	        	}
	        	else if(position == 3) {
	        		markerLoader.execute(MapLocations.CANTEENS);
	        	}
	        }

	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) 
	        {
	        }

	    });
      
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
    }

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
	    	
	   	@Override
	    protected List<Address> doInBackground(String... locationName) {
	 
	        Geocoder geocoder = new Geocoder(getBaseContext());
	        List<Address> addresses = null;
	 
	        try {
	            // Getting a maximum of 3 Address that matches the input text
	            addresses = geocoder.getFromLocationName(locationName[0], 3);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	            
	            return addresses;
	    }
	    	
	    @Override
	    protected void onPostExecute(List<Address> addresses) {
	 
	        if(addresses==null || addresses.size()==0){
	            Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
	        }
	 
	        // Clears all the existing markers on the map
	        map.clear();
	 
	        // Adding Markers on Google Map for each matching address
	        for(int i=0;i<addresses.size();i++){
	 
	            Address address = (Address) addresses.get(i);
	 
	            // Creating an instance of GeoPoint, to display in Google Map
	            latLng = new LatLng(address.getLatitude(), address.getLongitude());
	 
	            String addressText = String.format("%s, %s",
	            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	            address.getCountryName());
	 
	            markerOptions = new MarkerOptions();
	            markerOptions.position(latLng);
	            markerOptions.title(addressText);
	 
	            map.addMarker(markerOptions);
	 
	            // Locate the first location
	            if(i==0)
	            	map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
	        }
	    }
	}
	
	private class MarkerLoader extends AsyncTask<Integer, Void, List<?>> {

		private int locationType;
		private Context context;
		
		public MarkerLoader(Context context) {
			this.context = context;
		}
		
		@Override
		protected List<?> doInBackground(Integer... params) {
			
			locationType = params[0];
			try {
				if(locationType == MapLocations.TRAM_STOPS)
				{
					return MapLocations.getTramStops(context);
				}
				else if(locationType == MapLocations.CANTEENS)
				{
					return MapLocations.getLocation(context, MapLocations.CANTEENS);
				}
				else return MapLocations.getLocation(context, MapLocations.UNIVERSITIES);
				}
			catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@SuppressWarnings({ "unchecked", "unused" })
		@Override
	    protected void onPostExecute(List<?> locations) {
			
			map.clear();
			
			if(locationType == MapLocations.TRAM_STOPS)
			{
				List<Stop> stopList = (List<Stop>) locations;
				for(Stop stop : stopList) {			
					Marker marker = map.addMarker(new MarkerOptions()
				     .position(new LatLng(stop.getLat(), stop.getLon()))
				     .title(stop.getName())
				     .snippet(StringUtils.join(stop.getLines(), ", ")));
				}
			}
			else 
			{
				List<MapLocation> locationList = (List<MapLocation>) locations;
				for(MapLocation location : locationList) {
					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(location.getLat(), location.getLon()))
					.title(location.getName())
					.snippet(location.getAddress()));
				}
			}
			
			if(locationType == MapLocations.CANTEENS) {
				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick(Marker marker) {
						HospitalityPlace place = database.dohvatiMenzu(marker.getPosition().latitude + "," + marker.getPosition().longitude);
						Intent intent = new Intent(context, HospitalityDetailActivity.class);
    					intent.putExtra("HOSPITALITY_PLACE", place);
    					startActivity(intent);
					}
				});
			}
		}
	}

	private class SpinnerAdapter extends ArrayAdapter<String> {
		
		int images[] = {R.drawable.ic_location_place, R.drawable.ic_location_tram, R.drawable.ic_location_university, R.drawable.ic_location_eat};
		
		public SpinnerAdapter(Context ctx, int txtViewResourceId, String[] objects) { 
			super(ctx, txtViewResourceId, objects); 
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) { 
			
			LayoutInflater inflater = getLayoutInflater(); 
			View spinner = inflater.inflate(R.layout.map_spinner_row, parent, false); 
			ImageView icon = (ImageView) spinner.findViewById(R.id.spinner_row_icon); 		
			icon.setImageResource(images[position]); 
			icon.setPadding(0, 0, 5, 0);
			
			return spinner;
		}

		@Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater(); 
			View spinner = inflater.inflate(R.layout.map_spinner_row, parent, false); 
			spinner.setBackgroundColor(Color.parseColor("#3C8FDD"));
			
			TextView text = (TextView) spinner.findViewById(R.id.spinner_row_text); 
			ImageView icon = (ImageView) spinner.findViewById(R.id.spinner_row_icon); 
			if(position == 0) {
				text.setHeight(0);
				text.setVisibility(View.GONE);
				icon.setVisibility(View.GONE);
			}
			else {
				text.setText(spinnerText[position]);							
				icon.setPadding(5, 5, 5, 5);
				icon.setImageResource(images[position]); 
			}
			
			return spinner; 
		}

	}
}