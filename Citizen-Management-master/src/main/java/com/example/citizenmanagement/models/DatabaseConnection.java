package com.example.citizenmanagement.models;


import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        String dbName = "QUANLYDANCU";
        String dbUser = "postgres";
        String dbPassword = "Maianh1010?";

        String url = "jdbc:postgresql://localhost:5432/" + dbName;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (Exception e) {
            System.out.println("Lỗi xảy ra khi kết nối đến cơ sở dữ liệu.");
            throw new RuntimeException(e);
        }
    }

    private ResultSet executeQuery(String query) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query);
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return resultSet;
    }
    private void executeUpdate(String query) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    private int executeUpdate1(String query) {
//        Statement statement;
//        int result;
//        try {
//            statement = connection.createStatement();
//            result = statement.executeUpdate(query);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return result;
//    }
    /******************************************************************************************/
    // Citizen Manager Section - Phần Đăng Nhập
    public ResultSet getCitizenManagerData(String tenDangNhap, String matKhau) {

        String query = "SELECT * FROM NGUOIQUANLY\n" +
                "WHERE TENDANGNHAP = '" + tenDangNhap + "' AND MATKHAU = '" + matKhau +"'";
        return executeQuery(query);
    }

    public ResultSet checkCitizenManagerUsernameExisted(String tenDangNhap) {
        String query = "SELECT * FROM NGUOIQUANLY\n" +
                "WHERE TENDANGNHAP = '" + tenDangNhap + "'";
        return executeQuery(query);
    }

    public ResultSet checkCitizenManagerAccountExisted(String hoTen, String tenDangNhap, String soDienThoai, boolean vaiTro) {
        String query = "SELECT * FROM NGUOIQUANLY\n" +
                "WHERE HOTEN = '" + hoTen + "' AND TENDANGNHAP = '" + tenDangNhap + "' AND SODIENTHOAI = '" + soDienThoai + "' AND VAITRO = '" + vaiTro + "'";
        return executeQuery(query);
    }
    public void updateCitizenManagerAccountPassword(String hoTen, String tenDangNhap, String soDienThoai, boolean vaiTro, String maKhau) {
        String query = "UPDATE NGUOIQUANLY SET MATKHAU = '" + maKhau + "' \n" +
                "WHERE HOTEN = '" + hoTen+ "' AND TENDANGNHAP = '" + tenDangNhap + "' AND SODIENTHOAI = '" + soDienThoai + "' AND VAITRO = '" + vaiTro + "'";
        executeUpdate(query);
    }
    public void setCitizenManagerData(String hoTen, String tenDangNhap, String matKhau, String soDienThoai, boolean vaiTro) {
        String query = "INSERT INTO NGUOIQUANLY(HOTEN, TENDANGNHAP, MATKHAU, SODIENTHOAI, VAITRO)\n" +
                        "VALUES ('" + hoTen + "', '" + tenDangNhap + "', '" + matKhau + "', '" + soDienThoai + "', "+ Boolean.toString(vaiTro)+ ")";

        executeUpdate(query);
    }

    /**************************************************************************************/
    // trang chủ - thống kê quản lý dân cư
    public ResultSet getNumberOfTamTru(int nam){
        String query = "SELECT COUNT(MAGIAYTAMTRU) FROM TAMTRU " +
                "WHERE " + nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }
    public ResultSet getNumberOfTamVang(int nam){
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG " +
                "WHERE "+ nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";

        //String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE " + nam + " BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY)";
        return executeQuery(query);
    }
    public ResultSet getNumberOfNhanhKhau() {
        String query = "select count(MANHANKHAU) from NHANKHAU";
        return executeQuery(query);
    }

    public ResultSet getNumberOfHoKhau(){
        String query = "select count(MAHOKHAU) from HOKHAU";
        return executeQuery(query);
    }

    public ResultSet getNumberOfTamTru(){
        String query = "SELECT COUNT(MAGIAYTAMTRU) FROM TAMTRU WHERE date_part('year', current_date) BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        //String query = "SELECT COUNT(MAGIAYTAMTRU) FROM TAMTRU WHERE YEAR(GETDATE()) BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY) ";
        return executeQuery(query);
    }
    public ResultSet getNumberOfTamVang(){
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE date_part('year', current_date) BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        //String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE YEAR(GETDATE()) BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY) ";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauNam(){
        String query = "select count(MANHANKHAU) from NHANKHAU where GIOITINH = true";
        return executeQuery(query);
    }
    public ResultSet getNumberOfNhanKhauNu(){
        String query = "select count(MANHANKHAU) from NHANKHAU where GIOITINH = false";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauDuoi3Tuoi() {
        String query = "SELECT COUNT(MANHANKHAU) " +
                "FROM NHANKHAU " +
                "WHERE date_part('year', current_date) - date_part('year', NGAYSINH) < 3 AND date_part('year', current_date) - date_part('year', NGAYSINH) >= 0";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauTu3Den10Tuoi() {
        String query = "SELECT COUNT(MANHANKHAU) " +
                "FROM NHANKHAU " +
                "WHERE date_part('year', current_date) - date_part('year', NGAYSINH) >= 3 AND date_part('year', current_date) - date_part('year', NGAYSINH) < 10";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauTu10Den18Tuoi() {
        String query = "SELECT COUNT(MANHANKHAU) " +
                "FROM NHANKHAU " +
                "WHERE date_part('year', current_date) - date_part('year', NGAYSINH) >= 10 AND date_part('year', current_date) - date_part('year', NGAYSINH) < 18";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauTu18Den60Tuoi() {
        String query = "SELECT COUNT(MANHANKHAU) " +
                "FROM NHANKHAU " +
                "WHERE date_part('year', current_date) - date_part('year', NGAYSINH) >= 18 AND date_part('year', current_date) - date_part('year', NGAYSINH) < 60";
        return executeQuery(query);
    }

    public ResultSet getNumberOfNhanKhauTren60Tuoi() {
        String query = "SELECT COUNT(MANHANKHAU) " +
                "FROM NHANKHAU " +
                "WHERE date_part('year', current_date) - date_part('year', NGAYSINH) >= 60";
        return executeQuery(query);
    }

    public ResultSet getNamHienTai() {
        String query = "SELECT date_part('year', current_date)";
        return executeQuery(query);
    }


//    public ResultSet getNumberOfNhanKhauDuoi3Tuoi(){
//        String query = "select count(MANHANKHAU) \n" +
//                "from NHANKHAU\n" +
//                "where YEAR(GETDATE()) - YEAR(NGAYSINH) < 3 AND YEAR(GETDATE()) - YEAR(NGAYSINH) >= 0";
//        return executeQuery(query);
//    }
//    public ResultSet getNumberOfNhanKhauTu3Den10Tuoi(){
//        String query = "select count(MANHANKHAU) \n" +
//                "from NHANKHAU\n" +
//                "where YEAR(GETDATE()) - YEAR(NGAYSINH) >= 3 AND YEAR(GETDATE()) - YEAR(NGAYSINH) < 10";
//        return executeQuery(query);
//    }
//
//    public ResultSet getNumberOfNhanKhauTu10Den18Tuoi(){
//        String query = "select count(MANHANKHAU) \n" +
//                "from NHANKHAU\n" +
//                "where YEAR(GETDATE()) - YEAR(NGAYSINH) >= 10 AND YEAR(GETDATE()) - YEAR(NGAYSINH) < 18";
//        return executeQuery(query);
//    }
//
//    public ResultSet getNumberOfNhanKhauTu18Den60Tuoi(){
//        String query = "select count(MANHANKHAU) \n" +
//                "from NHANKHAU\n" +
//                "where YEAR(GETDATE()) - YEAR(NGAYSINH) >= 18 AND YEAR(GETDATE()) - YEAR(NGAYSINH) < 60";
//        return executeQuery(query);
//    }
//    public ResultSet getNumberOfNhanKhauTren60Tuoi(){
//        String query = "select count(MANHANKHAU) \n" +
//                "from NHANKHAU\n" +
//                "where YEAR(GETDATE()) - YEAR(NGAYSINH) >= 60";
//        return executeQuery(query);
//    }
//    public ResultSet getNamHienTai(){
//        String query = "select YEAR(GETDATE())";
//        return executeQuery(query);
 //   }
    public ResultSet getHoKhauOfNamHienTai(){
        String query = "SELECT COUNT(MAHOKHAU)\n" +
                "FROM HOKHAU";
        return executeQuery(query);
    }

    public ResultSet getHoKhauOfNam(int nam){
        String query = "SELECT COUNT(MAHOKHAU)\n" +
                "FROM HOKHAU\n" +
                "WHERE " + nam + " > date_part('year', NGAYTAO)";
        return executeQuery(query);
    }

    public ResultSet getTamTruOfThangVaNam(int thang,int nam){
        String query = "SELECT COUNT(MAGIAYTAMTRU)\n" +
                "FROM TAMTRU\n" +
                "WHERE " + thang + " BETWEEN date_part('month', TUNGAY) AND date_part('month', DENNGAY)" + "\n" +
                "AND " + nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }

    public ResultSet getTamTruViLyDoHocTap(int nam){
        String query = "SELECT COUNT(MAGIAYTAMTRU)\n" +
                "FROM TAMTRU\n" +
                "WHERE LYDO LIKE '%Học tập%' AND " + nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
       return executeQuery(query);
    }
    public ResultSet getTamTruViLyDoLamViec(int nam){
        String query = "SELECT COUNT(MAGIAYTAMTRU)\n" +
                "FROM TAMTRU\n" +
                "WHERE LYDO LIKE '%Làm việc%' AND " + nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }

    public ResultSet getTamTruViLyDoSucKhoe(int nam){
        String query = "SELECT COUNT(MAGIAYTAMTRU)\n" +
                "FROM TAMTRU\n" +
                "WHERE LYDO LIKE '%sức khỏe%' AND " + nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }

    public ResultSet getTamVangOfThangVaNam(int thang, int nam) {
        String query = "SELECT COUNT(MAGIAYTAMVANG)\n" +
                "FROM TAMVANG\n" +
                "WHERE (date_part('year', TUNGAY) = " + nam + " AND date_part('month', TUNGAY) <= " + thang + ") OR " +
                "(date_part('year', DENNGAY) = " + nam + " AND date_part('month', DENNGAY) >= " + thang + ") OR " +
                "(date_part('year', TUNGAY) < " + nam + " AND date_part('year', DENNGAY) > " + nam + ")";

        return executeQuery(query);
    }

    public ResultSet getTamVangViLyDoHocTap(int nam) {
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE LYDO LIKE '%Học tập%' AND " +
                nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";

        return executeQuery(query);
    }

    public ResultSet getTamVangViLyDoLamViec(int nam) {
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE LYDO LIKE '%Làm việc%' AND " +
                nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }

    public ResultSet getTamVangViLyDoSucKhoe(int nam) {
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE LYDO LIKE '%sức Khoẻ%' AND " +
                nam + " BETWEEN date_part('year', TUNGAY) AND date_part('year', DENNGAY)";
        return executeQuery(query);
    }


//    public ResultSet getTamVangOfThangVaNam(int thang,int nam){
//        String query = "SELECT COUNT(MAGIAYTAMVANG)\n" +
//                "FROM TAMVANG\n" +
//                "WHERE (YEAR(TUNGAY) = " + nam + " AND MONTH(TUNGAY) <= " + thang + ") OR (YEAR(DENNGAY) = " + nam + " AND MONTH(DENNGAY) >= " + thang + ")\n" +
//                "\tOR (YEAR(TUNGAY) < " + nam + " AND YEAR(DENNGAY) > " + nam + ")";
//
//        return executeQuery(query);
//    }
//
//    public ResultSet getTamVangViLyDoHocTap(int nam){
//        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE LYDO LIKE N'%Học tập%' AND " + nam + " BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY)";
//
//        return executeQuery(query);
//    }
//
//    public ResultSet getTamVangViLyDoLamViec(int nam){
//        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG   WHERE LYDO LIKE N'%Làm việc%' AND " + nam + " BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY)";
//        return executeQuery(query);
//    }
//
//    public ResultSet getTamVangViLyDoSucKhoe(int nam){
//        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG  WHERE LYDO LIKE N'%sức Khoẻ%' AND " + nam + " BETWEEN YEAR(TUNGAY) AND YEAR(DENNGAY)";
//        return executeQuery(query);
//    }



    //Nhân khẩu
    public int addNhanKhau (String hoTen, String CCCD, String ngaySinh, boolean gioiTinh, String noiSinh, String nguyenQuan,String danToc, String tonGiao, String quocTich, String noiThuongTru, String ngheNghiep, String ghiChu ){
        int thanhcong = 0;
        String querry = "insert into NHANKHAU (HOTEN, SOCANCUOC, NGAYSINH, GIOITINH, NOISINH, NGUYENQUAN, DANTOC, TONGIAO, QUOCTICH, NOITHUONGTRU, NGHENGHIEP, NGAYTAO, GHICHU ) " +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement pre = connection.prepareStatement(querry);
            pre.setString(1,hoTen); pre.setString(2,CCCD);
            pre.setDate(3, java.sql.Date.valueOf(ngaySinh));
            pre.setBoolean(4,gioiTinh);
            pre.setString(5,noiSinh); pre.setString(6,nguyenQuan);
            pre.setString(7,danToc); pre.setString(8,tonGiao);
            pre.setString(9,quocTich);
            pre.setString(10,noiThuongTru); pre.setString(11,ngheNghiep);
            pre.setDate(12, Date.valueOf(LocalDate.now().toString())); pre.setString(13,ghiChu);
            thanhcong = pre.executeUpdate();
        }
        catch(SQLException e) {
            System.out.println("Lỗi thêm nhân khẩu");
            throw new RuntimeException(e);
        }
        return thanhcong;
    }

//    public int addTamtru(String hoTen, String CCCD, String ngaySinh, int gioiTinh, String noiSinh, String nguyenQuan, String danToc, String tonGiao, String quocTich, String noiThuongTru, String ngheNghiep, String sdt, Date ngayDen, Date ngayDi, String liDo ) {
//        int thanhcong = 0;
//        String que = "SELECT INSERT_TAMTRU ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try{
//
//            PreparedStatement pre = connection.prepareStatement(que);
//            pre.setString(1,hoTen); pre.setString(2,CCCD);
//            pre.setString(3, ngaySinh); pre.setInt(4,gioiTinh);
//            pre.setString(5,noiSinh); pre.setString(6,nguyenQuan);
//            pre.setString(7,danToc); pre.setString(8,tonGiao);
//            pre.setString(9,quocTich);
//            pre.setString(10,noiThuongTru); pre.setString(11,ngheNghiep);
//            pre.setString(12, sdt);
//            pre.setDate(13, ngayDen); pre.setDate(14,ngayDi);
//            pre.setString(15,liDo);
//            thanhcong = pre.executeUpdate();
//        }catch(Exception e) {
//            System.out.println("Lỗi thêm nhân khẩu");
//            throw new RuntimeException(e);
//        }
//
//        return thanhcong;
//    }

    public int addTamtru(String hoTen, String CCCD, String ngaySinh, boolean gioiTinh, String noiSinh, String nguyenQuan, String danToc, String tonGiao, String quocTich, String noiThuongTru, String ngheNghiep, String sdt, Date ngayDen, Date ngayDi, String liDo) {
        int thanhcong = 0;
        try {
            // Thêm vào bảng NHANKHAU
            String queryNhanKhau = "INSERT INTO NHANKHAU (HOTEN, SOCANCUOC, NGAYSINH, GIOITINH, NOISINH, NGUYENQUAN, DANTOC, TONGIAO, QUOCTICH, NOITHUONGTRU, NGHENGHIEP, GHICHU, NGAYTAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preNhanKhau = connection.prepareStatement(queryNhanKhau, Statement.RETURN_GENERATED_KEYS);
            preNhanKhau.setString(1, hoTen);
            preNhanKhau.setString(2, CCCD);
            preNhanKhau.setDate(3, java.sql.Date.valueOf(ngaySinh));
            preNhanKhau.setBoolean(4, gioiTinh);
            preNhanKhau.setString(5, noiSinh);
            preNhanKhau.setString(6, nguyenQuan);
            preNhanKhau.setString(7, danToc);
            preNhanKhau.setString(8, tonGiao);
            preNhanKhau.setString(9, quocTich);
            preNhanKhau.setString(10, noiThuongTru);
            preNhanKhau.setString(11, ngheNghiep);
            preNhanKhau.setString(12, "tạm trú");
            LocalDate today = LocalDate.now();
            Date currentDate = Date.valueOf(today);

            // Truyền ngày hôm nay vào câu lệnh SQL
            preNhanKhau.setDate(13, currentDate);

            thanhcong = preNhanKhau.executeUpdate();

            // Lấy MANHANKHAU được tạo tự động
            ResultSet rs = preNhanKhau.getGeneratedKeys();
            int manhankhau = -1;
            if (rs.next()) {
                manhankhau = rs.getInt(1);
            }

            // Thêm vào bảng TAMTRU
            if (manhankhau != -1) {
                String queryTamTru = "INSERT INTO TAMTRU (MANHANKHAU, SODIENTHOAINGUOIDANGKY, TUNGAY, DENNGAY, LYDO) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preTamTru = connection.prepareStatement(queryTamTru);
                preTamTru.setInt(1, manhankhau);
                preTamTru.setString(2, sdt);
                preTamTru.setDate(3, ngayDen);
                preTamTru.setDate(4, ngayDi);
                preTamTru.setString(5, liDo);

                thanhcong = preTamTru.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Lỗi thêm nhân khẩu và tạm trú");
            e.printStackTrace();
        }

        return thanhcong;
    }


    public int addKhaitu(String maNguoiKhai, String maNguoiMat, Date ngayMat, String liDo) {
        int thanhcong = 0;
        String que = "INSERT INTO KHAITU (MANHANKHAUNGUOIKHAI, MANHANKHAUNGUOICHET, NGAYKHAI, NGAYCHET, LYDOCHET) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pre = connection.prepareStatement(que);
            pre.setInt(1, Integer.parseInt(maNguoiKhai));
            pre.setInt(2, Integer.parseInt(maNguoiMat));
            pre.setDate(3, Date.valueOf(LocalDate.now()));
            pre.setDate(4, ngayMat);
            pre.setString(5, liDo);
            thanhcong = pre.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi khai tử");
            throw new RuntimeException(e);
        }
        return thanhcong;
    }

    public int capnhatNhanKhau (String string){
        int thanhcong = 0;
        String querry = "update NHANKHAU SET NGAYTAO = ? Where SOCANCUOC = ?";
        try{
            PreparedStatement pre = connection.prepareStatement(querry);
            pre.setDate(1,Date.valueOf(LocalDate.now().toString()));
            pre.setString(2,string);
            thanhcong = pre.executeUpdate();
        }
        catch(SQLException e) {
            System.out.println("Lỗi câpj nhật khẩu");
            throw new RuntimeException(e);
        }
        return thanhcong;
    }

    public int capnhatNhanKhauShow (String hoten, Date ngaysinh, boolean Gioitinh, String noisinh, String nguyenquan, String dantoc, String tongiao, String quoctich, String noithuongtru, String nghenghiep, String ghichu,String manhankhau){
        int thanhcong = 0;
        String querry = "update NHANKHAU SET HOTEN = ? , NGAYSINH = ? , GIOITINH = ? , NOISINH = ? , NGUYENQUAN =? , DANTOC =? , TONGIAO = ? , QUOCTICH =? , NOITHUONGTRU = ? , NGHENGHIEP = ?, GHICHU =? Where CAST(MANHANKHAU AS TEXT) = ?";
        try{
            PreparedStatement pre = connection.prepareStatement(querry);
            pre.setString(1,hoten);
            pre.setDate(2, ngaysinh);
            pre.setBoolean(3,Gioitinh);
            pre.setString(4,noisinh);
            pre.setString(5,nguyenquan);
            pre.setString(6,dantoc);
            pre.setString(7,tongiao);
            pre.setString(8,quoctich);
            pre.setString(9,noithuongtru);
            pre.setString(10,nghenghiep);
            pre.setString(11,ghichu);
            pre.setString(12,manhankhau);
            thanhcong = pre.executeUpdate();
        }
        catch(SQLException e) {
            System.out.println("Lỗi câpj nhật khẩu");
            throw new RuntimeException(e);
        }
        return thanhcong;
    }
    // Nhân khâur
    public ResultSet KiemTraXemMaNhanKhauDaTonTaiTrongTamVang(int manhankhau){

        ResultSet resultSet = null;
        String query = "SELECT COUNT(MANHANKHAU) FROM TAMVANG WHERE MANHANKHAU =" + manhankhau;

        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;

    }

    public void dangKiTamVang(int maNhanKhau, String noiTamTru, String tuNgay, String denNgay, String lyDo) {
        String dangkitamvang = "INSERT INTO TAMVANG(MANHANKHAU, NOITAMTRU, TUNGAY, DENNGAY, LYDO) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(dangkitamvang);
            preparedStatement.setInt(1, maNhanKhau);

            if (noiTamTru.isEmpty())
                preparedStatement.setString(2, null);
            else
                preparedStatement.setString(2, noiTamTru);

            // Chuyển đổi từ String sang java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedTuNgay = dateFormat.parse(tuNgay);
            java.util.Date parsedDenNgay = dateFormat.parse(denNgay);

            preparedStatement.setDate(3, new Date(parsedTuNgay.getTime()));
            preparedStatement.setDate(4, new Date(parsedDenNgay.getTime()));

            preparedStatement.setString(5, lyDo);

            preparedStatement.executeUpdate();
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

//    public void dangKiTamVang(int maNhanKhau, String noiTamTru,String tuNgay, String denNgay,String lyDo){
//        String dangkitamvang = "INSERT INTO TAMVANG(MANHANKHAU, NOITAMTRU, TUNGAY, DENNGAY, LYDO) VALUES (?, ?, ?, ?, ?)";
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(dangkitamvang);
//            preparedStatement.setInt(1,maNhanKhau);
//
//            preparedStatement.setString(3,tuNgay);
//
//            preparedStatement.setString(4,denNgay);
//
//            preparedStatement.setString(5,lyDo);
//
//            if(noiTamTru.isEmpty())
//                preparedStatement.setString(2,null);
//            else
//                preparedStatement.setString(2,noiTamTru);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public ResultSet KiemTraMaNhanKhauCoTonTaiHayKhong(int manhankhau){
        ResultSet resultSet = null;
        String query = "SELECT COUNT(MANHANKHAU) FROM NHANKHAU WHERE MANHANKHAU = " + manhankhau;

        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

    public ResultSet truyvanlistNhanKhau( String manhankhau) {
        ResultSet resultSet = null;
        String que = "SELECT HOTEN, SOCANCUOC, NGAYSINH, GIOITINH, NOISINH, NGUYENQUAN, DANTOC, TONGIAO, QUOCTICH, NOITHUONGTRU, NGHENGHIEP, NGAYTAO, GHICHU FROM NHANKHAU WHERE CAST(MANHANKHAU AS TEXT) = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(que);
            preparedStatement.setString(1,    manhankhau );
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public int xoa_tam_tru(String MaNhanKhau) {
        if(!MaNhanKhau.isEmpty()) {
            String query = "Delete FROM TAMTRU where CAST(MANHANKHAU AS TEXT) = ?";
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, MaNhanKhau);
                statement.executeUpdate();
                return 1;
            } catch (Exception e) {
                return 0;
            }
        }
        else
            return 0;
    }

    public ResultSet nhanKhau_timkiem(String string) {
        ResultSet resultSet = null;
        String querry = " select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU where CAST(MANHANKHAU AS TEXT) like ? or SOCANCUOC like ? or upper(HOTEN) like ?";
        try {
            PreparedStatement preparedstatement = connection.prepareStatement(querry);
            preparedstatement.setString(1, "%" + string + "%");
            preparedstatement.setString(2, "%" + string + "%");
            preparedstatement.setString(3, "%" + string.toUpperCase() + "%");
            resultSet = preparedstatement.executeQuery();
        }
        catch(Exception e) {
            System.out.println("Lỗi tìm kiếm");
            throw new RuntimeException(e);
        }
        return resultSet;
    }
    public ResultSet truyvan() {
        ResultSet resultSet = null;
        String querry = " select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU;";
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(querry);
        }
        catch(Exception e) {

        }
        return resultSet;
    }

    public ResultSet truyvanTamTru() {
        ResultSet resultSet = null;
        String querry = " select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU where GHICHU like '%tạm trú%';";
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(querry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }
    /***********************************************************************************/
    // Hộ khẩu

    public int addHoKhau(String ma_ch, String diachi, String ghichu){
        if(!ma_ch.isEmpty() && !diachi.isEmpty()) {
            String query = "INSERT INTO HOKHAU(IDCHUHO, DIACHI, NGAYTAO, GHICHU) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement statement = connection.prepareStatement(query);

                statement.setInt(1, Integer.parseInt(ma_ch));
                statement.setString(2, diachi);
                statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                //statement.setString(3,LocalDate.now().toString());
                if(ghichu.isEmpty())
                    statement.setString(4,null);
                else
                    statement.setString(4, ghichu);


                statement.executeUpdate();
                System.out.println(query);
                return 1;
            } catch (Exception e) {
                System.out.println("loi o addHoKhau");
                e.printStackTrace();
                return 0;
            }
        }
        else
            return 0;
    }
    public ResultSet getDanhSachHoKhau(){
        String query = "select * from HOKHAU";
        return executeQuery(query);
    }
    public ResultSet timKiem(String dieukien){
        ResultSet resultSet=null;
        String query = "SELECT * FROM HOKHAU\n" +
                "WHERE CAST(MAHOKHAU AS TEXT) LIKE ? OR UPPER(TENCHUHO) LIKE ? OR UPPER(DIACHI) LIKE ?";
        try{
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + dieukien + "%");
            statement.setString(2, "%" + dieukien.toUpperCase() + "%");
            statement.setString(3, "%" + dieukien.toUpperCase() + "%");
            resultSet = statement.executeQuery();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return resultSet;
    }
    public ResultSet lay_ho_khau(String ma_chu_ho){
        String query = "select * from HOKHAU WHERE IDCHUHO = " + ma_chu_ho;
        return executeQuery(query);
    }
    public ResultSet getMaHoKhau(String maChuHo) {
        String query = "Select mahokhau from thanhviencuaho where manhankhau = " + maChuHo;

        return executeQuery(query);
    }
    public int capNhatHoKhau(String idHoKhau, String maChuHo, String diaChi, String ghiChu){

        try {
            String capnhat = "update HOKHAU set IDCHUHO=?, DIACHI=?, GHICHU=?, tenchuho=? where CAST(MAHOKHAU AS TEXT) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(capnhat);
            preparedStatement.setInt(1,Integer.parseInt(maChuHo));
            preparedStatement.setString(2,diaChi);
            if(ghiChu.isEmpty())
                preparedStatement.setString(3,null);
            else
                preparedStatement.setString(3, ghiChu);
            preparedStatement.setString(5,idHoKhau);


            String lay_ten_chu="select * from nhankhau where cast(manhankhau as text)= '"+maChuHo +"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet1 = statement.executeQuery(lay_ten_chu);
            if(resultSet1.isBeforeFirst()){
                resultSet1.next();
                preparedStatement.setString(4,resultSet1.getString(2));
            }
            else {
                preparedStatement.setString(4,null);
            }
            preparedStatement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int xoaHoKhau(String maHoKhau) {
        String query = "DELETE from HOKHAU\n" +
                "WHERE MAHOKHAU = " + maHoKhau;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return  1;
        } catch (SQLException e) {
            return 0;
            //throw new RuntimeException(e);
        }
    }
    public ResultSet getDanhSachTamVang() {
        ResultSet resultSet = null;
        String query= "SELECT * FROM TAMVANG JOIN NHANKHAU USING(MANHANKHAU) ";
        Statement statement;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

    public ResultSet lay_cac_thanh_vien(String ma_ho){
        String query = "select * from THANHVIENCUAHO where MAHOKHAU = "+ma_ho;
        ResultSet resultSet=null;
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }catch (Exception e){
            System.out.println("loi o truy van thanh vien");
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet lay_nhan_khau(String ma_nhan_khau) {
        String query = " select SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU where MANHANKHAU = " + ma_nhan_khau;
        return executeQuery(query);
    }

    public void add_thanh_vien_cua_ho(String maNhanKhau,String ma_ho, String quan_he){
        String query = "INSERT INTO THANHVIENCUAHO VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,Integer.parseInt(maNhanKhau));
            preparedStatement.setInt(2,Integer.parseInt(ma_ho));
            preparedStatement.setString(3,quan_he);
            preparedStatement.executeUpdate();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public void xoa_thanh_vien_cua_ho(String maNhanKhau){
        String query1 = "select * FROM NHANKHAU WHERE CAST(MANHANKHAU AS TEXT) = '" + maNhanKhau + "'";
        try{
            Statement statement1 = connection.createStatement();
            ResultSet resultSet=statement1.executeQuery(query1);
            if(resultSet.isBeforeFirst()) {
                resultSet.next();
                String query = "DELETE FROM THANHVIENCUAHO WHERE CAST(MANHANKHAU AS TEXT) = '"+resultSet.getString(1) + "'";
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
            }
        }catch (Exception e){
            System.out.println("loi o xoa_thanh_vien_cua_ho");
            e.printStackTrace();
        }
    }
    public void xoaThanhVienCuaHo(String maNhanKhau) {
        String query = "DELETE FROM THANHVIENCUAHO WHERE CAST(MANHANKHAU AS TEXT) = '" + maNhanKhau + "'";
        System.out.println("da xoa " + maNhanKhau);
        executeUpdate(query);
    }

    public ResultSet truyvan_chua_co_nha() {
        ResultSet resultSet = null;
        String querry = " select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU WHERE MANHANKHAU NOT IN (SELECT MANHANKHAU FROM THANHVIENCUAHO);";
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(querry);
        }
        catch(Exception e) {
            System.out.println("loi truy van chua co ho khau");
        }
        return resultSet;
    }

    public ResultSet nhanKhau_timkiem_chua_co_nha(String string) {
        ResultSet resultSet = null;
        String querry = " select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU from NHANKHAU where (CAST(MANHANKHAU AS TEXT) like ? or SOCANCUOC like ? or UPPER(HOTEN) like ?) AND MANHANKHAU NOT IN (SELECT MANHANKHAU FROM THANHVIENCUAHO);";
        try {
            PreparedStatement preparedstatement = connection.prepareStatement(querry);
            preparedstatement.setString(1, "%" + string + "%");
            preparedstatement.setString(2, "%" + string + "%");
            preparedstatement.setString(3, "%" + string.toUpperCase() + "%");
            resultSet = preparedstatement.executeQuery();
        }
        catch(Exception e) {
            System.out.println("Lỗi tìm kiếm");
            throw new RuntimeException(e);
        }
        return resultSet;
    }

   public String lay_chu_ho(String ma_ho_khau){
        String query = "select * from HOKHAU WHERE MAHOKHAU="+ma_ho_khau;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery(query);

                if (resultSet.isBeforeFirst()) {
                    resultSet.next();
                    return resultSet.getString(2);
                }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
   }

    /***************************************************************************/
    // Quản lý thu phí
    public ResultSet getDanhSachDongPhi() {
        String query = "SELECT HK.MAHOKHAU, NK.HOTEN, HK.DIACHI, COUNT(TV.MANHANKHAU)\n" +
                "FROM HOKHAU HK INNER JOIN NHANKHAU NK ON HK.IDCHUHO = NK.MANHANKHAU\n" +
                "\tINNER JOIN THANHVIENCUAHO TV ON HK.MAHOKHAU = TV.MAHOKHAU\n" +
                "GROUP BY HK.MAHOKHAU, NK.HOTEN, HK.DIACHI";
        return executeQuery(query);
    }

    public ResultSet danhsachdongphi_timKiem(String condition) {
        String query = "SELECT HK.MAHOKHAU\n" +
                "FROM HOKHAU HK INNER JOIN NHANKHAU NK ON HK.IDCHUHO = NK.MANHANKHAU\n" +
                "INNER JOIN THANHVIENCUAHO TV ON HK.MAHOKHAU = TV.MAHOKHAU\n" +
                "WHERE CAST(HK.MAHOKHAU AS TEXT) LIKE '%" + condition + "%' \n" +
                "\tOR NK.HOTEN LIKE '%" + condition + "%'\n" +
                "\tOR DIACHI LIKE '%" + condition+ "%'\n" +
                "GROUP BY HK.MAHOKHAU, NK.HOTEN, HK.DIACHI";
        return executeQuery(query);
    }
    public void themKhoanThuPhi(String tenKhoanThu, boolean batBuoc, long soTienCanDong, LocalDate ngayTao, String moTa) {
        String query = "INSERT INTO LOAIPHI(TEN, BATBUOC, SOTIENTRENMOTNGUOI, NGAYTAO, MOTA)\n" +
                "VALUES ('" + tenKhoanThu + "', " + batBuoc + ", "+ soTienCanDong + ", '" + ngayTao.toString() + "', '" + moTa +"')";
        System.out.println(query);
        executeUpdate(query);
    }

    public int layMaKhoanThu(String tenKhoanThu,boolean batBuoc, long soTienCanDong, LocalDate ngayTao, String moTa) {
        int maKhoanThu = -1;

        String query = "SELECT MAKHOANTHU\n" +
                "FROM LOAIPHI\n" +
                "WHERE TEN = '" + tenKhoanThu + "' AND BATBUOC = " + batBuoc +
                " AND SOTIENTRENMOTNGUOI = " + soTienCanDong + " AND NGAYTAO = '" + ngayTao.toString() + "' AND MOTA = '" + moTa +"'";
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.isBeforeFirst()){
                resultSet.next();
                maKhoanThu = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return maKhoanThu;
    }
//    public void themDanhSachThuPhi(int maHoKhau, int maKhoanThu, boolean trangThai) {
//        String query = "INSERT_DONGGOP( " + maHoKhau + ", " + maKhoanThu + ", " + trangThai + ")";
//        System.out.println(query);
//        executeUpdate(query);
//    }

    public void themDanhSachThuPhi(int maHoKhau, int maKhoanThu, boolean trangThai) {
        String query = "INSERT INTO DONGGOP(MAHOKHAU, MAKHOANTHU, TRANGTHAI) VALUES ( " + maHoKhau + ", " + maKhoanThu + ", " + trangThai + ")";
        //System.out.println(query);
        executeUpdate(query);
    }


    public ResultSet getDanhSachKhoanThu() {
        String query = "SELECT * FROM LOAIPHI";
        return executeQuery(query);
    }

    public ResultSet getKhoanThuPhi(int maKhoanThu) {
        String query = "SELECT * FROM LOAIPHI\n" +
                "WHERE CAST(MAKHOANTHU AS TEXT) LIKE '%" + maKhoanThu + "%'";
        return executeQuery(query);
    }
    public ResultSet danhSachKhoanThu_timKiem(String condition) {
        String query = "SELECT * FROM LOAIPHI\n" +
                "WHERE CAST(MAKHOANTHU AS TEXT) LIKE '%" + condition + "%' OR TEN LIKE '%" + condition + "%'";
        return executeQuery(query);
    }

    public int getSoLuongHoDaDongPhi(int maKhoanThu) {

        int res = 0;

        String query = "SELECT COUNT(MAHOKHAU) FROM DONGGOP\n" +
                "WHERE MAKHOANTHU = '" + maKhoanThu + "' AND TRANGTHAI = true";
        Statement statement;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int getSoLuongHoChuaDongPhi(int maKhoanThu) {

        int res = 0;
        String query = "SELECT COUNT(MAHOKHAU) FROM DONGGOP\n" +
                "WHERE MAKHOANTHU ='" + maKhoanThu + "' AND TRANGTHAI = false";

//        String query = "SELECT COUNT(MAHOKHAU) FROM DONGGOP\n" +
//                "WHERE MAKHOANTHU LIKE '" + maKhoanThu + "' AND TRANGTHAI = false";
        Statement statement;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int getSoLuongHoDongPhi(int maKhoanThu) {

        int res = 0;

        String query = "SELECT COUNT(MAHOKHAU) FROM DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu;
        Statement statement;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                res = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public void deleteKhoanThuPhi(int maKhoanThu) {
        String query1 = "DELETE FROM DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu;
        String query2 = "DELETE FROM LOAIPHI\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu;

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query1);
            statement.executeUpdate(query2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getTongSoTienDaThuPhi(){
        ResultSet resultSet = null;
        Statement statement;
        String query = "SELECT SUM(SOTIENCANDONG) FROM DONGGOP WHERE TRANGTHAI = true";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }

    public ResultSet getDanhSachChuaDongPhi(int maKhoanThu) {
        String query = "select MAHOKHAU, TENCHUHO, DIACHI, SOTHANHVIEN, SOTIENCANDONG\n" +
                "from DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu + " AND TRANGTHAI = false";
        return executeQuery(query);
    }

    //chưa sửa
    public ResultSet danhSachChuaDongPhi_timKiem(int maKhoanThu, String condition) {
        String query = "select MAHOKHAU, TENCHUHO, DIACHI, SOTHANHVIEN, SOTIENCANDONG\n" +
                "from DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu + " AND TRANGTHAI = false\n" +
                "\tAND (CAST(MAHOKHAU AS TEXT) LIKE '%" + condition + "%' OR UPPER(TENCHUHO) LIKE '%" + condition.toUpperCase() + "%')";
        return executeQuery(query);
    }

    public void updateNopPhi(int maHoKhau, int maKhoanThu, String soTien) {
        String query = "UPDATE DONGGOP\n" +
                "SET TRANGTHAI = true, NGAYDONG = CURRENT_DATE, SOTIENDADONG = " + Integer.valueOf(soTien) + "\n" +
                "WHERE MAHOKHAU = " + maHoKhau + " AND MAKHOANTHU = " + maKhoanThu;
        executeUpdate(query);
    }
    public String getNgayNopPhi(int maHoKhau, int maKhoanThu) {
        String query = "SELECT NGAYDONG FROM DONGGOP\n" +
                "WHERE MAHOKHAU = " + maHoKhau + " AND MAKHOANTHU = " + maKhoanThu;
        String ngayNopPhi = "";
        ResultSet resultSet = executeQuery(query);
        try {
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                ngayNopPhi = resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ngayNopPhi;
    }

    public ResultSet danhSachDaDongPhi_timKiem(int maKhoanThu, String condition) {
        String query = "select MAHOKHAU, TENCHUHO, DIACHI, SOTHANHVIEN, SOTIENDADONG\n" +
                "from DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu + " AND TRANGTHAI = true\n" +
                "\tAND (CAST(MAHOKHAU AS TEXT) LIKE '%" + condition + "%' OR UPPER(TENCHUHO) LIKE '%" + condition.toUpperCase() + "%')";
        return executeQuery(query);
    }

    public ResultSet getDanhSachDaDongPhi(int maKhoanThu) {
        String query = "Select MAHOKHAU, TENCHUHO, DIACHI, SOTHANHVIEN, SOTIENDADONG\n" +
                "from DONGGOP\n" +
                "WHERE MAKHOANTHU = " + maKhoanThu + " AND TRANGTHAI = true";
        return executeQuery(query);
    }

    public ResultSet getDSNguoiChet() {
        String query = "select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU\n" +
                "from NHANKHAU INNER JOIN KHAITU ON NHANKHAU.MANHANKHAU = KHAITU.MANHANKHAUNGUOICHET";

        return executeQuery(query);
    }

    public ResultSet deadNhanKhau_timkiem(String condition) {
        String query = "select MANHANKHAU, SOCANCUOC, HOTEN, GIOITINH, NGAYSINH, NOITHUONGTRU\n" +
                "from NHANKHAU INNER JOIN KHAITU ON NHANKHAU.MANHANKHAU = KHAITU.MANHANKHAUNGUOICHET\n" +
                "WHERE CAST(MANHANKHAU AS TEXT) LIKE '%" + condition + "%' OR SOCANCUOC LIKE '%" + condition + "%' OR UPPER(HOTEN) LIKE '%" + condition.toUpperCase() + "%'";

        return executeQuery(query);
    }
    public ResultSet getThongTinKhaiTu(String maNhanKhauNguoiChet) {
        String query = "SELECT KT.MAGIAYKHAITU, NK1.MANHANKHAU, NK1.HOTEN, NK2.MANHANKHAU, NK2.HOTEN, NK2.SOCANCUOC, NK2.NGAYSINH, NK2.GIOITINH, NK2.DANTOC, NK2.QUOCTICH,\n" +
                "\tNK2.NGUYENQUAN, NK2.NOITHUONGTRU, KT.NGAYKHAI, KT.NGAYCHET, KT.LYDOCHET\n" +
                "FROM KHAITU KT INNER JOIN NHANKHAU NK1 ON KT.MANHANKHAUNGUOIKHAI = NK1.MANHANKHAU\n" +
                "\tINNER JOIN NHANKHAU NK2 ON KT.MANHANKHAUNGUOICHET = NK2.MANHANKHAU\n" +
                "WHERE KT.MANHANKHAUNGUOICHET = " + maNhanKhauNguoiChet;
        return executeQuery(query);
    }

    public void updateThongTinKhaiTu(String maGiayKhaiTu, String ngayKhai, String ngayChet, String lyDo) {
        String query = "UPDATE KHAITU\n" +
                "SET NGAYKHAI = '" + ngayKhai + "', NGAYCHET = '" + ngayChet + "', LYDOCHET = '" + lyDo + "'\n" +
                "WHERE MAGIAYKHAITU = " + maGiayKhaiTu;
        executeUpdate(query);
    }
    /***************************************************************************/

    public ResultSet getNumberOfCacLoaiPhi(){
        ResultSet resultSet = null;
        Statement statement;
        String query = "SELECT COUNT(MAKHOANTHU) FROM LOAIPHI";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }
    public  void xoaTamVang(int magiaytamvang){
        String query = "DELETE FROM TAMVANG  WHERE MAGIAYTAMVANG = " + magiaytamvang;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public int xoaNhanKhau(String soNhanKhau) {
//        String query = "DELETE FROM NHANKHAU WHERE CAST(MANHANKHAU AS TEXT) = ?";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, soNhanKhau);
//
//            return statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    //chưa sửa
    public int xoaNhanKhau(String soNhanKhau) {
        String query = "SELECT DELETE_NHANKHAU(" + Integer.parseInt(soNhanKhau) +")";
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkKhaiTu(String maNhanKhau) {
        String query = "SELECT COUNT(MAGIAYKHAITU) FROM KHAITU WHERE MANHANKHAUNGUOICHET = " + maNhanKhau;
        ResultSet resultSet = executeQuery(query);
        try {
            resultSet.next();
            if (resultSet.getInt(1) == 0) return true; // chua chet
            else return false; // da chet
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkTamVang(String maNhanKhau) {
        String query = "SELECT COUNT(MAGIAYTAMVANG) FROM TAMVANG WHERE MANHANKHAU = " + maNhanKhau;
        ResultSet resultSet = executeQuery(query);
        try {
            resultSet.next();
            if (resultSet.getInt(1) == 0) return true; // chua di tam vang
            else return false;// da da di tam vang
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}


