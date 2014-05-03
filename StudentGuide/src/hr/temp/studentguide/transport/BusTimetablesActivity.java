package hr.temp.studentguide.transport;

import hr.temp.studentguide.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

public class BusTimetablesActivity extends Activity {

	TreeMap<String, ArrayList<Ruta>> novaMapa;
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    RetrieveBusTimetablesTask retrieveBusTimetablesTask;
    ArrayList<String> lista;  
    Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_timetables_activity);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		novaMapa = new TreeMap<String, ArrayList<Ruta>>();
		 // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
 
        // preparing list data
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		if(isConnected) {
	        retrieveBusTimetablesTask = new RetrieveBusTimetablesTask();
	        retrieveBusTimetablesTask.execute();
		}
		else {
			Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
		}
        
		// Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {
 
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
        });
 
        expListView.setOnChildClickListener(new OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(novaMapa.get(lista.get(groupPosition)).get(childPosition).url)));

				return true;
			}
		});
        
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

	@SuppressLint("DefaultLocale")
	public class RetrieveBusTimetablesTask extends AsyncTask<Void, Void, TreeMap<String, ArrayList<Ruta>>> {
		
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute(){ 
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading...");
			pDialog.show();    
		}
		
	    protected TreeMap<String, ArrayList<Ruta>> doInBackground(Void... urls) {
	    	
	    	Document doc;
	        try {
	            doc = Jsoup.connect("http://www.zet.hr/autobus/dnevni.aspx").timeout(60*1000).get();
				Elements naslov = doc.select("div#autobus li:not(:has(a[href]))");
				for (Element element : naslov) {
					
					Elements rute = doc.select("div#autobus a[href]:contains(" + element.text() + ")");
					
					ArrayList<Ruta> listaRuta = new ArrayList<Ruta>();
						
					for (Element element2 : rute) {
						if(!element2.text().contains("autobusnih linija terminala"))
							listaRuta.add(new Ruta(element2.text().toUpperCase(), element2.attr("href")));
					}
					if(element.text() != null && !listaRuta.isEmpty() && element.text().length() > 3)
						novaMapa.put(element.text(), listaRuta);
		
				}
				doc = Jsoup.connect("http://www.zet.hr/autobus/nocni.aspx").get();
				Elements nocni = doc.select("div#autobus a[href]");
				ArrayList<Ruta> listaNocnih = new ArrayList<Ruta>();
				for (Element element : nocni) {
					listaNocnih.add(new Ruta(element.text().toUpperCase(), element.attr("href")));
				}
				novaMapa.put("Night Lines", listaNocnih);
				
	            return novaMapa;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    protected void onPostExecute(TreeMap<String, ArrayList<Ruta>> result) {
	    	super.onPostExecute(result);
	    	pDialog.dismiss();
	    	
	    	if(result != null) {
		    	Set<String> set = result.keySet();
		    	lista = new ArrayList<String>();
		    	lista.addAll(set);
		    	listAdapter = new ExpandableListAdapter(BusTimetablesActivity.this, lista, result);
		        expListView.setAdapter(listAdapter);	
	    	}
	    	else {
	    		Toast.makeText(context, "Unable to retrieve timetables", Toast.LENGTH_SHORT).show();
	    	}
	    }

	}
}
