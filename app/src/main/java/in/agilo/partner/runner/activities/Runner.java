package in.agilo.partner.runner.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import in.agilo.partner.runner.Constants;
import in.agilo.partner.runner.MainActivity;
import in.agilo.partner.runner.R;
import in.agilo.partner.runner.RunnerApplication;
import in.agilo.partner.runner.utils.StringRequest;
import in.agilo.partner.runner.view.LocationAutoCompleteTextView;

public class Runner extends Activity {

    @InjectView(R.id.etEmail)
    MaterialEditText email;

    @InjectView(R.id.etPassword)
    MaterialEditText password;

    @InjectView(R.id.progressView)
    RotateLoading rlProgressView;

    LocationAutoCompleteTextView atvAddress;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        ButterKnife.inject(this);
        
        autoLogin();
    }

    private void autoLogin() {
        String username = sharedPreferences.getString("username", null);
        String pass = sharedPreferences.getString("password", null);


        if (username != null && pass != null) {
            boolean check = true;
            if(!TextUtils.isEmpty(username)){
                email.setText(username);
            }else{
                check = false;
                email.setError("Invalid Phone");
            }
            if (isPasswordValid(pass)) {
                password.setText(pass);
            }else{
                check = false;
                password.setError("Invalid Password");
            }

            if(check) {
                Constants.username = username;
                Constants.password = pass;
                MainActivity.startMainActivity(Runner.this, rlProgressView);
            }
        }
    }


    private boolean isPasswordValid(String password) {
        return (password.length() > 2);
    }


    @OnClick(R.id.tvRegister)
    public void onClickRegister(View v) {
        dialogRegister();
    }

    @OnClick(R.id.tvForgotPass)
    public void onClickForgot(View v) {
        resetPassword();
    }

    @OnClick(R.id.btnLogin)
    public void login(View view) {
        //// TODO: 4/5/2016 save password
        boolean check = true;
        if (TextUtils.isEmpty(email.getText())) {
            check = false;
            email.setError("Incorrect Phone");
        }

        if (TextUtils.isEmpty(password.getText())) {
            check = false;
            password.setError("Incorrect Password");
        }

        if (check) {
            loginRequest();
        }
    }

    private void dialogRegister() {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Register User");
        AlertDialog.Builder builder = new AlertDialog.Builder(Runner.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_register, null);
        final MaterialEditText etName = (MaterialEditText) dView.findViewById(R.id.etName);
        final MaterialEditText etMobile = (MaterialEditText) dView.findViewById(R.id.etMobile);
        final MaterialEditText etAlternate = (MaterialEditText) dView.findViewById(R.id.etAlternate);
        final MaterialEditText etNukkad = (MaterialEditText) dView.findViewById(R.id.etNukkad);
        final RotateLoading rlView = (RotateLoading) dView.findViewById(R.id.progressView);


        builder.setView(dView)
                // Add action buttons
                .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }

                }).

                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }

                );
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean check = true;
                try {
                    JSONObject data = new JSONObject();

                    if (TextUtils.isEmpty(etName.getText())) {
                        check = false;
                        etName.setError("Invalid Input");
                    }

                    if (TextUtils.isEmpty(etMobile.getText())) {
                        check = false;
                        etMobile.setError("Invalid Input");
                    }

                    if (TextUtils.isEmpty(etNukkad.getText())) {
                        check = false;
                        etNukkad.setError("Input Invalid");
                    }

                    if (check) {
                            data.accumulate("nukkadcode", etNukkad.getText());
                            data.accumulate("name", etName.getText());
                            data.accumulate("email", etMobile.getText());
                            data.accumulate("phone", etMobile.getText());
                            data.accumulate("altphone", etAlternate.getText());
                            data.accumulate("location", null);
                            data.accumulate("kyc1", "");
                            data.accumulate("kyc2", "");
                            data.accumulate("kyc3", "");
                            data.accumulate("kyc1type", "");
                            data.accumulate("kyc2type", "");
                            data.accumulate("kyc3type", "");
                            if (Constants.DEBUG)
                                System.out.println(data.toString());
                            requestRegister(data);
                            dialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void requestRegister(JSONObject data) {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Volley Register User");

        String url = "http://partner.agilo.in/openapi/createRunner";
        rlProgressView.start();


        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rlProgressView.stop();
                if (Constants.DEBUG)
                    System.out.println(response);
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getBoolean("isnew")) {
                        alertMessage("Registration Successful!\nNew Runner Created\nPassword will be messaged to you");
                    } else {
                        alertMessage("You are already registered");
                    }
                } catch (JSONException e) {
                    alertMessage("Registration Failed!");
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rlProgressView.stop();
                alertMessage("Registration Failed");
                if (Constants.DEBUG)
                    error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                data.toString(),
                listener,
                errorListener
        );
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RunnerApplication.getInstance().getRequestQueue().add(request);
    }

    private void alertMessage(String msg) {


        final AlertDialog alertDialog = new AlertDialog.Builder(
                Runner.this).create();

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

    private void resetPassword() {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Reset Password");
        AlertDialog.Builder builder = new AlertDialog.Builder(Runner.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_reset_password, null);
        final MaterialEditText etMobile = (MaterialEditText) dView.findViewById(R.id.etMobile);


        builder.setView(dView)
                // Add action buttons
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }

                }).

                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }

                );
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean check = true;
                try {
                    JSONObject data = new JSONObject();



                    if (TextUtils.isEmpty(etMobile.getText())) {
                        check = false;
                        etMobile.setError("Invalid Input");
                    }

                    if (check) {
                        data.accumulate("contactemail", etMobile.getText());
                        data.accumulate("contactphone", etMobile.getText());
                        if (Constants.DEBUG)
                            System.out.println(data.toString());
                        resetPassword(data);
                        dialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void resetPassword(JSONObject data) {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Volley Change Password");

        String url = "http://partner.agilo.in/password/resetpassword";
        rlProgressView.start();


        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rlProgressView.stop();
                if (Constants.DEBUG)
                    System.out.println(response);
                alertMessage(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rlProgressView.stop();
                alertMessage("Password Change Failed");
                if (Constants.DEBUG)
                    error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                data.toString(),
                listener,
                errorListener
        );
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RunnerApplication.getInstance().getRequestQueue().add(request);
    }

    private void loginRequest() {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Login");

        String url = "http://partner.agilo.in/runner/rapi/check";
        rlProgressView.start();


        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rlProgressView.stop();
                if (Boolean.parseBoolean(response)) {
                    Constants.username = email.getText().toString();
                    Constants.password = password.getText().toString();
                    sharedPreferences.edit().putString("username", Constants.username).commit();
                    sharedPreferences.edit().putString("password", Constants.password).commit();
                    MainActivity.startMainActivity(Runner.this, rlProgressView);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rlProgressView.stop();
                alertMessage("Login Failed");
                if (Constants.DEBUG)
                    error.printStackTrace();
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                "",
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = email.getText().toString() + ":" + password.getText().toString();
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


}
