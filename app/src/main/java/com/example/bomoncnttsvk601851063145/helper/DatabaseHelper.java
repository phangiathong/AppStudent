package com.example.bomoncnttsvk601851063145.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bomoncnttsvk601851063145.model.SinhVien;
import com.example.bomoncnttsvk601851063145.model.User;


//abstract class : Lớp trừu tượng
//DatabaseHelper.Database_name
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String Database_name="Students.db";
    public static final String Table_name="student_table";
    public static final String col_masv="masv";
    public static final String col_tensv="tensv";
    public static final String col_gt="gt";
    public static final String col_lop="lop";
    public static final String col_image="image";

    //Tạo bảng user gồm có trường username và password
    public static final String Table_user="user_table";
    public static final String col_taikhoan="username";
    public static final String col_matkhau="password";

    //Tiến hành tạo username và pass cho user riêng
    //username =massv
    //password =123
    //Gửi file apk + source
    //Show lịch sử đăng nhập lên listview

    //Tạo database
    public DatabaseHelper(@Nullable Context context) {
        super(context, Database_name,null,1);
        //chay vô đây trước

    }
 //gọi kế tiếp
    //Tạo table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+Table_name+" (masv TEXT primary key,tensv TEXT, gt TEXT,lop TEXT, image TEXT)");
        db.execSQL("create table "+Table_user+" (username TEXT primary key, password TEXT)");
    }

    //Thay đổi thành phần trong database sẽ update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);//tạo lại

    }

    //Hàm insert vào database dưới dạng object.
    public boolean insertDataStudent(SinhVien sv){
        SQLiteDatabase db=this.getWritableDatabase(); //Ghi dữ liệu vào table
        ContentValues cv=new ContentValues();
        cv.put(col_masv,sv.getMasv());
        cv.put(col_tensv,sv.getTensv());
        cv.put(col_gt,sv.getGt());
        cv.put(col_lop,sv.getLop());
        cv.put(col_image,sv.getImage());
        Long result=db.insert(Table_name,null,cv);

        if(result==-1){
            return false;//insert không thành công
        }else
        {
            return true;
        }

    }

    //Show trong csdl
    public Cursor showDataStudents() {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor =db.rawQuery("select * from "+Table_name,null);
        return cursor;

    }

    //Xóa phần tử trong csdl
    public Integer deleteStudent(String masv){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(Table_name,"masv = ?", new String[]{masv});
    }

    //Update khi có sv bị xóa hoặc thêm
    public boolean updateStudent(SinhVien sv){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(col_tensv, sv.getTensv());
        cv.put(col_gt, sv.getGt());
        cv.put(col_lop, sv.getLop());
        cv.put(col_image, sv.getImage());
       int result= db.update(Table_name,cv,"masv = ?",new String[]{sv.getMasv()});
        if(result==-1){
            return false;//insert không thành công
        }else
        {
            return true;
        }
    }
    //user database, của tài khoản mật khẩu.
    public boolean insertuser(User user){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(col_taikhoan, user.getUsername());
        cv.put(col_matkhau, user.getPassword());
        Long result=db.insert(Table_user,null,cv);
        if(result==-1){
            return false;//insert không thành công
        }else
        {
            return true;
        }

    }

    //Show user //Nếu người dùng nhập đúng user name và password thì hàm trả về 1 bảng có dữ liệu
    public Cursor checkLoginUser(String username, String password){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor =db.rawQuery("select * from "+Table_user + " where username=? and password=?",new String[]{username,password});
        return cursor;
    }

    //Kiểm tra user đã tồn tại hay chưa
    public Cursor checkUserExist(String username){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor =db.rawQuery("select * from "+Table_user + " where username=?",new String[]{username});
        return cursor;
    }
}
