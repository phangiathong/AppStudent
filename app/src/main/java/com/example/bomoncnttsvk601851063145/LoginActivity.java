package com.example.bomoncnttsvk601851063145;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bomoncnttsvk601851063145.helper.DatabaseHelper;
import com.example.bomoncnttsvk601851063145.model.User;

public class LoginActivity extends AppCompatActivity {
    EditText edtu,edtp;
    Button btnlogin;
    CheckBox ck;
    public static final String MyPREFERENCES="MYPREFS";
    SharedPreferences pref;// khai báo
    SharedPreferences.Editor editor;//chỉnh sửa dữ liệu
    DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Đăng nhập hệ thống");

        mapView();
        //
        pref=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); //Khi đăng nhập sẽ ghi lại thông tin để lần sau đăng nhập không cần đăng nhập lại

        mydb=new DatabaseHelper(this);

        Cursor cursor=mydb.checkUserExist("1851063145");
        if (cursor.getCount()==0) { //Không tìm thấy user
            User user=new User("1851063145","123");
            mydb.insertuser(user);
        }

        //Khi chọn nhớ đăng nhập sẽ ghi vô file này
        //Đọc thông tin từ share ra
        String username=pref.getString("USERNAME","");
        String password=pref.getString("PASSWORD","");
        //Nếu trong file có dữ liệu rồi thì mở main lên
        if(!username.equals("")&&!password.equals("")){

            Intent in=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(in);
            finish();
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u=edtu.getText().toString();
                String p=edtp.getText().toString();
                //Kiểm tra u và p có tồn tại trong user csdl ?
                Cursor userLogin=mydb.checkLoginUser(u,p); //Kiểm tra tài khoản có đúng không
                //Nhớ đăng nhập
                if(userLogin.getCount()>0){ //Tồn tại trong csdl, nhập tài khoản mật khẩu chính xác
                    if(ck.isChecked()){
                        //lưu thông tin xuống sharepreferences
                        //Muốn chỉnh sửa phải gọi edit()
                        editor=pref.edit(); //chỉnh sửa file  MYPREFS.xml
                        editor.putString("USERNAME",u); //ghi thông tin vào fields USERNAME='admin'
                        editor.putString("PASSWORD",p);
                        editor.commit();
                    }else
                    {
                        //xóa preferences
                        editor=pref.edit();
                        editor.clear();
                        editor.commit();
                    }
                    finish();
                    Intent in=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(in);
                }else {
                    Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mapView() {
        edtu=findViewById(R.id.edtusername);
        edtp=findViewById(R.id.edt_password);
        btnlogin=findViewById(R.id.btnlogin);
        ck=(CheckBox)findViewById(R.id.checkBoxRemember);
    }
}