package in.agilo.partner.runner.view;

/**
 * Created by Ujjwal on 4/5/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import in.agilo.partner.runner.R;
import in.agilo.partner.runner.model.Location;
import in.agilo.partner.runner.utils.PlaceDetailsJSONParser;
import in.agilo.partner.runner.utils.PlaceJSONParser;

/** Customizing AutoCompleteTextView to return Place Description
 * corresponding to the selected item
 */
public class LocationAutoCompleteTextView extends AutoCompleteTextView {

    /**
     * The variable used to get the location based on @Google Places Api
     */

    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;

    final int PLACES = 0;
    final int PLACES_DETAILS = 1;

    LocationAutoCompleteTextView self;

    public LatLng searchCoordinates;
    public String uaddress;
    public String uarea;
    public String ucity;
    public String ustate;

    private Activity activity;

    private int type;

    RotateLoading rlView;

    public Location userLocation = new Location();

    public LocationAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Activity a, int type, RotateLoading rlView){
        this.setThreshold(3);
        this.type = type;
        this.addTextChangedListener(new TextWatcher() {
            private Editable s;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString().replace(" ", "+"));

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown list
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                ListView lv = (ListView) arg0;

                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);

                InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }
        });

        this.activity = a;

        self = this;

        this.rlView = rlView;
    }

    /**
     * Returns the place description corresponding to the selected item
     */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }

    public void addAddress(final LocationAutoCompleteTextView etAddress, String area, String city, String state){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_address, null);

        final MaterialEditText tvAddress = (MaterialEditText) dView.findViewById(R.id.etAddress);
        final MaterialEditText tvArea = (MaterialEditText) dView.findViewById(R.id.etArea);
        final MaterialEditText tvCity = (MaterialEditText) dView.findViewById(R.id.etCity);
        final MaterialEditText tvState = (MaterialEditText) dView.findViewById(R.id.etState);

        tvArea.setText(area);
        tvCity.setText(city);
        tvState.setText(state);

        builder.setView(dView)
                // Add action buttons
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.cancel, null);


        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;

                if (TextUtils.isEmpty(tvArea.getText())) {
                    tvArea.setError("Cannot be Empty");
                    check = false;
                }

                if (TextUtils.isEmpty(tvCity.getText())) {
                    tvCity.setError("Cannot be Empty");
                    check = false;
                }

                if (TextUtils.isEmpty(tvState.getText())) {
                    tvState.setError("Cannot be Empty");
                    check = false;
                }

                if (check) {
                    //Todo set address in corresponding variable
                    uaddress = tvAddress.getText().toString();
                    userLocation.setAddress1(uaddress);
                    uarea = tvArea.getText().toString();
                    ucity = tvCity.getText().toString();
                    ustate = tvState.getText().toString();
                    userLocation.setAddress2(uarea + ", " + ucity + ", " + ustate);
                    userLocation.setLatitude((float) searchCoordinates.latitude);
                    userLocation.setLongitude((float) searchCoordinates.longitude);
                    dialog.dismiss();
                }

            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etAddress.setText("");
                dialog.dismiss();
            }
        });
    }

    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyBrl-RRzrwGisYJpFI2QhpcCeknXg_bSmw";

        // place to be be searched
        String input = "input=" + place;

        //place around where results to be shown
//        String location = "location="+Double.toString(currentCoordinate.latitude)+","+Double.toString(currentCoordinate.longitude);

        //biased result radius constraint
        String radius = "radius=50000";

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        //country for autocomplete results
        String components = "components=country:india";


        // Building the parameters to the web service
//        String parameters = input+"&"+radius+"&"+location+"&"+types+"&"+sensor+"&"+key;
        String parameters = input + "&" + radius + "&" + types + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyBrl-RRzrwGisYJpFI2QhpcCeknXg_bSmw";

        //place around where results to be shown
//        String location = "location="+Double.toString(currentCoordinate.latitude)+","+Double.toString(currentCoordinate.longitude);

        //biased result radius constraint
        String radius = "radius=50000";

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        //country for autocomplete results
        // String components = "components=country:in";

        // Output format
        String output = "json";

        // reference of place
        String reference = "reference=" + ref;

        // Building the parameters to the web service
//        String parameters = reference+"&"+radius+"&"+location+"&"+types+"&"+sensor+"&"+key;
        String parameters = reference + "&" + radius + "&" + types + "&" + sensor + "&" + key;

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected void onPreExecute(){
            rlView.start();
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            rlView.stop();

            switch (parserType) {
                case PLACES:
                    String[] from = new String[]{"description"};
                    int[] to = new int[]{android.R.id.text1};

                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(activity, result, R.layout.simple_spinner_dropdown_item, from, to);

                    // Setting the adapter
                    self.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    break;
                case PLACES_DETAILS:
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    searchCoordinates = new LatLng(Double.parseDouble(hm.get("lat")), Double.parseDouble(hm.get("lng")));
                    setParentCoordinates();
                    addAddress(LocationAutoCompleteTextView.this, hm.get("area"), hm.get("city"), hm.get("state"));

                    // Getting reference to the SupportMapFragment of the activity_main.xml
                    break;
            }
        }
    }

    private void setParentCoordinates(){
        switch(type){
//            case 0:
//                //Main Activity
//                ((Runner)activity).searchCoordinates = searchCoordinates;
//                break;
//            case 1:
//                ((RegisterActivityUser)activity).setUserLocation(userLocation);
//                break;
//            case 2:
//                //Register Lawyer
//                ((RegisterActivityLawyer)activity).setCoordinates(searchCoordinates);
//                ((RegisterActivityLawyer)activity).setUserLocation(userLocation);
//                break;
//            case 3:
//                //More Activity
//                break;

        }
    }
}
