package in.agilo.partner.runner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.agilo.partner.runner.activities.SingleOrder;
import in.agilo.partner.runner.adapter.ItemsAdapter;
import in.agilo.partner.runner.adapter.MyItemTouchHelperCallback;
import in.agilo.partner.runner.interfaces.CallbackItemTouch;
import in.agilo.partner.runner.model.AppOrder;
import in.agilo.partner.runner.model.ItemRequest;
import in.agilo.partner.runner.model.Order;
import in.agilo.partner.runner.utils.StringRequest;

public class MainActivity extends ActionBarActivity implements CallbackItemTouch, ItemsAdapter.OnClickItemListener {

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.progressView)
    RotateLoading progressView;
    @InjectView(R.id.tvMessage)
    public TextView tvMessage;
    /**
     * Variable to click photo
     */
    TransferUtility transferUtility;
    AmazonS3 s3;
    private ItemsAdapter itemsAdapter; //The Adapter for RecyclerVIew

    Drawer mDrawer;
    PrimaryDrawerItem mItemRequests;

    android.support.v7.app.ActionBar actionBar;


    public List<Order> ordersList = new ArrayList<Order>();
    public List<AppOrder> appOrders = new ArrayList<AppOrder>();

    DatabaseHelper db;

    private int pageSP = 0;
    private int pageOP = 0;
    private int pageRSC = 0;

    private ActionBarDrawerToggle mDrawerToggle;

    public static void startMainActivity(final Activity startingActivity, final RotateLoading pv) {
        if(Constants.DEBUG)
        Log.d("MainActivity", "Request Orders");
        pv.start();
        String url = "http://partner.agilo.in/runner/rapi/orders/OP/id/0/8";
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pv.stop();
                if(Constants.DEBUG)
                    System.out.println(response);
                startMainActivityPhase2(startingActivity, pv, response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pv.stop();
                startMainActivityPhase2(startingActivity, pv, "");
                if (error != null) {
                    Log.e("EXCEPTION", error.toString());
                }
                Log.e("EXCEPTION", "Exception occured in volley");
                error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                "",
                listener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Constants.username+":"+Constants.password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

       RunnerApplication.getInstance().getRequestQueue().add(request);
    }

    public static void startMainActivityPhase2(final Activity startingActivity, final RotateLoading pv, final String orders) {
        if(Constants.DEBUG)
            Log.d("MainActivity", "Request Orders");
        pv.start();
        String url = "http://partner.agilo.in/runner/rapi/orders/RSC/id/0/8";
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pv.stop();
                if(Constants.DEBUG)
                    System.out.println(response);
                Intent intent = new Intent(startingActivity, MainActivity.class);
                intent.putExtra(Constants.ORDERS_OP, orders);
                intent.putExtra(Constants.ORDERS_RSC, response);
                intent.putExtra("offline", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startingActivity.startActivity(intent);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pv.stop();
                Intent intent = new Intent(startingActivity, MainActivity.class);
                intent.putExtra(Constants.ORDERS_OP, orders);
                intent.putExtra(Constants.ORDERS_RSC, "");
                if(orders.isEmpty())
                    intent.putExtra("offline", true);
                else
                    intent.putExtra("offline", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startingActivity.startActivity(intent);
                if (error != null) {
                    Log.e("EXCEPTION", error.toString());
                }
                Log.e("EXCEPTION", "Exception occured in volley");
                error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                "",
                listener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Constants.username+":"+Constants.password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RunnerApplication.getInstance().getRequestQueue().add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        actionBar = getSupportActionBar();


        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bcf6d93f-864c-4adc-b802-779c4f6a53ee", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        db = new DatabaseHelper(this);
        appOrders = db.getAllOrders();

        int requestCount = db.getAllRequests().size();

        setupDrawer(requestCount);

        if(Constants.DEBUG)
            System.out.println(appOrders.size());


        Intent i = getIntent();
        String data = i.getStringExtra(Constants.ORDERS_OP);
        if(!TextUtils.isEmpty(data)){
            try {
                JSONArray orders = new JSONArray(data);
                if(orders.length() == 8)
                    pageOP++;
                parseData(orders);
            } catch (JSONException e) {
                if(Constants.DEBUG)
                e.printStackTrace();
            } catch (IOException e) {
                if(Constants.DEBUG)
                e.printStackTrace();
            }
        }

        data = i.getStringExtra(Constants.ORDERS_RSC);
        if(!TextUtils.isEmpty(data)){
            try {
                JSONArray orders = new JSONArray(data);
                if(orders.length() == 8)
                    pageRSC++;
                parseData(orders);
            } catch (JSONException e) {
                if(Constants.DEBUG)
                    e.printStackTrace();
            } catch (IOException e) {
                if(Constants.DEBUG)
                    e.printStackTrace();
            }
        }


        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());
        initList(); //call method
    }

    @Override
    protected void onResume() {
        int count = db.getAllRequests().size();
        mItemRequests.withBadge(String.valueOf(count));
        mDrawer.updateItem(mItemRequests);
        appOrders = db.getAllOrders();
        itemsAdapter.updateItems(appOrders.size());
        if(appOrders.size()<=0)
            tvMessage.setVisibility(View.VISIBLE);
        else
            tvMessage.setVisibility(View.GONE);
        super.onResume();
    }

    private void setupDrawer(int count) {
        //if you want to update the items at a later time it is recommended to keep it in a variable

        mItemRequests = new PrimaryDrawerItem().withName("Pending Requests").withBadge(String.valueOf(count)).withIdentifier(1);

//create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new SectionDrawerItem(),
                        new SectionDrawerItem(),
                        mItemRequests
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if(!isConnectingToInternet()) {
                            alertMessage("No Connected to Internet");
                            return true;
                        }
                        List<ItemRequest> requests = db.getAllRequests();
                        mItemRequests.withBadge(String.valueOf(requests.size()));
                        mDrawer.updateItem(mItemRequests);
                        if(Constants.DEBUG)
                            System.out.println(""+requests.size());

                        for(int i = 0; i < requests.size(); i++){
                            ItemRequest request = requests.get(i);
                            switch (request.getType()){
                                case 0:
                                    //// TODO: 4/12/2016 check if uploads for image and barcode done
                                    if(request.isUploads()){
                                        if(request.getBarcode().equals("")){
                                            RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/updateProductInfo/" + request.getOrderID(), request.getOrderID(), request.getId(), request.getBody(), 1);
                                        }else{
                                            RunnerApplication.postBarcode(request.getOrderID(), request.getId(), request.getBody(), request.getBarcode(), progressView);
                                        }
                                    }else {
                                        List<String> urls = RunnerApplication.uploadImage(request.getOrderID());
                                        try {
                                            if (urls.size() > 0) {
                                                JSONObject data = new JSONObject();
                                                data.accumulate("orderid", request.getId());
                                                data.accumulate("signature", urls.get(0));
                                                for (int j = 1; j < urls.size(); j++)
                                                    data.accumulate("pic" + j, urls.get(j));
                                                db.updateRequestBody(request.getId(), data.toString());
                                                RunnerApplication.postBarcode(request.getOrderID(), request.getId(), request.getBody(), request.getBarcode(), progressView);
                                            }
                                        } catch (Exception e) {
                                            if (Constants.DEBUG)
                                                e.printStackTrace();
                                        }
                                    }
                                    break;
                                case 1:
                                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + request.getOrderID(),request.getOrderID(),  request.getId(), request.getBody(), 0);
                                    break;
                                case 2:
                                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickup/" + request.getOrderID(), request.getOrderID(),  request.getId(), request.getBody(), 0);
                                    break;
                                case 3:
                                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickupOOH/" + request.getOrderID(), request.getOrderID(),  request.getId(), request.getBody(), 0);
                                    break;
                            }
                        }

                        return true;
                    }
                })
                .withDrawerGravity(Gravity.END)
                .build();
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
        if (id == R.id.action_moreSP) {
            getOrders("SP");
            return true;
        }else if(id == R.id.action_moreOP){
            getOrders("OP");
            return true;
        }else if(id == R.id.action_moreRSC){
            getOrders("RSC");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseData(JSONArray orders) throws JSONException, IOException {

            for(int i = 0; i < orders.length(); i++){
                ObjectMapper objectMapper = new ObjectMapper();
                Order order = objectMapper.readValue(orders.getJSONObject(i).toString(), Order.class);
                AppOrder order1 = new AppOrder(order.getId(), order.getProductPartner().getItemname(),
                        order.getProductPartner().getPickupaddress() + ", " + order.getProductPartner().getPickupstreet() + ", " + order.getProductPartner().getPickupcity(),
                        order.getProductPartner().getPickupmobile(), order.getProductPartner().getDescription(),
                        order.getStatus(), "LISTED", 0, order.getProductPartner().getPickupname(), order.getProductPartner().getReasonforreturn1(),
                        order.getProductPartner().getItemID(), order.getAwb(),order.getProductPartner().getPickuptime(),
                        order.getProductPartner().getBrand(), order.getProductPartner().getCategory());
                ordersList.add(order);
                if(!checkOrderInDB(order1)){
                    ItemRequest ir = db.getParticularRequest(order.getId());
                    if(ir == null) {
                        order1.setPriority(appOrders.size());
                        appOrders.add(order1);
                        db.addOrder(order1);
                    }
                }
            }

        if(appOrders.size()<=0)
            tvMessage.setVisibility(View.VISIBLE);
        else
            tvMessage.setVisibility(View.GONE);
    }

    /**
     * Add data to the List
     */
    private void initList(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager in the RecyclerView
        itemsAdapter = new ItemsAdapter(this, this); // Create Instance of ItemsAdapter
        itemsAdapter.setOnClickItemListener(this);
        mRecyclerView.setAdapter(itemsAdapter); // Set Adapter for RecyclerView
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(this);// create MyItemTouchHelperCallback
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
        touchHelper.attachToRecyclerView(mRecyclerView); // Attach ItemTouchHelper to RecyclerView
        itemsAdapter.updateItems(appOrders.size());
    }

    private boolean checkOrderInDB(AppOrder appOrder){
        if(appOrders.size() <= 0)
            return false;
        for(int i = 0; i < appOrders.size(); i++){
            if(appOrder.getId() == appOrders.get(i).getId())
                return true;
        }
        return false;
    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        int newPriority = appOrders.get(newPosition).getPriority();
        int oldPriority = appOrders.get(oldPosition).getPriority();
//
//        //update priority in db
        db.updateItem(appOrders.get(oldPosition).getId(), newPriority);
        db.updateItem(appOrders.get(newPosition).getId(), oldPriority);
//
//        //swap priority
        appOrders.get(newPosition).setPriority(oldPriority);
        appOrders.get(oldPosition).setPriority(newPriority);

        //swap item
        Collections.swap(appOrders, oldPosition, newPosition); // change position
        itemsAdapter.notifyItemMoved(oldPosition, newPosition); //notifies changes in adapter, in this case use the notifyItemMoved
    }


    @Override
    public void onClickItem(View v, int position) {
        Intent intent = new Intent(this, SingleOrder.class);
        intent.putExtra("order", appOrders.get(position));
        this.startActivity(intent);
    }

    @Override
    public void onClickCall(View v, int position) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + appOrders.get(position).getContact()));
        startActivity(intent);
    }

    private void getOrders(final String listType){
        String url = "";
        if(listType.equals("SP")){
            url = "http://partner.agilo.in/runner/rapi/orders/SP/id/" + pageSP + "/8";
        }else if(listType.equals("OP")){
            url = "http://partner.agilo.in/runner/rapi/orders/OP/id/" + pageOP + "/8";
        }else if(listType.equals("RSC")){
            url = "http://partner.agilo.in/runner/rapi/orders/RSC/id/" + pageRSC + "/8";
        }

        if(Constants.DEBUG)
            Log.d("MainActivity", "Request Orders");
        progressView.start();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressView.stop();
                if(Constants.DEBUG)
                    System.out.println(response);
                try {
                    if (listType.equals("SP")) {
                        JSONArray orders = new JSONArray(response);
                        if(orders.length() > 0){
                            if(orders.length() == 8)
                            pageSP++;
                            parseData(orders);
                            itemsAdapter.updateItems(appOrders.size());
                        }else {
                            alertMessage("No more orders for SP");
                        }
                    } else if (listType.equals("OP")) {
                        JSONArray orders = new JSONArray(response);
                        if(orders.length() > 0){
                            if(orders.length() == 8)
                            pageOP++;
                            parseData(orders);
                            itemsAdapter.updateItems(appOrders.size());
                        }else{
                            alertMessage("No more orders for OP");
                        }
                    } else if (listType.equals("RSC")) {
                        JSONArray orders = new JSONArray(response);
                        if(orders.length() > 0){
                            if(orders.length() == 8)
                            pageRSC++;
                            parseData(orders);
                            itemsAdapter.updateItems(appOrders.size());
                        }else{
                            alertMessage("No more orders for RSC");
                        }
                    }
                }catch (Exception e){
                    if(Constants.DEBUG)
                        e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertMessage("Connectivity Problem");
                progressView.stop();
                if (error != null) {
                    Log.e("EXCEPTION", error.toString());
                }
                Log.e("EXCEPTION", "Exception occured in volley");
                error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                "",
                listener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Constants.username+":"+Constants.password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RunnerApplication.getInstance().getRequestQueue().add(request);
    }

    private void alertMessage(String msg) {


        final AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Runner");

        // Setting Dialog Message

        alertDialog.setMessage(msg);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.mipmap.ic_launcher);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    //Request Handlers (Volley)

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
