package hr.temp.studentguide.eatanddrink;

import android.os.Parcel;
import android.os.Parcelable;

public class HospitalityPlace implements Parcelable {

	private String name;
	private String address;
	private double lat, lng;
	private String website;
	private String desc;
	private String smallImage;
	private String bigImage;
	
	public HospitalityPlace(){};
	
	public HospitalityPlace(String name, String address, double lat, double lng,
			String website, String desc, String smallImage, String bigImage) {

		this.name = name;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.website = website;
		this.desc = desc;
		this.smallImage = smallImage;
		this.bigImage = bigImage;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
	
	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lng;
	}

	public String getWebsite() {
		return website;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getSmallImage() {
		return smallImage;
	}
	
	public String getBigImage() {
		return bigImage;
	}
	
	public static final Parcelable.Creator<HospitalityPlace> CREATOR = new Creator<HospitalityPlace>() { 
		public HospitalityPlace createFromParcel(Parcel source) { 
			android.os.Debug.waitForDebugger();
			HospitalityPlace place = new HospitalityPlace(); 
		    place.name = source.readString(); 
		    place.address = source.readString(); 
		    place.lat = source.readDouble();
		    place.lng = source.readDouble();
		    place.website = source.readString();
		    place.desc = source.readString();
		    place.smallImage = source.readString();
		    place.bigImage = source.readString();
		      
		    return place; 
	    } 
		
		public HospitalityPlace[] newArray(int size) { 
			 return new HospitalityPlace[size]; 
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(address);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeString(website);
		dest.writeString(desc);
		dest.writeString(smallImage);
		dest.writeString(bigImage);
	}
}
