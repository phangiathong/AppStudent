package com.example.bomoncnttsvk601851063145;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bomoncnttsvk601851063145.helper.DatabaseHelper;
import com.example.bomoncnttsvk601851063145.helper.MyListAdapter;
import com.example.bomoncnttsvk601851063145.model.SinhVien;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<SinhVien> arrayListStudents;
    ArrayAdapter<SinhVien> adapterStudents;
    ListView listViewStudents;
    DatabaseHelper mydb;
    ArrayList<SinhVien> StudentCheckItemList;
    public static final String MyPREFERENCES="MYPREFS"; //Khi check nhớ đăng nhập nó sẽ lưu thông tin. khi vào app lần sau thì vào luôn
    SharedPreferences pref;// khai báo
    SharedPreferences.Editor editor;//chỉnh sửa dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Thông Tin Sinh Viên");
        pref=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        StudentCheckItemList=new ArrayList<>();//cấp phát mảng rỗng chưa chứa bất kỳ sinh viên nào

        //New database
        mydb=new DatabaseHelper(this);

        listViewStudents=findViewById(R.id.lvsinhvien);

        //Lấy thông tin sinh viên từ database đưa vào mảng
        arrayListStudents=new ArrayList<>();//Khởi tạo mảng kiểu sinh viên lưu các đối tượng
        Cursor cursor=mydb.showDataStudents(); //Lấy dữ liệu từ csdl trả về biến cursor
        while(cursor.moveToNext()){ //moveToNext duyệt qua tất cả phần tử của cursor.
            SinhVien sv=new SinhVien(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            arrayListStudents.add(sv);
        }
        //custom, mỗi 1 phần tử sẽ gọi giao dien layout_item_sv
        adapterStudents = new MyListAdapter(this, arrayListStudents);//gán data mảng vào adapter mà mình custom
        //b2
        listViewStudents.setAdapter(adapterStudents);

        //Check hoặc không check trên listView
        //Bắt sự kiện cho listview để chọn và lấy thông tin sinh viên
        listViewStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox itemCheckBox=(CheckBox)view.findViewById(R.id.sinhvien_list_item_checkbox);
                boolean checkboxChecked=false;
                if(itemCheckBox.isChecked()) { //Đã check rồi thì không check nữa.
                    itemCheckBox.setChecked(false);
                    checkboxChecked=false;
                }else {
                    itemCheckBox.setChecked(true);
                    checkboxChecked=true;
                }

                //Khi mình chọn check sẽ lấy thông tin sinh viên
                SinhVien sv=new SinhVien();
                sv.setMasv(arrayListStudents.get(position).getMasv());
                sv.setTensv(arrayListStudents.get(position).getTensv());
                sv.setLop(arrayListStudents.get(position).getLop());
                sv.setGt(arrayListStudents.get(position).getGt());
                sv.setImage(arrayListStudents.get(position).getImage());

                addCheckListItem(sv,checkboxChecked);
            }
        });
    }

    //Check sinh viên có trong mảng
    private void addCheckListItem(SinhVien student,boolean add){
        if(StudentCheckItemList!=null){
            boolean accountExist=false;
            int existPosition=-1;
            int size=StudentCheckItemList.size();// kích thước hiện tại
            for(int i=0;i<size;i++){
                SinhVien sv=StudentCheckItemList.get(i); //sv đã có trong mảng đã chọn
                if(sv.getMasv().equals(student.getMasv())){
                    accountExist=true;
                    existPosition=i;
                    break;

                }
            }
            if(add){  //add có bằng true ( item đang checked)
                if(!accountExist){ //accountExist=false
                    StudentCheckItemList.add(student);//chính là mà được chọn
                }
            }else {  //add =false (item bỏ checked
                if(accountExist){
                    if(existPosition!=-1){
                        StudentCheckItemList.remove(existPosition);
                    }
                }
            }
        }

    }

    //interface thì lớp con chắc chắn phải viết lại hàm của interfer
    //abstract class thì lớp con có thể viết code lại hay không là tùy
    @Override //Thêm action bar vào các Activity
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.item_action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Bắt sự kiện các nút trên actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId(); //Trả về id của item. Mỗi item có 1 mã số.
        if(itemId==R.id.menu_add){
            Intent in=new Intent(getApplicationContext(),InforSVActivity.class);
            in.putExtra("Flag","add");//sửa
            startActivity(in);

            //Xóa hàng loạt phần tử trên listView
        }else if(itemId==R.id.menu_delete){ //Xóa khi nhấn trên icon Actionbar
            if(StudentCheckItemList!=null){
                int size=StudentCheckItemList.size();//trả về kích thước thật sự
                if(size==0){
                    Toast.makeText(getApplicationContext(), "chọn ít nhất 1 phần tử để xóa", Toast.LENGTH_SHORT).show();

                }else {
                    for(int i=0;i<size;i++){
                        SinhVien sv=StudentCheckItemList.get(i);
                        Integer delete=mydb.deleteStudent(sv.getMasv());
                        if(delete>0){ //xóa thành công
                            StudentCheckItemList.remove(i); //Xóa trong csdl khi nhấn icon trên actionbar
                            size=StudentCheckItemList.size();
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "Xóa bị lỗi", Toast.LENGTH_SHORT).show();
                        }
                        i--;
                    }

                    //đã xóa
                    arrayListStudents.clear();
                    //cập nhật dữ liệu từ database đổ lên listview
                    //Hiện thị list sv sau khi xóa
                    Cursor cursor =mydb.showDataStudents();
                    while(cursor.moveToNext()){
                        SinhVien sv=new SinhVien(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
                        arrayListStudents.add(sv);

                    }
                    adapterStudents=new MyListAdapter(this,arrayListStudents);
                    listViewStudents.setAdapter(adapterStudents);
                    //outsource

                }
            }
        }else if(itemId==R.id.menu_edit){
            if(StudentCheckItemList!=null) {
                int size=StudentCheckItemList.size();//trả về kích thước thật sự
                if(size!=1){
                    Toast.makeText(getApplicationContext(), "Chỉ chọn 1 phần tử để sửa", Toast.LENGTH_SHORT).show();
                }else {  //size=1
                    Intent in = new Intent(getApplicationContext(), InforSVActivity.class);
                    in.putExtra("Flag", "edit");//sửa
                    in.putExtra("MASV", StudentCheckItemList.get(0).getMasv());
                    in.putExtra("TENSV", StudentCheckItemList.get(0).getTensv());
                    in.putExtra("GT", StudentCheckItemList.get(0).getGt());
                    in.putExtra("LOP", StudentCheckItemList.get(0).getLop());
                    in.putExtra("ANHSV", StudentCheckItemList.get(0).getImage());
                    startActivity(in);
                }
            }
        }else{ //Chạy về logout
            editor=pref.edit(); //chỉnh sửa file  MYPREFS.xml
            editor.remove("USERNAME");
            editor.remove("PASSWORD");
            editor.commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}