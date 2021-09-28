package com.example.bomoncnttsvk601851063145.helper;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bomoncnttsvk601851063145.R;
import com.example.bomoncnttsvk601851063145.model.SinhVien;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyListAdapter extends ArrayAdapter<SinhVien> {

    private final Activity context;
    private final ArrayList<SinhVien> listsv;
    //Lớp tỉnh
    //ViewHolder tất cả thông tin của 1 Item
    private static class ViewHolder {
        TextView txtMasv;
        TextView txtTensv;
        TextView txtgt;
        TextView txtlop;
        CircleImageView imageStudents;
    }

    public MyListAdapter(Activity context, ArrayList<SinhVien> data) {
        super(context, R.layout.layout_item_sv, data);
        // TODO Auto-generated constructor stub

        this.context=context;
        //List sinhviên chứa 3 Object vừa truyền qua.
        this.listsv = data;
        Log.d("listsv",""+listsv);

    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        SinhVien dataModel = getItem(position); //lấy Object (masv, tensv, gt, lop )
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) { //Biến convertView kiểm tra các View hiển thị chưa. Nếu chưa thì get nó ra.
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_item_sv, parent, false);
            //Ánh xạ
            viewHolder.txtMasv =  convertView.findViewById(R.id.item_txtmasv);
            viewHolder.txtTensv =  convertView.findViewById(R.id.item_txttensv);
            viewHolder.txtgt=convertView.findViewById(R.id.item_txtgt);
            viewHolder.txtlop=convertView.findViewById(R.id.item_txtlop);
            viewHolder.imageStudents=convertView.findViewById(R.id.imageViewsv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtMasv.setText(dataModel.getMasv());
        viewHolder.txtTensv.setText(dataModel.getTensv());
        viewHolder.txtgt.setText(dataModel.getGt());
        viewHolder.txtlop.setText(dataModel.getLop());
        String imageStudentString=dataModel.getImage();
        viewHolder.imageStudents.setImageBitmap(Common.StringToBitMap(imageStudentString));

        return convertView;

    };
}