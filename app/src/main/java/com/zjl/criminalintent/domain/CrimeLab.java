package com.zjl.criminalintent.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zjl.criminalintent.database.CrimeBaseHelper;
import com.zjl.criminalintent.database.CrimeCursorWrapper;
import com.zjl.criminalintent.database.CrimeDbSchema;
import com.zjl.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lenovo on 2017/8/8.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFileName());
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    /**
     * 获取数据表中第一条Crime记录
     * @param uuid
     * @return
     */
    public Crime getCrime(UUID uuid){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void addCrime(Crime crime){
        ContentValues values = getContentValues(crime);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void removeCrime(Crime crime){
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }

    /**
     * 根据Crime对象的内容，构建ContentValues对象
     * @param crime
     * @return
     */
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }



}
