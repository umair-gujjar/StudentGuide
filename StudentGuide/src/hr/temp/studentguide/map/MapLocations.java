package hr.temp.studentguide.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.content.Context;

public class MapLocations {

	private static final String STOPS_FILE_NAME = "stops.json";
	private static final String ROUTES_FILE_NAME = "routes.json";
	private static final String CANTEENS_FILE_NAME = "canteens.txt";
	private static final String UNIVERSITIES_FILE_NAME = "universities.txt";
	public static final int TRAM_STOPS = 0;
	public static final int CANTEENS = 1;
	public static final int UNIVERSITIES = 2;
	
	public static List<Stop> getTramStops(Context context) {

		Map<Integer, Stop> stopList = new HashMap<>();
		List<String> lines = new ArrayList<>();
		List<Integer> addedStops = new ArrayList<>();

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(loadJSONFromAsset(context, STOPS_FILE_NAME));		
			JSONArray routes = (JSONArray) jsonParser.parse(loadJSONFromAsset(context, ROUTES_FILE_NAME));
			
			for(int i = 0; i < routes.size(); i++) {
				JSONObject route = (JSONObject) routes.get(i);
				String route_type = (String) route.get("route_type");
				if(route_type.equals("TRAM"))
				{
					JSONArray routeStops = (JSONArray) route.get("stops");		
					String routeShortName = (String) route.get("route_short_name");
					
					for(int j = 0; j < routeStops.size(); j++) {
						JSONObject jsonStop = (JSONObject) routeStops.get(j);
						int id =  ((Long) jsonStop.get("stop_id")).intValue();		
						String name = (String) jsonStop.get("stop_name");
						if(!addedStops.contains(id))
						{
							addedStops.add(id);
							Stop stop = new Stop(id, name);
							stop.addLine(routeShortName);
							stopList.put(id, stop);				
						}
						else
						{
							stopList.get(id).addLine(routeShortName);
						}
					}
					lines.add(routeShortName);					
				}					
			}
			
			// get an array from the JSON object
			JSONArray stopDetails = (JSONArray) jsonObject.get("stops");
			@SuppressWarnings("rawtypes")
			Iterator i = stopDetails.iterator();

			// take each value from the json array separately
			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				Stop stop = stopList.get(((Long) innerObj.get("stop_id")).intValue());
				if(stop != null)
				{
					stop.lat = (double) innerObj.get("stop_lat");
					stop.lon = (double) innerObj.get("stop_lon");
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		return new ArrayList<Stop>(stopList.values());
	}
	
	public static String loadJSONFromAsset(Context context, String fileName) {
	    String json = null;
	    try {

	        InputStream is = context.getAssets().open(fileName);
	        int size = is.available();
	        byte[] buffer = new byte[size];

	        is.read(buffer);
	        is.close();

	        json = new String(buffer, "UTF-8");

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return json;
	}
	
	public static List<MapLocation> getLocation(Context context, int type) {
		
		String fileName;
		if(type == CANTEENS)
			fileName = CANTEENS_FILE_NAME;
		else
			fileName = UNIVERSITIES_FILE_NAME;
		
		List<MapLocation> list = new ArrayList<>();
		BufferedReader reader = null;
		InputStream inputStream = null;
	    InputStreamReader inputStreamReader = null;
	    
		try {
			inputStream = context.getAssets().open(fileName);
			inputStreamReader = new InputStreamReader(inputStream);
		    reader = new BufferedReader(inputStreamReader);
			String line;
			String[] data;
			String[] coords;
		    while ((line = reader.readLine()) != null) {
		        data = line.split("-");
		        coords = data[2].split(",");
		        MapLocation location = new MapLocation(data[0], data[1], Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
		        list.add(location);
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		return list;
	}
	
	public static class Stop {
		private double lat, lon;
		private int id;
		private String name;
		private Set<Integer> lines;
		
		Stop(int id, String name) {
			this.id = id;
			this.name = name;
			lines = new TreeSet<>();
		}
		
		public int getId() {
			return id;
		}
		
		public double getLat() {
			return lat;
		}
		
		public double getLon() {
			return lon;
		}
		
		public String getName() {
			return name;
		}
		
		public Set<Integer> getLines() {
			return lines;
		}
		
		public void addLine(String routeShortName) {
			lines.add(Integer.parseInt(routeShortName));
		}
	}
	
	public static class MapLocation {
		private String name, address;
		private double lat, lon;
		
		MapLocation(String name, String address, double lat, double lon) {
			this.name = name;
			this.address = address;
			this.lat = lat;
			this.lon = lon;
		}
		
		public String getName() {
			return name;
		}
		
		public String getAddress() {
			return address;
		}
		
		public double getLat() {
			return lat;
		}
		
		public double getLon() {
			return lon;
		}
	}
}

