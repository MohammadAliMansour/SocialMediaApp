//package com.example.socialmediaapp.data;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import androidx.annotation.Nullable;
//
//import com.example.socialmediaapp.model.ModelUsers;
//
//import java.util.ArrayList;
//
//public class DbHelper extends SQLiteOpenHelper {
//    public static final String DATABASE_NAME = "Contact.db";
//    public static final int DATABASE_VERSION = 1;
//    public static final String TABLE_NAME = "ContactTable";
//    public static final String C_ID = "ID";
//    public static final String C_IMAGE = "IMAGE";
//    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
//            C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//            C_IMAGE + " TEXT"+")";
//
//
//
//    public DbHelper(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//        //create table on database
//        db.execSQL(CREATE_TABLE);
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//
//    // Insert Function to insert data in database
//    public long insertContact(String image, String name, String email, String password){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(C_IMAGE,image);
//        long id = db.insert(TABLE_NAME,null,contentValues);
//        db.close();
//        return id;
//
//    }
//
//    public void updateContact(String id,String image){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(C_IMAGE,image);
//        db.update(TABLE_NAME,contentValues,C_ID+" =? ",new String[]{id} );
//
//        // close db
//        db.close();
//
//    }
//
//
//
//
//
//    // get data
//    public ArrayList<ModelUsers> getAllData(){
//        //create arrayList
//        ArrayList<ModelUsers> arrayList = new ArrayList<>();
//        //sql command query
//        String selectQuery = "SELECT * FROM "+ TABLE_NAME;
//
//        //get readable db
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery,null);
//
//        // looping through all record and add to list
//        if (cursor.moveToFirst()){
//            do {
//                ModelUsers modelContact = new ModelUsers(
//                        // only id is integer type
//                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(C_ID)),
//
//                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_IMAGE))
//                );
//                arrayList.add(modelContact);
//            }while (cursor.moveToNext());
//        }
//        db.close();
//        return arrayList;
//    }
//
//    // search data in sql Database
//
//
//}