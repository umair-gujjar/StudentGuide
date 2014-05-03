package hr.temp.studentguide.eatanddrink;

import hr.temp.studentguide.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.androidquery.AQuery;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class HospitalityPlaceAdapter extends ArrayAdapter<HospitalityPlace> implements Filterable {

	private ArrayList<HospitalityPlace> placeList, backupPlaceList;
	private LayoutInflater inflater;
	private Location currentLocation;
	
	public HospitalityPlaceAdapter(Context context, ArrayList<HospitalityPlace> placeList) {
		super(context, 0, placeList);
		
		this.placeList = placeList;
		backupPlaceList = placeList;
		this.inflater = LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.hospitality_list_item, null);
			
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.list_image);
			holder.placeName = (TextView) convertView.findViewById(R.id.tvName);
			holder.placeAddress = (TextView) convertView.findViewById(R.id.tvAddress);
			holder.placeDistance = (TextView) convertView.findViewById(R.id.tvDistance);
			
			convertView.setTag(holder);
		}
				
		holder = (ViewHolder)convertView.getTag();
		final HospitalityPlace place = placeList.get(position);
		
			
		Bitmap placeholder = BitmapFactory.decodeResource(inflater.getContext().getResources(), R.drawable.photo_placeholder);
		String imgUrl = place.getSmallImage();
		
		//Check for network connection
		ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		//Android Query downloads an image asynchronously
		//and sets a placeholder image until it's downloaded
		AQuery aq = new AQuery(convertView);
		if(imgUrl != null && isConnected)
		{
            aq.id(holder.thumbnail).image(imgUrl, true, true, 0, 0, placeholder, 0, 0.75f);
		}
		else
		{				
			aq.id(holder.thumbnail).image(placeholder);
		}
		
		holder.placeName.setText(place.getName());
		holder.placeAddress.setText(place.getAddress());
		
		if(currentLocation != null) {
			Location placeLocation = new Location(place.getName());
			placeLocation.setLatitude(place.getLatitude());
			placeLocation.setLongitude(place.getLongitude());	
			double distance = currentLocation.distanceTo(placeLocation) / 1000;
			
			holder.placeDistance.setText("Distance: " + new DecimalFormat("#0.0").format(distance) + " km");
		}
		else holder.placeAddress.setText("Distance: N/A");
		
		return convertView;
	}
	
	@Override
	public HospitalityPlace getItem(int position) {
	    return placeList.get(position);
	}
	
	private static class ViewHolder {
		public ImageView thumbnail;
		public TextView placeName;
		public TextView placeAddress;
		public TextView placeDistance;
	}
	
	public void setLocation(Location location) {
		this.currentLocation = location;
	}
	
	@Override
	public int getCount() {
	    return placeList.size();
	}
	
	//Filters the list by distance
	@Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<HospitalityPlace> FilteredPlaces = new ArrayList<HospitalityPlace>();             
                
	            String strConstraint = constraint.toString();
	            if(!strConstraint.equals("0"))
	            {
	                Double dist = Double.parseDouble(strConstraint);
	                for(HospitalityPlace place : backupPlaceList) {      
	                	
	                	Location placeLocation = new Location("loc");
	        			placeLocation.setLatitude(place.getLatitude());
	        			placeLocation.setLongitude(place.getLongitude());	
	        			double distance = currentLocation.distanceTo(placeLocation);
	        			
	        			if(distance < dist)
	        				FilteredPlaces.add(place);
	                }
	            }
	            else 
	            {
	            	FilteredPlaces = backupPlaceList;
	            }
	            
                results.count = FilteredPlaces.size();
                results.values = FilteredPlaces;

                return results;
            }
            
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                	placeList = (ArrayList<HospitalityPlace>) results.values;
                    notifyDataSetChanged();
            }
        };

        return filter;
    }
}