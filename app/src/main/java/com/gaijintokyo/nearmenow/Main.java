package com.gaijintokyo.nearmenow;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class Main extends ActionBarActivity implements View.OnClickListener {


    HttpClient mClient;
    JSONObject json;
    ListView mListView;
    TextView tvLong, tvLat;
    ArrayList<String> mVenueList;
    ArrayList<Venue> mVenueList2;

    final static String URL = "https://api.foursquare.com/v2/venues/explore?";
    final static String CLIENT_ID = "4K213ITDZ20HNGIZDOR2GMO1IC20OV0DRIOY5KO41MVE3KGP";
    final static String CLIENT_SECRET = "4HWMRXRRLDPSCNCC0NCTKSK5TWGFJGGMPDDXJS2HDEJW12OX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button bUpdate = (Button)findViewById(R.id.bGet);
        bUpdate.setOnClickListener(this);
        mClient = new DefaultHttpClient();
        mListView = (ListView)findViewById(R.id.lvVenues);
        tvLong = (TextView)findViewById(R.id.tvLongtitude);
        tvLat = (TextView)findViewById(R.id.tvLatitude);
        new Load().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        mListView.setAdapter(null);
        new Load().execute();
    }

    public void exploreFoursquare() throws IOException, JSONException {
        StringBuilder url = new StringBuilder(URL);
        url.append("client_id=" + CLIENT_ID);
        url.append("&client_secret=" + CLIENT_SECRET);
        url.append("&v=20141219");
        url.append("&section=food");
        url.append("&ll=35.631129,139.744099");

        HttpGet get = new HttpGet(url.toString());
        HttpResponse r = mClient.execute(get);
        int status = r.getStatusLine().getStatusCode();
        if (status == 200) {
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONObject jsonObj = new JSONObject(data);
            JSONArray groups    = jsonObj.getJSONObject("response").getJSONArray("groups");
            JSONObject objGroup = groups.getJSONObject(0);
            JSONArray items = objGroup.getJSONArray("items");

            mVenueList = new ArrayList<String>(items.length());
            mVenueList2 = new ArrayList<Venue>(items.length());
            for (int i = 0; i < items.length(); i++){
                Venue venue = new Venue();
                JSONObject objItem = items.getJSONObject(i);
                JSONObject objVenue = objItem.getJSONObject("venue");
                mVenueList.add(i, objVenue.getString("name"));
                venue.mName = objVenue.getString("name");
                //rating not available for some venues
                if (objVenue.has("rating")) {
                    venue.mRating = venue.mRating + objVenue.getString("rating");
                }
                venue.mCheckins = venue.mCheckins + objVenue.getJSONObject("stats").getString("checkinsCount");
                mVenueList2.add(i, venue);
            }

        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT);
        }
        return;
    }

    public class Load extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                exploreFoursquare();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this, android.R.layout.simple_list_item_1, mVenueList);
            VenueAdapter vAdapter = new VenueAdapter(Main.this, mVenueList2);
            //mListView.setAdapter(adapter);
            mListView.setAdapter(vAdapter);
        }
    }
}
