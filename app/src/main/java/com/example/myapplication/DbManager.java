package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbManager extends SQLiteOpenHelper {

    private static final String dbname = "Bill.db";
    SQLiteDatabase db = this.getWritableDatabase();
    public DbManager(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String str1 = "create table t_bill (id integer primary key autoincrement, perticulars text, costIncurred integer, actSufficientCost integer,diff integer, addedondatetime Date, date Date)";
        db.execSQL(str1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists t_bill");
        onCreate(db);
    }

    public Integer getSavings(){
        Cursor cursor = db.rawQuery("select sum(diff) from t_bill ",null);
        int diffTotal=0;
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                diffTotal = Integer.parseInt(String.valueOf(cursor.getInt(0)));
            }
        }
        return diffTotal;
    }

    public int saveOne(String perticulars, Integer costIncurred, Integer actSufficientCost, Integer diff, String addedondatetime, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("perticulars", String.valueOf(perticulars));
        cv.put("costIncurred", String.valueOf(costIncurred));
        cv.put("actSufficientCost", String.valueOf(actSufficientCost));
        cv.put("diff", String.valueOf(diff));
        cv.put("addedondatetime", String.valueOf(addedondatetime));
        cv.put("date", String.valueOf(date));
        System.out.println(cv);
        long res = db.insert("t_bill",null,cv);
        if(res==-1){
            return -1;
        }else{
            return 1;
        }
    }

    public Cursor getOne(Integer id) {
        String str = "select * from t_bill where id = '"+id+"'";
        Cursor cur =  db.rawQuery(str,null);
        return cur;
    }

    public int updateRec(Integer id, String pert, Integer costInc, Integer actSuffCost, Integer diff, String addedDateTime, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("perticulars", String.valueOf(pert));
        cv.put("costIncurred", String.valueOf(costInc));
        cv.put("actSufficientCost", String.valueOf(actSuffCost));
        cv.put("diff", String.valueOf(diff));
        cv.put("addedondatetime", String.valueOf(addedDateTime));
        cv.put("date", String.valueOf(date));
        int res = db.update("t_bill",cv,"id = ?", new String[]{String.valueOf(id)});
        System.out.println(cv);
        return  res;
    }

    public String deleteExpByBillNo(int billno) {
        long res = db.delete("t_bill"," id = ?",new String[]{String.valueOf(billno)});
        if(res==-1){
            return "Expense Deletion Failed: "+billno;
        }else{
            return "Expense deleted successfully: "+billno;
        }
    }

    public Cursor fetchAll() {
        String str = "select * from t_bill order by 1 desc ";
        Cursor cur =  db.rawQuery(str,null);
        return cur;
    }
}
