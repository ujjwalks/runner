package in.agilo.partner.runner;

/**
 * Created by Ujjwal on 3/30/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.agilo.partner.runner.model.AppOrder;
import in.agilo.partner.runner.model.ItemRequest;
import in.agilo.partner.runner.model.ItemUpload;

/**
 * Created by Ujjwal on 2/19/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Table Names
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_PENDING_ACTIONS = "pending";
    private static final String TABLE_PENDING_UPLOAD = "upload";

    // column names orders
    private static final String KEY_STATUS = "status";
    private static final String KEY_APP_STATUS = "app_status";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "description";
    private static final String KEY_EXTRA = "extra";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PRIORITY = "priority";

    //column names pending
    private static final String KEY_TYPE = "type";
    private static final String KEY_BODY = "body";
    private static final String KEY_UPLOAD = "upload";
    private static final String KEY_BARCODE = "barcode";


    //column names upload
    private static final String KEY_URI = "uri";
    private static final String KEY_ORDERID = "orderid";


    // Table Create Statements
    private static final String CREATE_TABLE_CONSULTATION = "CREATE TABLE "
            + TABLE_ORDERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_DESC + " TEXT," + KEY_CONTACT + " TEXT,"
            + KEY_STATUS + " TEXT,"  + KEY_EXTRA + " TEXT," + KEY_ADDRESS + " TEXT,"  + KEY_APP_STATUS +  " TEXT," +  KEY_PRIORITY + " INTEGER )";

    private static final String CREATE_TABLE_UPLOAD = "CREATE TABLE "
            + TABLE_PENDING_UPLOAD + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME
            + " TEXT," + KEY_URI + " TEXT," + KEY_ORDERID  + " INTEGER," + KEY_TYPE
            + " INTEGER )";

    private static final String CREATE_TABLE_PENDING_ACTIONS = "CREATE TABLE "
            + TABLE_PENDING_ACTIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TYPE
            + " INTEGER," + KEY_BODY + " TEXT," + KEY_UPLOAD + " BOOLEAN," + KEY_BARCODE + " TEXT,"
            + KEY_ORDERID  + " INTEGER UNIQUE )";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_CONSULTATION);
        db.execSQL(CREATE_TABLE_UPLOAD);
        db.execSQL(CREATE_TABLE_PENDING_ACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING_UPLOAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING_ACTIONS);

        // create new tables
        onCreate(db);
    }

    /*****************************Insert Table*************************************/

    public long addOrder(AppOrder order) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, order.getId());
        values.put(KEY_NAME, order.getName());
        values.put(KEY_DESC, order.getDetails());
        values.put(KEY_CONTACT, order.getContact());
        values.put(KEY_STATUS, order.getStatus());
        values.put(KEY_EXTRA, order.getExtras());
        values.put(KEY_ADDRESS, order.getAddress());
        values.put(KEY_APP_STATUS, order.getAppStatus());
        values.put(KEY_PRIORITY, order.getPriority());

        // insert row
        long _id = db.insert(TABLE_ORDERS, null, values);
        return _id;
    }

    public long addUpload(ItemUpload upload) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, upload.getName());
        values.put(KEY_URI, upload.getUri());
        values.put(KEY_ORDERID, upload.getOrderID());
        values.put(KEY_TYPE, upload.getType());

        // insert row
        long _id = db.insert(TABLE_PENDING_UPLOAD, null, values);
        return _id;
    }

    public long addRequest(ItemRequest request) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, request.getType());
        values.put(KEY_BODY, request.getBody());
        values.put(KEY_UPLOAD, request.isUploads());
        values.put(KEY_BARCODE, request.getBarcode());
        values.put(KEY_ORDERID, request.getOrderID());

        // insert row
        long _id = db.insert(TABLE_PENDING_ACTIONS, null, values);
        return _id;
    }

    /*
 * Updating a todo
 */
    public int updateItem(int id, int priority) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRIORITY, priority);

        // updating row
        return db.update(TABLE_ORDERS, values, KEY_ID + " = " + id, null);
    }

    public int updateRequestBarCode(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, "");

        // updating row
        return db.update(TABLE_PENDING_ACTIONS, values, KEY_ID + " = " + id, null);
    }


    public int updateRequestBody(int id, String body) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BODY, body);
        values.put(KEY_UPLOAD, true);

        // updating row
        return db.update(TABLE_PENDING_ACTIONS, values, KEY_ID + " = " + id, null);
    }



    /*
     *  get All orders
     */

    public List<AppOrder> getAllOrders() {
        List<AppOrder> ordertList = new ArrayList<AppOrder>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ORDERS;

        if(Constants.DEBUG)
            System.out.println(selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppOrder order = new AppOrder();
                order.setId(cursor.getInt(0));
                order.setName(cursor.getString(1));
                order.setDetails(cursor.getString(2));
                order.setContact(cursor.getString(3));
                order.setStatus(cursor.getString(4));
                order.setExtras(cursor.getString(5));
                order.setAddress(cursor.getString(6));
                order.setAppStatus(cursor.getString(7));
                order.setPriority(cursor.getInt(8));
                // Adding contact to list
                ordertList.add(order);
            } while (cursor.moveToNext());
        }

        if (ordertList.size() > 0) {
            Collections.sort(ordertList, new Comparator<AppOrder>() {
                @Override
                public int compare(final AppOrder object1, final AppOrder object2) {
                    return Integer.compare(object1.getPriority(), object2.getPriority());
                }
            });
        }
        // return contact list
        return ordertList;
    }

    /*
     *  get All Request
     */

    public List<ItemRequest> getAllRequests() {
        List<ItemRequest> requests = new ArrayList<ItemRequest>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_ACTIONS;

        if(Constants.DEBUG)
            System.out.println(selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemRequest request = new ItemRequest();
                request.setId(cursor.getInt(0));
                request.setType(cursor.getInt(1));
                request.setBody(cursor.getString(2));
                request.setUploads(cursor.getInt(3) > 0);
                request.setBarcode(cursor.getString(4));
                request.setOrderID(cursor.getInt(5));

                // Adding contact to list
                requests.add(request);
            } while (cursor.moveToNext());
        }
        // return contact list
        return requests;
    }

    public ItemRequest getParticularRequest(int _orderId){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_ACTIONS + " WHERE "
                + KEY_ORDERID + " = " + _orderId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        try {

            ItemRequest td = new ItemRequest();
            td.setId((c.getInt(c.getColumnIndex(KEY_ID))));
            td.setBarcode(c.getString(c.getColumnIndex(KEY_BARCODE)));
            td.setBody(c.getString(c.getColumnIndex(KEY_BODY)));
            td.setOrderID(c.getInt(c.getColumnIndex(KEY_ORDERID)));
            td.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
            td.setUploads(c.getInt(c.getColumnIndex(KEY_UPLOAD)) > 0);
            return td;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
     *  get All Uploads
     */

    public List<ItemUpload> getAllUploads(int orderId) {
        List<ItemUpload> uploads = new ArrayList<ItemUpload>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_UPLOAD + " WHERE " + KEY_ORDERID + " = " + orderId;

        if(Constants.DEBUG)
            System.out.println(selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemUpload upload = new ItemUpload();
                upload.setId(cursor.getInt(0));
                upload.setName(cursor.getString(1));
                upload.setUri(cursor.getString(2));
                upload.setOrderID(cursor.getInt(3));
                upload.setType(cursor.getInt(4));

                // Adding contact to list
                uploads.add(upload);
            } while (cursor.moveToNext());
        }
        // return uploads list for particular request
        return uploads;
    }



    /**
     * get single Consultation from Consultation Key
     */
    public AppOrder getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ORDERS + " WHERE "
                + KEY_ID + " = " + id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        try {

            AppOrder td = new AppOrder();
            td.setId((c.getInt(c.getColumnIndex(KEY_ID))));
            td.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            td.setDetails(c.getString(c.getColumnIndex(KEY_DESC)));
            td.setAddress(c.getString(c.getColumnIndex(KEY_ADDRESS)));
            td.setAppStatus(c.getString(c.getColumnIndex(KEY_APP_STATUS)));
            td.setStatus(c.getString(c.getColumnIndex(KEY_STATUS)));
            td.setContact(c.getString(c.getColumnIndex(KEY_CONTACT)));
            td.setExtras(c.getString(c.getColumnIndex(KEY_EXTRA)));
            td.setPriority(c.getInt(c.getColumnIndex(KEY_PRIORITY)));
            return td;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ItemUpload getItemUpload(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_UPLOAD + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        try {

            ItemUpload td = new ItemUpload();
            td.setId((c.getInt(c.getColumnIndex(KEY_ID))));
            td.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            td.setUri(c.getString(c.getColumnIndex(KEY_URI)));
            td.setOrderID(c.getInt(c.getColumnIndex(KEY_ORDERID)));
            td.setType(c.getInt((c.getColumnIndex(KEY_TYPE))));

            return td;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Deleting a Order, Requests, Upload
     */
    public void deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println(""+db.delete(TABLE_ORDERS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)}));
    }

    public void deleteUpload(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PENDING_UPLOAD, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteRequest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PENDING_ACTIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}

