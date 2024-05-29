package com.example.socialmediaapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.socialmediaapp.model.ModelUsers;

import java.io.ByteArrayOutputStream;

public class DataSource {
    SQLiteDatabase database;
    DbHelper dbHelper;

    public DataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertUser(ModelUsers user) {
        boolean didSucceed = false;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", user.getEmail());
            if (user.getWallpaper() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                user.getWallpaper().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] wallpaper = byteArrayOutputStream.toByteArray();
                contentValues.put("wallpaper", wallpaper);
            }
            didSucceed = database.insert("users", null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return didSucceed;
    }

    public boolean updateUser(ModelUsers user) {
        boolean didSucceed = false;
        try {
            ContentValues contentValues = new ContentValues();

            if (user.getWallpaper() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                user.getWallpaper().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] wallpaper = byteArrayOutputStream.toByteArray();
                contentValues.put("wallpaper", wallpaper);
            }

            String whereClause = "email = ?";
            String[] whereArgs = { user.getEmail() };

            didSucceed = database.update("users", contentValues, whereClause, whereArgs) > 0;
        } catch (Exception e) {
            Log.e("UpdateUserError", "Error updating user", e);
        }
        return didSucceed;
    }

    public ModelUsers getUser(String email) {
        ModelUsers user = new ModelUsers();

        try {
            String query = "SELECT * FROM users WHERE email = ?";
            Cursor cursor = database.rawQuery(query, new String[]{email});
            if (cursor.moveToFirst()) {
                byte[] wallpaper = cursor.getBlob(2);
                if (wallpaper != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(wallpaper, 0, wallpaper.length);
                    user.setWallpaper(bitmap);
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        }
        return user;
    }

}
