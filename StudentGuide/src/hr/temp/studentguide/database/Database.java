package hr.temp.studentguide.database;

import java.util.ArrayList;

import hr.temp.studentguide.eatanddrink.HospitalityPlace;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

@SuppressWarnings("unused")
public class Database extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "baza.db";
    private static final int DATABASE_VERSION = 1;
    private static final int COLUMN_KATEGORIJA = 0;
    private static final int COLUMN_NAZIV = 1;
    private static final int COLUMN_ADRESA = 2;
    private static final int COLUMN_KOORDINATE = 3;
    private static final int COLUMN_WEB = 4;
    private static final int COLUMN_OPIS = 5;
    private static final int COLUMN_NAZIV_SLIKE = 6;
    private static final String KATEGORIJA = "kategorija";
    private static final String NAZIV = "naziv";
    private static final String ADRESA = "adresa";
    private static final String KOORDINATE = "koordinate";
    private static final String WEB = "web";
    private static final String OPIS = "opis";
    private static final String URL_SLIKE = "url_slike";
    
    private static Database database;
	
    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	public ArrayList<HospitalityPlace> dohvatiIzBaze(int kategorija) {
    	
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		String [] projection = {NAZIV, ADRESA, KOORDINATE, WEB, OPIS, URL_SLIKE}; 
		String selection = KATEGORIJA + " = " + kategorija;
		String sqlTables = "lokali";

		qb.setTables(sqlTables);
		Cursor result = qb.query(db, projection, selection, null, null, null, null);
		
		ArrayList<HospitalityPlace> lokali = new ArrayList<>();
		String naziv, adresa, koordinate, web, opis, urlSlika;
		String[] latlng;
		String[] slike = new String[2];
		double lat, lng;
		
		for(result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {

			naziv = result.getString(0);
			adresa = result.getString(1);
			koordinate = result.getString(2);
			web = result.getString(3);
			opis = result.getString(4);
			opis = opis.replace(";", "\n");
			urlSlika = result.getString(5);		
			if(!urlSlika.equals("-"))
				slike = urlSlika.split(";");
			latlng = koordinate.split(",");
			lat = Double.parseDouble(latlng[0]);
			lng = Double.parseDouble(latlng[1]);
			
			lokali.add(new HospitalityPlace(naziv, adresa, lat, lng, web, opis, slike[0], slike[1]));
		}
		
		return lokali;
    }
	
	public HospitalityPlace dohvatiMenzu(String coords) {
		
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		String [] projection = {NAZIV, ADRESA, KOORDINATE, WEB, OPIS, URL_SLIKE}; 
		String selection = "koordinate LIKE '" + coords.substring(0, 8) + "%'";
		String sqlTables = "lokali";

		qb.setTables(sqlTables);
		Cursor result = qb.query(db, projection, selection, null, null, null, null);
		
		String naziv, adresa, koordinate, web, opis, urlSlika;
		String[] latlng;
		String[] slike = new String[2];
		double lat, lng;
		
		result.moveToFirst();

		naziv = result.getString(0);
		adresa = result.getString(1);
		koordinate = result.getString(2);
		web = result.getString(3);
		opis = result.getString(4);
		opis = opis.replace(";", "\n");
		urlSlika = result.getString(5);		
		if(!urlSlika.equals("-"))
			slike = urlSlika.split(";");
		latlng = koordinate.split(",");
		lat = Double.parseDouble(latlng[0]);
		lng = Double.parseDouble(latlng[1]);
			
		return new HospitalityPlace(naziv, adresa, lat, lng, web, opis, slike[0], slike[1]);
	}
}
