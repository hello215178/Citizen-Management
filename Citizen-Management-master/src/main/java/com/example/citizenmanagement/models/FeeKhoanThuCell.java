package com.example.citizenmanagement.models;

import javafx.beans.property.*;

public class FeeKhoanThuCell {
    private final IntegerProperty maKhoanThu = new SimpleIntegerProperty();
    private final StringProperty tenKhoanThu = new SimpleStringProperty();
    private final BooleanProperty batBuoc = new SimpleBooleanProperty();
    private final LongProperty soTienCanDong = new SimpleLongProperty();
    private final StringProperty ngayTao = new SimpleStringProperty();

    public FeeKhoanThuCell() {
    }
    public FeeKhoanThuCell(int maKhoanThu, String tenKhoanThu, boolean batBuoc, long soTienCanDong, String ngayTao) {
        this.maKhoanThu.setValue(maKhoanThu);
        this.tenKhoanThu.setValue(tenKhoanThu);
        this.batBuoc.setValue(batBuoc);
        this.soTienCanDong.setValue(soTienCanDong);
        this.ngayTao.setValue(ngayTao);
    }

    public int getMaKhoanThu() {
        return maKhoanThu.get();
    }

    public IntegerProperty maKhoanThuProperty() {
        return maKhoanThu;
    }

    public void setMaKhoanThu(int maKhoanThu) {
        this.maKhoanThu.set(maKhoanThu);
    }

    public String getTenKhoanThu() {
        return tenKhoanThu.get();
    }

    public StringProperty tenKhoanThuProperty() {
        return tenKhoanThu;
    }

    public void setTenKhoanThu(String tenKhoanThu) {
        this.tenKhoanThu.set(tenKhoanThu);
    }

    public boolean getBatBuoc() {
        return batBuoc.get();
    }

    public BooleanProperty batBuocProperty() {
        return batBuoc;
    }

    public void setBatBuoc(boolean batBuoc) {
        this.batBuoc.set(batBuoc);
    }

    public long getSoTienCanDong() {
        return soTienCanDong.get();
    }

    public LongProperty soTienCanDongProperty() {
        return soTienCanDong;
    }

    public void setSoTienCanDong(int soTienCanDong) {
        this.soTienCanDong.set(soTienCanDong);
    }

    public String getNgayTao() {
        return ngayTao.get();
    }

    public StringProperty ngayTaoProperty() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao.set(ngayTao);
    }
}
