package in.agilo.partner.runner;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.agilo.partner.runner.model.ItemUpload;
import in.agilo.partner.runner.utils.StringRequest;

/**
 * Created by Ujjwal on 04-07-2015.
 */
public class RunnerApplication extends Application {

    public static RunnerApplication sInstance;
    private RequestQueue mRequestQueue;
    public static DatabaseHelper db;
    public TransferUtility transferUtility;
    public static AmazonS3 s3;


    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;
        db = new DatabaseHelper(this);

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bcf6d93f-864c-4adc-b802-779c4f6a53ee", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());
        printHashKey();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public synchronized static RunnerApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void printHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ruly.com.rulypro",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void postBarcode(final int orderId, final int requestId, final String data,
                                   String barCode, final RotateLoading progressView) {

        if(Constants.DEBUG)
            System.out.println("POST BARCODE");

        progressView.start();

        String url = "http://partner.agilo.in/runner/rapi/updateBarcode/" + orderId;
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressView.stop();
                if (Constants.DEBUG)
                    System.out.println(response);
                Boolean result = Boolean.parseBoolean(response);
                if (result) {
                    db.updateRequestBarCode(requestId);
                    postRequest("http://partner.agilo.in/runner/rapi/updateProductInfo/" + orderId, orderId, requestId, data, 1);
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                barCode,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Constants.username + ":" + Constants.password;
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

    public static void postRequest(String url, final int orderId, final int requestID, String data, final int type){

        if(Constants.DEBUG)
            System.out.println("POST REQUEST " + url);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Constants.DEBUG)
                    System.out.println(response);
                if(Boolean.parseBoolean(response)){
                    //// TODO: 4/12/2016 delete from db
                    if(type == 0) {
                        db.deleteRequest(requestID);
                    }
                    else
                        postRequest("http://partner.agilo.in/runner/rapi/updatePickup/" + orderId, orderId, requestID, "", 0);
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    Log.e("EXCEPTION", error.toString());
                }
                Log.e("EXCEPTION", "Exception occured in volley");
                error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                data,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Constants.username + ":" + Constants.password;
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

    public static List<String> uploadImage(int orderId){
        List<ItemUpload> itemUploads = db.getAllUploads(orderId);
        List<String> result = new ArrayList<String>();
        String signature = "";


        for(int i = 0; i < itemUploads.size(); i++){
            if(Constants.DEBUG)
                System.out.println("UPLOADING IMAGES");
            final ItemUpload itemUpload = itemUploads.get(i);
            if(itemUpload.getType() == 1) signature = "https://s3-ap-southeast-1.amazonaws.com/agilo/" + itemUpload.getName();
            else result.add("https://s3-ap-southeast-1.amazonaws.com/agilo/" + itemUpload.getName());
            new Thread(new Runnable() {
                public void run() {
                    s3.putObject(new PutObjectRequest(Constants.MY_BUCKET, itemUpload.getName(), new File(itemUpload.getUri()))
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                }
            }).start();
        }

        if(TextUtils.isEmpty(signature))
            return new ArrayList<String>();
        else{
            result.add(0, signature);
            for(int i = 0; i < result.size(); i++)
                db.deleteUpload(itemUploads.get(i).getId());
            return result;
        }
    }
}

