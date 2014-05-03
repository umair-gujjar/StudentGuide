package hr.temp.studentguide.eatanddrink;

import java.util.ArrayList;

import hr.temp.studentguide.R;
import hr.temp.studentguide.database.Database;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class EatAndDrinkActivity extends FragmentActivity implements ActionBar.TabListener, LocationListener {

	public static final String ACTIVITY_TYPE = "activity_type";
	public static final int EAT_ACTIVITY = 0;
	public static final int DRINK_ACTIVITY = 1;
	
    //The PagerAdapter that will provide fragments for each of the
    //three/four primary sections of the app.
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    boolean locationServicesEnabled;
    LocationManager locationManager;
    static Location currentLocation;
    String provider;
    static Database database;
    Context context; 
    
    //The ViewPager that will display the three primary sections of the app, one at a
    //time.
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_and_drink);

        context = this;
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), getIntent().getIntExtra(ACTIVITY_TYPE, DRINK_ACTIVITY));
        
        database = new Database(this);
        
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }   
        
        if(locationServicesEnabled())
        {
        	getCurrentLocation();
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume(); 
        if(locationManager != null && provider != null)
        	locationManager.requestLocationUpdates(provider, 600000, 100, this); 
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
       super.onPause();
       if(locationManager != null)		
    	   locationManager.removeUpdates(this);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.eat_and_drink_actions, menu);
		
		//Setup spinner
		MenuItem item = menu.findItem(R.id.action_list_filter);
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.distance_array,
		          android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) item.getActionView().findViewById(R.id.spinner);
		if(locationServicesEnabled() == false)
		{
			//spinner.getSelectedView().setEnabled(false);
			spinner.setEnabled(false);
		}
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
	    {
	        @Override
	        public void onItemSelected(AdapterView<?> adapter, View view,
	                int position, long rowId) 
	        {
	        	CharSequence distance = "0";
	        	if(position == 1)
	        		distance = "500";
	        	else if(position == 2)
	        		distance = "2000";
	        	else if(position == 3)
	        		distance = "5000";
	        	
	        	Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
	            if (currentFragment != null) {
	                ((HospitalityListFragment)currentFragment).adapter.getFilter().filter(distance);    
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
		switch(item.getItemId()) {
		case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) 	{
	}

	@Override
	public void onProviderDisabled(String provider) {	
 	}
    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());   
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
    //A FragmentPagerAdapter that returns a fragment corresponding to one of the primary
    //sections of the app.
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

    	int activityType;
    	
        public AppSectionsPagerAdapter(FragmentManager fm, int activityType) {
            super(fm);
            this.activityType = activityType;
        }

        @Override
        public Fragment getItem(int i) {
        	
        	Fragment fragment = new HospitalityListFragment();
        	Bundle args = new Bundle(); 
        	
        	if(activityType == DRINK_ACTIVITY)
        	{	
	           	args.putInt(HospitalityListFragment.ARG_FRAGMENT_TYPE, i + 1);
        	}
        	else
        	{
        		args.putInt(HospitalityListFragment.ARG_FRAGMENT_TYPE, i + 4);
        	}
        	
        	fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
        	
        	if(activityType == DRINK_ACTIVITY)
            	return 3;
        	
        	return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	
        	if(activityType == DRINK_ACTIVITY)
        	{
	            switch(position)
	            {
	            case 0:
	            	return "Night Clubs";
	            case 1:
	            	return "Bars";
	            default:
	            	return "Breweries";
	            }
        	}
        	else
        	{
        		switch(position)
	            {
        		case 0:
        			return "Student canteens";
	            case 1:
	            	return "Domestic restaurants";
	            case 2:
	            	return "Internation restaurants";
	            default:
	            	return "Pizza places";
	            }
        	}
        }
    }

    //A fragment which contains the list of places
    public static class HospitalityListFragment extends Fragment {
    	
    	public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    	public static final int TYPE_NIGHT_CLUBS = 1;
    	public static final int TYPE_BARS = 2;
    	public static final int TYPE_BREWERIES = 3;
    	public static final int TYPE_CANTEEN = 4;
    	public static final int TYPE_DOMESTIC_RESTAURANT = 5;
    	public static final int TYPE_INTERNATIONAL_RESTAURANT = 6;
    	public static final int TYPE_PIZZA_PLACE = 7;
 	
    	private ListView listView;
    	private ArrayList<HospitalityPlace> places;
    	private HospitalityPlaceAdapter adapter;
    	private int type;
    	
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);

    	    type = getArguments().getInt(ARG_FRAGMENT_TYPE);   	  
    	    places = database.dohvatiIzBaze(type);
    	}
    	
    	@Override
    	public void onAttach(Activity activity) {
    	    super.onAttach(activity);
    	}   	
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	View rootView = inflater.inflate(R.layout.fragment_hospitality_list, container, false);
            listView = (ListView) rootView.findViewById(R.id.places_list_view);
    		
            adapter = new HospitalityPlaceAdapter(getActivity(), places);
            adapter.setLocation(currentLocation);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
    		
    					HospitalityPlace place = (HospitalityPlace) ((HospitalityPlaceAdapter) parent.getAdapter()).getItem(position);
    					Intent intent = new Intent(getActivity(), HospitalityDetailActivity.class);
    					intent.putExtra("HOSPITALITY_PLACE", place);
    					startActivity(intent);
    			}
    		});
            
            return rootView;
        }
    }
    
    private void getCurrentLocation() {
    	
    	Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(provider);
   
        if(currentLocation != null) {
        	onLocationChanged(currentLocation);
        }
    }
    
    private boolean locationServicesEnabled() {
   
    	boolean gpsEnabled = false, networkEnabled = false;
    	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
   	  
	    try{
 	    	gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
 	    }
 	    catch(Exception ex){}
	    
 	    try{
 	    	networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 	    }
 	    catch(Exception ex){}

 	    if(!gpsEnabled && !networkEnabled){
 	        return false;
 	    }
 	    
 	    return true;
    }
}