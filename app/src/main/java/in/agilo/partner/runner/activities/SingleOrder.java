package in.agilo.partner.runner.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import in.agilo.partner.runner.Constants;
import in.agilo.partner.runner.DatabaseHelper;
import in.agilo.partner.runner.R;
import in.agilo.partner.runner.RunnerApplication;
import in.agilo.partner.runner.model.AppOrder;
import in.agilo.partner.runner.model.ItemRequest;
import in.agilo.partner.runner.model.ItemUpload;
import in.agilo.partner.runner.view.CanvasView;

public class SingleOrder extends Activity {

    AppOrder mOrder;
    @InjectView(R.id.tvItem)
    TextView tvItem;

    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvReason)
    TextView tvReason;
    @InjectView(R.id.tvShipping)
    TextView tvShipping;
    @InjectView(R.id.tvAWB)
    TextView tvAWB;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.tvBrand)
    TextView tvBrand;
    @InjectView(R.id.tvCategory)
    TextView tvCategory;

    @InjectView(R.id.tvAddress)
    TextView tvAddress;


    //Photo Click variable
    int snapshot = 0;
    ImageView snapshot1, snapshot2, snapshot3, snapshot4;
    private String picName = "";
    Uri picUri;          //uri for selected image


    //Barcode
    private Class<?> mClss;
    private MaterialEditText barcode;

    //Dialog Pickup
    TextView tvMessage;
    RotateLoading progressView;

    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);
        ButterKnife.inject(this);

        mOrder = (AppOrder) getIntent().getSerializableExtra("order");
        db = new DatabaseHelper(this);
        setupView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAPTURE_IMAGE && resultCode == RESULT_OK) {

            if (Constants.DEBUG)
                System.out.println(picUri + " " + picName + " ");
            final File auxFile = new File(picUri.getPath());
            FileOutputStream out = null;
            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);

                Bitmap bitmap = decodeSampledBitmapFromUri(picUri, 128, 128);
                bitmap = getResizedBitmap(bitmap);

                try {
                    out = new FileOutputStream(auxFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    if (Constants.DEBUG)
                        e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                            ItemUpload itemUpload = new ItemUpload(0, 0, picName, auxFile.getPath(), mOrder.getId());
                            db.addUpload(itemUpload);
                            switch (snapshot) {
                                case 1:
                                    snapshot1.setImageBitmap(bitmap);
                                    break;
                                case 2:
                                    snapshot2.setImageBitmap(bitmap);
                                    break;
                                case 3:
                                    snapshot3.setImageBitmap(bitmap);
                                    break;
                                case 4:
                                    snapshot4.setImageBitmap(bitmap);
                                    break;

                            }
                        }
                    } catch (IOException e) {
                        if (Constants.DEBUG)
                            e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                if (Constants.DEBUG)
                    e.printStackTrace();
            }
        } else if (requestCode == Constants.CAPTURE_BARCODE) {
            try {
                String message = data.getStringExtra("barcode");
                if (Constants.DEBUG)
                    System.out.println(message);
                barcode.setText(message);
            }catch(Exception e){
                if(Constants.DEBUG)
                    e.printStackTrace();
            }
        }
    }


    private void setupView() {
        tvItem.setText(mOrder.getName());
        tvAddress.setText(mOrder.getAddress());
        tvName.setText(mOrder.getCustomerName());
        tvAWB.setText(mOrder.getAwb());
        tvBrand.setText(mOrder.getBrand());
        tvCategory.setText(mOrder.getCategory());
        tvReason.setText(mOrder.getReason());
        tvTime.setText(mOrder.getTime());
        tvShipping.setText(mOrder.getShippingID());
    }

    @OnClick(R.id.btnPickUp)
    public void onClickPickUp(View v) {
        dialogPickUp();
    }

    @OnClick(R.id.btnCancel)
    public void onClickCancel(View v) {
        dialogCancel();
    }

    @OnClick(R.id.btnReschedule)
    public void onClickReschedule(View v) {
        dialogReschedule();
    }

    @OnClick(R.id.btnOOH)
    public void onClickOOH(View v) {
        try {
            int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 3, ""));
            System.out.println("OOH " + _id);
            RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickupOOH/" + mOrder.getId(), mOrder.getId(), _id, "", 0);
            db.deleteOrder(mOrder.getId());
            finishActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnCall)
    public void callCustomer(View v){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mOrder.getContact()));
        startActivity(intent);
    }

    private void dialogPickUp() {
        if (Constants.DEBUG)
            Log.d(this.getClass().getSimpleName(), "Register User ");
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrder.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_pickup, null);

        final TextView tvSign = (TextView) dView.findViewById(R.id.tvSigned);
        barcode = (MaterialEditText) dView.findViewById(R.id.etBarcode);

        ImageButton btnScanner = (ImageButton) dView.findViewById(R.id.btnScanner);
        ImageButton btnSign = (ImageButton) dView.findViewById(R.id.btnSign);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSign(tvSign);
            }
        });

        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSimpleActivity(v);
            }
        });

        snapshot1 = (ImageView) dView.findViewById(R.id.imageView1);
        snapshot2 = (ImageView) dView.findViewById(R.id.imageView2);
        snapshot3 = (ImageView) dView.findViewById(R.id.imageView3);
        snapshot4 = (ImageView) dView.findViewById(R.id.imageView4);

        snapshot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot = 1;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                picName = Constants.username + "IMG_" + timeStamp + ".png";
                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getOutputMediaFile(1);
                    picUri = Uri.fromFile(file); // create
                    i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                    startActivityForResult(i, Constants.CAPTURE_IMAGE);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        snapshot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot = 2;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                picName = Constants.username + "IMG_" + timeStamp + ".png";
                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getOutputMediaFile(1);
                    picUri = Uri.fromFile(file); // create
                    i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                    startActivityForResult(i, Constants.CAPTURE_IMAGE);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        snapshot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot = 3;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                picName = Constants.username + "IMG_" + timeStamp + ".png";
                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getOutputMediaFile(1);
                    picUri = Uri.fromFile(file); // create
                    i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                    startActivityForResult(i, Constants.CAPTURE_IMAGE);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        snapshot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot = 4;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                picName = Constants.username + "IMG_" + timeStamp + ".png";
                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getOutputMediaFile(1);
                    picUri = Uri.fromFile(file); // create
                    i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                    startActivityForResult(i, Constants.CAPTURE_IMAGE);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }
            }
        });


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
                JSONObject data = new JSONObject();

                if (TextUtils.isEmpty(barcode.getText())) {
                    check = false;
                    barcode.setError("Input Invalid");
                }

                if (tvSign.getText().equals("Not Signed")) {
                    check = false;
                    tvSign.setError("Not Signed");
                }


                if (check) {
                    try {
                        int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 0, "", barcode.getText().toString()));
                        List<String> urls = RunnerApplication.uploadImage(mOrder.getId());
                        try {
                            if (urls.size() > 0) {
                                JSONObject signatureData = new JSONObject();
                                signatureData.accumulate("orderid", mOrder.getId());
                                signatureData.accumulate("signature", urls.get(0));
                                for (int j = 1; j < urls.size(); j++)
                                    signatureData.accumulate("pic" + j, urls.get(j));
                                db.updateRequestBody(_id, signatureData.toString());
                                RunnerApplication.postBarcode(mOrder.getId(), _id, signatureData.toString(), barcode.getText().toString(), progressView);
                            }
                        } catch (Exception e) {
                            if (Constants.DEBUG)
                                e.printStackTrace();
                        }
                        db.deleteOrder(mOrder.getId());
                        dialog.dismiss();
                        finishActivity();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

    //Click snapshots

    /**
     * Create a File for saving an image
     */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");

        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    picName);
        } else {
            return null;
        }

        return mediaFile;
    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = Constants.IMG_MAX_SIZE;
            height = (int) (width / bitmapRatio);
        } else {
            height = Constants.IMG_MAX_SIZE;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    //Customer Signature

    private void dialogSign(final TextView tvSign) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrder.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_sign, null);
        final ImageButton btnClear = (ImageButton) dView.findViewById(R.id.btnClear);

        final CanvasView canvas = (CanvasView) dView.findViewById(R.id.canvas);
        canvas.setMode(CanvasView.Mode.DRAW);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.clear();
            }
        });

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
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "MyApplication");

                /**Create the storage directory if it does not exist*/
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                final String name = Constants.username + "IMG_" + timeStamp + ".png";
                final File mediaFile = new File(mediaStorageDir.getPath() + File.separator + name);

                FileOutputStream out = null;
                try {

                    Bitmap bitmap = getResizedBitmap(canvas.getBitmap());

                    try {
                        out = new FileOutputStream(mediaFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (Exception e) {
                        if (Constants.DEBUG)
                            e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                                ItemUpload itemUpload = new ItemUpload(0, 1, name, mediaFile.getPath(), mOrder.getId());
                                db.addUpload(itemUpload);
                            }
                        } catch (IOException e) {
                            if (Constants.DEBUG)
                                e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    if (Constants.DEBUG)
                        e.printStackTrace();
                }

                tvSign.setText("Signed");
                dialog.dismiss();
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    //BarCode Reader
    public void launchSimpleActivity(View v) {
        launchActivity(SimpleScannerActivity.class);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, Constants.CAPTURE_BARCODE);
        }
    }

    //volley requests


    //Cancel Pickup

    private void dialogCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrder.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_cancel_pickup, null);

        final TextView reason1 = (TextView) dView.findViewById(R.id.tvReason1);
        final TextView reason2 = (TextView) dView.findViewById(R.id.tvReason2);
        final TextView reason3 = (TextView) dView.findViewById(R.id.tvReason3);
        final TextView reason4 = (TextView) dView.findViewById(R.id.tvReason4);
        final TextView reason5 = (TextView) dView.findViewById(R.id.tvReason5);

        final MaterialEditText etReason = (MaterialEditText) dView.findViewById(R.id.etReason);


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
            public void onClick(View v) {
                boolean check = true;

                if (TextUtils.isEmpty(etReason.getText())) {
                    check = false;
                    etReason.setError("Please give reason for cancellation");
                }

                if (check) {
                    try {
                        int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, etReason.getText().toString()));
                        RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, etReason.getText().toString(), 0);
                        db.deleteOrder(mOrder.getId());
                        dialog.dismiss();
                        finishActivity();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, reason1.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, reason1.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, reason2.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, reason2.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, reason3.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, reason3.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, reason4.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, reason4.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 1, reason5.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/cancelPickup/" + mOrder.getId(), mOrder.getId(), _id, reason5.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    //Reschedule

    private void dialogReschedule(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrder.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_cancel_pickup, null);

        final  TextView tvTitle = (TextView) dView.findViewById(R.id.tvTitle);

        final TextView reason1 = (TextView) dView.findViewById(R.id.tvReason1);
        final TextView reason2 = (TextView) dView.findViewById(R.id.tvReason2);
        final TextView reason3 = (TextView) dView.findViewById(R.id.tvReason3);
        final TextView reason4 = (TextView) dView.findViewById(R.id.tvReason4);
        final TextView reason5 = (TextView) dView.findViewById(R.id.tvReason5);

        reason1.setText("Customer has asked to come back later");
        reason2.setText("Address not traceable/accessible currently");
        reason3.setText("Customer phone not reachable currently");

        tvTitle.setText("Reschedule Order");

        reason4.setVisibility(View.GONE);
        reason5.setVisibility(View.GONE);

        final MaterialEditText etReason = (MaterialEditText) dView.findViewById(R.id.etReason);


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
            public void onClick(View v) {
                boolean check = true;

                if (TextUtils.isEmpty(etReason.getText())) {
                    check = false;
                    etReason.setError("Please give reason for reschedule");
                }

                if (check) {
                    try {
                        int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 2, etReason.getText().toString()));
                        RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickup/" + mOrder.getId(), mOrder.getId(), _id, etReason.getText().toString(), 0);
                        db.deleteOrder(mOrder.getId());
                        dialog.dismiss();
                        finishActivity();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 2, reason1.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickup/" + mOrder.getId(), mOrder.getId(), _id, reason1.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 2, reason2.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickup/" + mOrder.getId(), mOrder.getId(), _id, reason2.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int _id = (int) db.addRequest(new ItemRequest(0, mOrder.getId(), 2, reason3.getText().toString()));
                    RunnerApplication.postRequest("http://partner.agilo.in/runner/rapi/reschedulePickup/" + mOrder.getId(), mOrder.getId(), _id, reason3.getText().toString(), 0);
                    db.deleteOrder(mOrder.getId());
                    dialog.dismiss();
                    finishActivity();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private void finishActivity(){
        db.closeDB();
        finish();
    }
}
