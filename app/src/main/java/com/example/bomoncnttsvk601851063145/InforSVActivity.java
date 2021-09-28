package com.example.bomoncnttsvk601851063145;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bomoncnttsvk601851063145.helper.Common;
import com.example.bomoncnttsvk601851063145.helper.DatabaseHelper;
import com.example.bomoncnttsvk601851063145.model.SinhVien;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;

public class InforSVActivity extends AppCompatActivity {
    EditText txtIdStudent, txtNameStudent;
    Button btnSave, btnReset;
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    Spinner spinnerClass;

    CircleImageView croppedImageView; //Ảnh
    ArrayList<String> arrayListClass;
    ArrayAdapter<String> adapterClass;
    DatabaseHelper mydb;
    Bitmap myBitmap; //Mã hóa ảnh theo số chữ, sau đó chuyển sang string
    Uri picUri; //Đường dẫn của ảnh từ bộ nhớ điện thoại
    String className="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_s_v);

        mapView();
        mydb=new DatabaseHelper(this);

        arrayListClass=new ArrayList<>();
        arrayListClass.add("TH_K59");
        arrayListClass.add("TH1_K60");
        arrayListClass.add("TH2_K60");

        adapterClass=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayListClass);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapterClass);

        //bắt sự kiện spinner
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                className=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                className="";
            }
        });


        //Bắt sự kiện image_profile-------------------------------------------------------------------
        croppedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Gọi cấp cấp quyền ra
                Dexter.withContext(InforSVActivity.this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() { //alow , deny

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){ //Tất cả các quyền được đồng ý
                            //Gọi thư viện Image Picker
                            ImagePicker.with(InforSVActivity.this) //Lấy ảnh đại diện từ chỗ chụp trực tiếp
                                    .crop()	    			//Crop image(Optional), Check Customization for more option
                                    .compress(100)			//Final image size will be less than 1 MB(Optional) Nén file
                                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                                    .start();
                        }else {
                            Toast.makeText(getApplicationContext(), "Bạn đã từ chỗi cấp quyền", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }

                }).check();
            }
        });

        //Đọc dữ liệu mà cái bên MainActivity gửi qua
        Intent in=getIntent();
        String flag=in.getStringExtra("Flag"); //Gắn cờ
        if(flag.equals("add")) {
            setTitle("Thêm dữ liệu");
        }else if(flag.equals("edit")){
            setTitle("Sửa dữ liệu");
            String masv = in.getStringExtra("MASV");
            String tensv = in.getStringExtra("TENSV");
            String gt = in.getStringExtra("GT");
            String lops = in.getStringExtra("LOP");
            String anhsv = in.getStringExtra("ANHSV");
            txtIdStudent.setEnabled(false);
            txtIdStudent.setText(masv);
            txtNameStudent.setText(tensv);
            //sét thêm giới tính
            //sét thêm lớp
            //set giới tính và lop ra spinner
            if(gt.equalsIgnoreCase("Nam")){
                radioSexButton=(RadioButton)findViewById(R.id.radioButtonNam);
                radioSexGroup.check(radioSexButton.getId());
            }else{
                radioSexButton=(RadioButton)findViewById(R.id.radioButtonNu);
                radioSexGroup.check(radioSexButton.getId());
            }
            selectValueSpinner(spinnerClass, lops);
            croppedImageView.setImageBitmap(Common.StringToBitMap(anhsv));
        }

        //bắt sự kiện nút lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("add")) {
                    String masv = txtIdStudent.getText().toString();
                    String tensv = txtNameStudent.getText().toString();
                    int selectedId = radioSexGroup.getCheckedRadioButtonId();
                    radioSexButton = findViewById(selectedId);
                    String gt = radioSexButton.getText().toString();
                    String anhsv="";

                    if(myBitmap!=null) { //Có hình rồi
                        anhsv = Common.BitMapToString(myBitmap); //Đã chọn anh thì chuyển từ bitmap sang string để chuyển vào database
                    }

                    SinhVien sv=new SinhVien(masv, tensv, gt, className,anhsv); //Khởi tạo sv mới với dử liệu người dùng nhập vào
                    boolean inserted = mydb.insertDataStudent(sv); //Nhận 1 đối tượng csdl
                    if (inserted) {
                        Toast.makeText(getApplicationContext(), "Insert thành công", Toast.LENGTH_SHORT).show();
                        finish();//đóng cửa sổ(activity) hiện hành
                        Intent st = new Intent(getApplicationContext(), MainActivity.class);
                        st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(st);

                    } else {
                        Toast.makeText(getApplicationContext(), "Insert không thành công", Toast.LENGTH_SHORT).show();

                    }
                }else //sửa
                {
                    String masv = txtIdStudent.getText().toString();
                    String tensv = txtNameStudent.getText().toString();
                    int selectedId = radioSexGroup.getCheckedRadioButtonId();
                    radioSexButton =findViewById(selectedId);
                    String gt = radioSexButton.getText().toString();
                    // myBitmap
                    //convert Bitmap sang chuoi va nguoc lai
                    String anhsv=in.getStringExtra("ANHSV");
                    if(myBitmap!=null) {
                        anhsv = Common.BitMapToString(myBitmap);

                    }
                    SinhVien sv=new SinhVien(masv,tensv,gt,className,anhsv);
                    boolean updated=mydb.updateStudent(sv);
                    if (updated) {
                        Toast.makeText(getApplicationContext(), "Update thành công", Toast.LENGTH_SHORT).show();
                        finish();//đóng cửa sổ(activity) hiện hành
                        Intent st = new Intent(getApplicationContext(), MainActivity.class);
                        st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(st);

                    } else {
                        Toast.makeText(getApplicationContext(), "Update Không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        //Bắt sự kiện nút reset
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView(flag);
            }
        });
    }

    private void mapView() {
        txtIdStudent=findViewById(R.id.txtmasv);
        txtNameStudent=findViewById(R.id.txttensv);
        btnSave=findViewById(R.id.btnluu);
        btnReset=findViewById(R.id.btnlamlai);
        radioSexGroup=findViewById(R.id.radiogroupsex);
        spinnerClass=findViewById(R.id.spinnerlop);
        croppedImageView=findViewById(R.id.img_profile);
    }

    //Cờ để nhận diện đang thao tác ở đâu.
    private void resetView(String flag){
        txtNameStudent.setText("");
        radioSexButton=findViewById(R.id.radioButtonNam);
        radioSexGroup.check(radioSexButton.getId());
        selectValueSpinner(spinnerClass,arrayListClass.get(0));

        if (flag.equals("add")){
            txtIdStudent.setText("");
            txtIdStudent.requestFocus();
        }
    }

    //Hiện thị lại thông tin của Lớp khi
    private void selectValueSpinner(Spinner spinner, String name) {
        //set selected spinner value.
        int spinnerPosition = adapterClass.getPosition(name);
        spinner.setSelection(spinnerPosition);
    }

    //GỌi menu add iamge
    //icon trên acctionbarr
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_image_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Bắt sự kiện cho các icon---------------------------------
        int itemId = item.getItemId();
        if (itemId == R.id.menu_camera) { //Khi bấm váo camera phải hỏi quyền
            //Gọi cấp cấp quyền ra
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() { //alow , deny

                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if(report.areAllPermissionsGranted()){ //Tất cả các quyền được đồng ý
                                //Gọi thư viện Image Picker
                                ImagePicker.with(InforSVActivity.this)
                                        .crop()	    			//Crop image(Optional), Check Customization for more option
                                        .compress(100)			//Final image size will be less than 1 MB(Optional) Nén file
                                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                                        .start();
                            }else {
                                Toast.makeText(getApplicationContext(), "Bạn đã từ chỗi cấp quyền", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        }

            }).check();

        }
        return super.onOptionsItemSelected(item);
    }

    //Xử lý ảnh, lấy đường dẫn file ảnh chuyển sang bitmap--------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) { //Lưu trữ camera khi chụp
            //Khi chọn ảnh sẽ có 1 data. Lưu đường dẫn vào picUri và sẽ chuyển vào bimap
            if (data.getData() != null) {
                picUri =data.getData(); //Lấy file ảnh
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);//hình ảnh trong thư mục mà chụp
                    croppedImageView.setImageBitmap(myBitmap);
                    //  imageView.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                bitmap = (Bitmap) data.getExtras().get("data");

                myBitmap = bitmap;

                if (croppedImageView != null) {
                    croppedImageView.setImageBitmap(myBitmap);
                }

                // imageView.setImageBitmap(myBitmap);

            }

        }

    }
}