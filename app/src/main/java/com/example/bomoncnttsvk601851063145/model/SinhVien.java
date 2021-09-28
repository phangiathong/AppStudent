package com.example.bomoncnttsvk601851063145.model;

public class SinhVien {

    private  String masv;
    private String tensv;
    private String gt;
    private String lop;
    private String image;

    public SinhVien() {
    }



    public SinhVien(String masv, String tensv, String gt, String lop, String image) {
        this.masv = masv;
        this.tensv = tensv;
        this.gt = gt;
        this.lop = lop;
        this.image = image;
    }

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getTensv() {
        return tensv;
    }

    public void setTensv(String tensv) {
        this.tensv = tensv;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return masv+"-"+tensv+"-"+gt+"-"+lop;
    }
}
