-- B1:
CREATE DATABASE QUANLYDANCU;
----------------------------

-- B2:
\c QUANLYDANCU

\encoding UTF8

-- B3:
CREATE TABLE NGUOIQUANLY (
    HOTEN VARCHAR(50) NOT NULL,
    TENDANGNHAP VARCHAR(50) NOT NULL UNIQUE,
    MATKHAU VARCHAR(20) NOT NULL,
    SODIENTHOAI CHAR(15) NOT NULL,
    VAITRO BOOLEAN NOT NULL
);

CREATE TABLE NHANKHAU (
    MANHANKHAU SERIAL PRIMARY KEY,
    HOTEN VARCHAR(50) NOT NULL,
    SOCANCUOC VARCHAR(15),
    NGAYSINH DATE NOT NULL,
    GIOITINH BOOLEAN NOT NULL,
    NOISINH VARCHAR(200) NOT NULL,
    NGUYENQUAN VARCHAR(200) NOT NULL,
    DANTOC VARCHAR(20) NOT NULL,
    TONGIAO VARCHAR(20) NOT NULL,
    QUOCTICH VARCHAR(20) NOT NULL,
    NOITHUONGTRU VARCHAR(200),
    NGHENGHIEP VARCHAR(100),
    NGAYTAO DATE,
    GHICHU VARCHAR(200)
);

CREATE TABLE HOKHAU (
    MAHOKHAU SERIAL PRIMARY KEY,
    IDCHUHO INT NOT NULL,
    DIACHI VARCHAR(200) NOT NULL,
    NGAYTAO DATE,
    GHICHU VARCHAR(200),
    TENCHUHO VARCHAR(300),
    FOREIGN KEY (IDCHUHO) REFERENCES NHANKHAU(MANHANKHAU)
);

CREATE TABLE THANHVIENCUAHO (
    MANHANKHAU INT NOT NULL,
    MAHOKHAU INT NOT NULL,
    QUANHEVOICHUHO VARCHAR(100) NOT NULL,
    PRIMARY KEY (MANHANKHAU, MAHOKHAU),
    FOREIGN KEY (MANHANKHAU) REFERENCES NHANKHAU(MANHANKHAU),
    FOREIGN KEY (MAHOKHAU) REFERENCES HOKHAU(MAHOKHAU)
);

--trigger insert hộ khẩu
CREATE OR REPLACE FUNCTION INSERT_HOKHAU() RETURNS TRIGGER AS $$
DECLARE
    V_TENCHUHO VARCHAR(300);
    V_MAHOKHAU INT;
BEGIN

    SELECT MAX(HOKHAU.MAHOKHAU) INTO V_MAHOKHAU FROM HOKHAU;

    INSERT INTO THANHVIENCUAHO(MANHANKHAU, MAHOKHAU, QUANHEVOICHUHO)
    VALUES (NEW.IDCHUHO, V_MAHOKHAU, 'Chủ hộ');

    SELECT HOTEN INTO V_TENCHUHO FROM NHANKHAU WHERE MANHANKHAU = NEW.IDCHUHO;

    UPDATE HOKHAU
    SET TENCHUHO = V_TENCHUHO
    WHERE MAHOKHAU = V_MAHOKHAU;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tf_INSERT_HOKHAU
AFTER INSERT ON HOKHAU
FOR EACH ROW
EXECUTE PROCEDURE INSERT_HOKHAU();


CREATE TABLE TAMTRU (
    MAGIAYTAMTRU SERIAL PRIMARY KEY,
    MANHANKHAU INT NOT NULL,
    SODIENTHOAINGUOIDANGKY VARCHAR(15) NOT NULL,
    TUNGAY DATE NOT NULL,
    DENNGAY DATE NOT NULL,
    LYDO VARCHAR(300),
    FOREIGN KEY (MANHANKHAU) REFERENCES NHANKHAU(MANHANKHAU)
);

CREATE TABLE TAMVANG (
    MAGIAYTAMVANG SERIAL PRIMARY KEY,
    MANHANKHAU INT NOT NULL,
    NOITAMTRU VARCHAR(300) NOT NULL,
    TUNGAY DATE NOT NULL,
    DENNGAY DATE NOT NULL,
    LYDO VARCHAR(300),
    FOREIGN KEY (MANHANKHAU) REFERENCES NHANKHAU(MANHANKHAU)
);

CREATE TABLE KHAITU (
    MAGIAYKHAITU SERIAL PRIMARY KEY,
    MANHANKHAUNGUOIKHAI INT NOT NULL,
    MANHANKHAUNGUOICHET INT NOT NULL,
    NGAYKHAI DATE,
    NGAYCHET DATE,
    LYDOCHET VARCHAR(300),
    FOREIGN KEY (MANHANKHAUNGUOIKHAI) REFERENCES NHANKHAU(MANHANKHAU),
    FOREIGN KEY (MANHANKHAUNGUOICHET) REFERENCES NHANKHAU(MANHANKHAU)
);

ALTER TABLE KHAITU
ADD CONSTRAINT UQ_MANHANKHAUNGUOICHET UNIQUE (MANHANKHAUNGUOICHET);

CREATE OR REPLACE FUNCTION DELETE_NHANKHAU(
    V_MANHANKHAU INT,
    OUT OUTPUT INT
)
RETURNS INT AS
$$
BEGIN
    IF (V_MANHANKHAU NOT IN (SELECT IDCHUHO FROM HOKHAU)) THEN
        DELETE FROM TAMTRU WHERE MANHANKHAU = V_MANHANKHAU;
        DELETE FROM TAMVANG WHERE MANHANKHAU = V_MANHANKHAU;
        DELETE FROM KHAITU WHERE MANHANKHAUNGUOICHET = V_MANHANKHAU;
        DELETE FROM THANHVIENCUAHO WHERE MANHANKHAU = V_MANHANKHAU;
        DELETE FROM NHANKHAU WHERE MANHANKHAU = V_MANHANKHAU;
        OUTPUT := 1;
    ELSE
        OUTPUT := 0;
    END IF;
END;
$$
LANGUAGE plpgsql;

INSERT INTO Nguoiquanly (HOTEN, TENDANGNHAP, MATKHAU, SODIENTHOAI, VAITRO)
VALUES
    ('Nguyễn Tiến Thành', 'thanh', 'thanh', '0123456789', true),
    ('Từ Văn An', 'an', 'an', '0987654321', true),
    ('Lương Trung Kiên', 'kien', 'kien', '0123456788', false),
    ('Quách Đình Dương', 'duong', 'duong', '0122222222', true),
    ('Thiều Văn Dũng', 'dung', 'dung', '999993333', false);


INSERT INTO NHANKHAU (
    HOTEN, 
    SOCANCUOC,
    NGAYSINH, 
    GIOITINH, 
    NOISINH, 
    NGUYENQUAN, 
    DANTOC, 
    TONGIAO, 
    QUOCTICH
)
VALUES 
    ('Nguyễn Tiến Thành','027213504397', '2003-07-04', true, 'Bắc Ninh', 'Bắc Ninh', 'Kinh', 'Không', 'Việt Nam'),
    ('Quách Đình Dương','349871236984', '2003-01-01', true, 'Thái Bình', 'Thái Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Lương Trung Kiên', '567823490817','2003-01-01', true, 'Thái Bình', 'Thái Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Từ Văn An', '129384756209', '2003-01-01', true, 'Bắc Giang', 'Bắc Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Thiều Văn Dũng', '674502983176', '2003-01-01', true, 'Bắc Giang', 'Bắc Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Văn Vegeta', '987654321098', '1995-04-12', true, 'Hà Nội', 'Hà Nội', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thị Gojo', '238756190824', '1998-07-24', false, 'Hải Phòng', 'Hải Phòng', 'Kinh', 'Không', 'Việt Nam'),
    ('Lê Văn Sukuna', '509812347665', '2000-05-04', true, 'Thái Nguyên', 'Thái Nguyên', 'Kinh', 'Không', 'Việt Nam'),
    ('Lê Thị John Wick', '769083451234', '1990-11-06', false, 'Hà Nam', 'Hà Nam', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Văn Neyma', '120938476583', '1988-03-15', true, 'Hải Dương', 'Hải Dương', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thanh Tùng', '498172365094', '1993-09-22', true, 'Thái Bình', 'Thái Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thúc Thùy Tiên', '765890231847', '1996-02-18', false, 'Quảng Ninh', 'Quảng Ninh', 'Kinh', 'Không', 'Việt Nam'),
    ('Lê Văn Rhymastic', '237456890124', '2002-10-30', true, 'Nam Định', 'Nam Định', 'Kinh', 'Không', 'Việt Nam'),
    ('Lê Thị Binz', '654321098765', '1985-08-12', false, 'Hưng Yên', 'Hưng Yên', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Văn Billgate', '890234567123', '1992-06-28', true, 'Vĩnh Phúc', 'Vĩnh Phúc', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Văn Hitman', '345098172634', '1997-04-11', true, 'Phú Thọ', 'Phú Thọ', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Thị chatGPT', '432765098124', '1999-12-27', false, 'Thái Bình', 'Thái Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Hoàng Văn Null', '109823745876', '1989-01-27', true, 'Bắc Kạn', 'Bắc Kạn', 'Kinh', 'Không', 'Việt Nam'),
    ('Hoàng Thị Obama', '876501239284', '2001-09-15', false, 'Hòa Bình', 'Hòa Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Vũ Văn messi', '321654098723', '1987-07-09', true, 'Thanh Hóa', 'Thạch Thành', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Văn Anh', '928734501267', '1995-03-01', true, 'Hà Nội', 'Hải Phòng', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thị Bình', '456789012345', '1998-10-23', false, 'Hải Phòng', 'Hà Nội', 'Tày', 'Không', 'Việt Nam'),
    ('Lê Văn Cường', '102938475601', '2000-07-16', true, 'Thái Nguyên', 'Thái Nguyên', 'Mường', 'Không', 'Việt Nam'),
    ('Lê Thị Dung', '789012345678', '1990-01-01', false, 'Hà Nam', 'Hồ Chí Minh', 'Khơ Mú', 'Công giáo', 'Việt Nam'),
    ('Trần Văn Eo', '234567890123', '1988-01-02', true, 'Hải Dương', 'Thái Bình', 'H''Mông', 'Không', 'Việt Nam'),
    ('Phạm Thị Lan', '567890123456', '1993-01-03', false, 'Quảng Ninh', 'Bình Dương', 'Hoà Bình', 'Buddhist', 'Việt Nam'),
    ('Trịnh Văn Thành', '890123456789', '1987-01-04', true, 'Nghệ An', 'Hà Giang', 'Thái', 'Protestant', 'Việt Nam'),
    ('Hoàng Thị Linh', '123456789012', '2002-01-05', false, 'Quảng Bình', 'Hà Nội', 'Khmer', 'Không', 'Việt Nam'),
    ('Nguyễn Văn Hùng', '345678901234', '1991-01-06', true, 'Hà Tĩnh', 'Tiền Giang', 'Tày', 'Buddhist', 'Việt Nam'),
    ('Trần Thị Kim', '901234567890', '1995-01-07', false, 'Vĩnh Phúc', 'Phú Thọ', 'Mường', 'Protestant', 'Việt Nam'),
    ('Nguyễn Văn white', NULL, '2012-09-10', true, 'Thái Bình', 'Hải Phòng', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thị batman', NULL, '2011-07-15', false, 'Nam Định', 'Hà Nội', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Văn superman', '987654321012', '1998-03-20', true, 'Hải Phòng', 'Hải Dương', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Thị spiderman', NULL, '2013-06-05', false, 'Hưng Yên', 'Hưng Yên', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Văn wonder woman', '876543210123', '1995-11-25', true, 'Hải Dương', 'Thái Bình', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Thị songoku', NULL, '2014-01-18', false, 'Quảng Ninh', 'Bình Dương', 'Kinh', 'Không', 'Việt Nam'),
    ('Trịnh Văn ronaldo', '765432109234', '1992-08-15', true, 'Nghệ An', 'Hà Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Trịnh Thị luffy', NULL, '2010-12-03', false, 'Nghệ An', 'Hà Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Hoàng Văn Ill', '654321098345', '1999-05-22', true, 'Quảng Bình', 'Hà Nội', 'Kinh', 'Không', 'Việt Nam'),
    ('Hoàng Thị Kante', NULL, '2012-11-08', false, 'Quảng Bình', 'Hà Nội', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Văn Linda', '543210987456', '1997-04-27', true, 'Hà Tĩnh', 'Tiền Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Nguyễn Thị Mane', NULL, '2010-08-12', false, 'Hà Tĩnh', 'Tiền Giang', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Văn None', '432109876567', '1994-03-30', true, 'Vĩnh Phúc', 'Phú Thọ', 'Kinh', 'Không', 'Việt Nam'),
    ('Trần Thị Puka', NULL, '2012-10-25', false, 'Vĩnh Phúc', 'Phú Thọ', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Văn Qi', '321098765678', '1991-08-05', true, 'Thanh Hóa', 'Thạch Thành', 'Kinh', 'Không', 'Việt Nam'),
    ('Phạm Thị Su', NULL, '2013-03-15', false, 'Thanh Hóa', 'Thạch Thành', 'Kinh', 'Không', 'Việt Nam'),
    ('Lê Văn Zoro', '210987654789', '1996-01-01', true, 'Hà Giang', 'Nam Định', 'Kinh', 'Không', 'Việt Nam'),
    ('người được chọn', '000000000', '2000-01-01', true, 'Thái Bình', 'Thái Bình', 'lksd', 'Không', 'Việt Nam'),
    ('Nguyễn Văn Poe', '000000001', '2021-01-01', true, 'Lạng Sơn', 'Sơn La', 'bot', 'Không', 'Việt Nam');



--Thêm tạm trú
CREATE OR REPLACE FUNCTION INSERT_TAMTRU (
    P_HOTEN VARCHAR(50), 
    P_SOCANCUOC VARCHAR(15), 
    P_NGAYSINH DATE, 
    P_GIOITINH BOOLEAN, 
    P_NOISINH VARCHAR(200), 
    P_NGUYENQUAN VARCHAR(200),
    P_DANTOC VARCHAR(20), 
    P_TONGIAO VARCHAR(20), 
    P_QUOCTICH VARCHAR(20), 
    P_NOITHUONGTRU VARCHAR(200), 
    P_NGHENGHIEP VARCHAR(100),
    P_SODIENTHOAINGUOIDANGKY VARCHAR(15), 
    P_TUNGAY DATE, 
    P_DENNGAY DATE, 
    P_LYDO VARCHAR(300)
) RETURNS VOID AS $$
DECLARE
    V_MANHANKHAU INT;
BEGIN
    INSERT INTO NHANKHAU (
        HOTEN, 
        SOCANCUOC, 
        NGAYSINH, 
        GIOITINH, 
        NOISINH, 
        NGUYENQUAN, 
        DANTOC, 
        TONGIAO, 
        QUOCTICH, 
        NOITHUONGTRU, 
        NGHENGHIEP, 
        GHICHU
    )
    VALUES (
        P_HOTEN, 
        P_SOCANCUOC, 
        P_NGAYSINH, 
        P_GIOITINH, 
        P_NOISINH, 
        P_NGUYENQUAN, 
        P_DANTOC, 
        P_TONGIAO, 
        P_QUOCTICH, 
        P_NOITHUONGTRU, 
        P_NGHENGHIEP, 
        'tạm trú'
    )
    RETURNING MANHANKHAU INTO V_MANHANKHAU;

    INSERT INTO TAMTRU (MANHANKHAU, SODIENTHOAINGUOIDANGKY, TUNGAY, DENNGAY, LYDO)
    VALUES (V_MANHANKHAU, P_SODIENTHOAINGUOIDANGKY, P_TUNGAY, P_DENNGAY, P_LYDO);
END;
$$ LANGUAGE plpgsql;



--trigger xóa tạm trú
CREATE OR REPLACE FUNCTION DELETE_TAMTRU_FUNCTION() RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM NHANKHAU
    WHERE MANHANKHAU = OLD.MANHANKHAU;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER DELETE_TAMTRU
AFTER DELETE ON TAMTRU
FOR EACH ROW
EXECUTE PROCEDURE DELETE_TAMTRU_FUNCTION();


-- Gọi hàm cho mỗi dòng dữ liệu
SELECT INSERT_TAMTRU(
    'Nguyễn Văn ACE', '123456789', '1990-01-01', true, 'Hà Nội', 'Hà Nội', 'Kinh', 'Phật giáo', 'Việt Nam', 'Hà Nội', 'Kỹ sư', '0123456789', '2022-01-01', '2023-12-31', 'Học tập'
);

SELECT INSERT_TAMTRU(
    'Trần Thị Sabo', '987654321', '1985-01-01', false, 'Hải Phòng', 'Hải Phòng', 'Kinh', 'Phật giáo', 'Việt Nam', 'Hải Phòng', 'Giáo viên', '0987654321', '2023-02-01', '2023-11-30', 'Công tác'
);

SELECT INSERT_TAMTRU(
    'Lê Văn Dragon', '456789012', '1995-01-01', true, 'Thái Nguyên', 'Thái Nguyên', 'Mông', 'Đạo Cao Đài', 'Việt Nam', 'Thái Nguyên', 'Y sĩ', '0123456789', '2021-03-01', '2023-10-31', 'Chăm sóc sức khỏe'
);

SELECT INSERT_TAMTRU(
    'Phạm Thị Garp', '789012345', '2000-01-01', false, 'Hồ Chí Minh', 'Hồ Chí Minh', 'Khơ Mú', 'Tin Lành', 'Việt Nam', 'Hồ Chí Minh', 'Sinh viên', '0987654321', '2023-04-01', '2023-09-30', 'Học tập'
);

SELECT INSERT_TAMTRU(
    'Hoàng Văn Sanji', '012345678', '1988-01-01', true, 'Hải Dương', 'Hải Dương', 'Kinh', 'Đạo Cao Đài', 'Việt Nam', 'Hải Dương', 'Nhân viên văn phòng', '0123456789', '2020-05-01', '2023-08-31', 'Làm việc'
);

SELECT INSERT_TAMTRU(
    'Trần Thị Chopper', '1234567890', '1992-01-01', false, 'Hải Dương', 'Hải Dương', 'Kinh', 'Đạo Cao Đài', 'Việt Nam', 'Hải Dương', 'Giáo viên', '0123456789', '2020-06-01', '2023-11-30', 'Chăm sóc sức khỏe'
);

SELECT INSERT_TAMTRU(
    'Nguyễn Thị Pikachu', '0987654321', '1980-01-01', false, 'Bắc Ninh', 'Bắc Ninh', 'Kinh', 'Phật giáo', 'Việt Nam', 'Bắc Ninh', 'Nhân viên kinh doanh', '0987654321', '2022-07-01', '2023-10-31', 'Làm việc'
);

SELECT INSERT_TAMTRU(
    'Lê Thị Hinata', '1122334455', '1987-01-01', false, 'Hải Phòng', 'Hải Phòng', 'Kinh', 'Tin Lành', 'Việt Nam', 'Hải Phòng', 'Trình dược viên', '0123456789', '2019-08-01', '2023-09-30', 'Học tập'
);

SELECT INSERT_TAMTRU(
    'Phạm Văn Naruto', '5566778899', '1998-01-01', true, 'Thái Bình', 'Thái Bình', 'Kinh', 'Đạo Cao Đài', 'Việt Nam', 'Thái Bình', 'Kỹ thuật viên', '0123456789', '2023-09-01', '2023-08-31', 'Làm việc'
);

SELECT INSERT_TAMTRU(
    'Uchihahaha Sasuke', '0011223344', '1982-01-01', false, 'Hà Nội', 'Hà Nội', 'Kinh', 'Phật giáo', 'Việt Nam', 'Hà Nội', 'Bác sĩ', '0987654321', '2023-10-01', '2023-07-31', 'Chăm sóc sức khỏe'
);


-- Trigger cho việc chèn dữ liệu vào bảng TAMVANG
CREATE OR REPLACE FUNCTION INSERT_TAMVANG_TRIGGER()
RETURNS TRIGGER AS $$
BEGIN
    -- Cập nhật trường GHICHU của NHANKHAU thành 'tạm vắng'
    UPDATE NHANKHAU
    SET GHICHU = 'tạm vắng'
    WHERE MANHANKHAU = NEW.MANHANKHAU;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Kích hoạt trigger khi có sự chèn dữ liệu vào bảng TAMVANG
CREATE TRIGGER INSERT_TAMVANG
AFTER INSERT ON TAMVANG
FOR EACH ROW
EXECUTE PROCEDURE INSERT_TAMVANG_TRIGGER();

-- Trigger cho việc xóa dữ liệu khỏi bảng TAMVANG
CREATE OR REPLACE FUNCTION DELETE_TAMVANG_TRIGGER()
RETURNS TRIGGER AS $$
BEGIN
    -- DECLARE
    --     MANHANKHAU_VALUE INT;
    BEGIN
        
        UPDATE NHANKHAU
        SET GHICHU = NULL
        WHERE MANHANKHAU = OLD.MANHANKHAU;
    END;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER DELETE_TAMVANG
AFTER DELETE ON TAMVANG
FOR EACH ROW
EXECUTE PROCEDURE DELETE_TAMVANG_TRIGGER();


-- Tạo stored procedure
CREATE OR REPLACE FUNCTION INSERT_TAM_VANG(
    P_MANHANKHAU_PARAM INT,
    P_NOITAMTRU_PARAM VARCHAR(300),
    P_TUNGAY_PARAM DATE,
    P_DENNGAY_PARAM DATE,
    P_LYDO_PARAM VARCHAR(300)
) RETURNS VOID AS $$
BEGIN
    -- Thêm dữ liệu vào bảng TAMVANG
    INSERT INTO TAMVANG(MANHANKHAU, NOITAMTRU, TUNGAY, DENNGAY, LYDO)
    VALUES (P_MANHANKHAU_PARAM, P_NOITAMTRU_PARAM, P_TUNGAY_PARAM, P_DENNGAY_PARAM, P_LYDO_PARAM);

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION INSERT_KHAITU_TRIGGER_FUNCTION()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE NHANKHAU
    SET GHICHU = 'qua đời'
    WHERE MANHANKHAU = NEW.MANHANKHAUNGUOICHET;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER INSERT_KHAITU
AFTER INSERT
ON KHAITU
FOR EACH ROW
EXECUTE PROCEDURE INSERT_KHAITU_TRIGGER_FUNCTION();

-------------------------database cho đóng phí ---------------------------

CREATE TABLE LOAIPHI (
    MAKHOANTHU SERIAL PRIMARY KEY,
    TEN VARCHAR(50) NOT NULL,
    BATBUOC BOOLEAN NOT NULL,
    SOTIENTRENMOTNGUOI BIGINT NOT NULL,
    NGAYTAO DATE,
    MOTA VARCHAR(300)
);

CREATE TABLE DONGGOP (
    MAHOKHAU INT,
    MAKHOANTHU INT,
    SOTIENCANDONG BIGINT,
    SOTIENDADONG BIGINT,
    TRANGTHAI BOOLEAN NOT NULL,
    NGAYDONG DATE,
    TENCHUHO VARCHAR(300),
    DIACHI VARCHAR(300),
    SOTHANHVIEN INT,
    FOREIGN KEY (MAKHOANTHU) REFERENCES LOAIPHI(MAKHOANTHU)
);

INSERT INTO LOAIPHI(TEN, BATBUOC, SOTIENTRENMOTNGUOI, MOTA)
VALUES 
    ('Thu phí vệ sinh', true, 120000, 'Thu phí vệ sinh năm 2023'),
    ('Đóng góp phí thương binh liệt sĩ 27/7', true, 50000, 'Thu phí đóng góp thương binh liệt sĩ 27/7, trừ mấy hộ có thương binh liệt sĩ'),
    ('Đóng góp chiến dịch Xuân Yêu Thương', false, 0, 'Chiến dịch Xuân Yêu Thương diễn ra tại tỉnh A, ...');



--trigger cho việc insert đóng góp vào 
CREATE OR REPLACE FUNCTION INSERT_DONGGOP () RETURNS TRIGGER AS $$
DECLARE
    V_TENCHUHO VARCHAR(300);
    V_DIACHI VARCHAR(300);
    V_SOTHANHVIEN INT;
    V_SOTIENTRENMOTNGUOI BIGINT;
BEGIN
    SELECT SOTIENTRENMOTNGUOI INTO V_SOTIENTRENMOTNGUOI
    FROM LOAIPHI 
    WHERE MAKHOANTHU = NEW.MAKHOANTHU;

    SELECT TENCHUHO, DIACHI INTO V_TENCHUHO, V_DIACHI
    FROM HOKHAU 
    WHERE MAHOKHAU = NEW.MAHOKHAU;

    SELECT COUNT(MANHANKHAU) INTO V_SOTHANHVIEN
    FROM THANHVIENCUAHO 
    WHERE MAHOKHAU = NEW.MAHOKHAU;

    UPDATE DONGGOP
    SET TENCHUHO = V_TENCHUHO,
        DIACHI = V_DIACHI,
        SOTHANHVIEN = V_SOTHANHVIEN,
        SOTIENCANDONG = V_SOTIENTRENMOTNGUOI * V_SOTHANHVIEN
    WHERE MAKHOANTHU = NEW.MAKHOANTHU AND MAHOKHAU = NEW.MAHOKHAU;

    IF NEW.TRANGTHAI THEN
        UPDATE DONGGOP
        SET SOTIENDADONG = NEW.SOTIENCANDONG
        WHERE MAKHOANTHU = NEW.MAKHOANTHU AND MAHOKHAU = NEW.MAHOKHAU;
    ELSE
        UPDATE DONGGOP
        SET SOTIENDADONG = 0
        WHERE MAKHOANTHU = NEW.MAKHOANTHU AND MAHOKHAU = NEW.MAHOKHAU;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER tf_INSERT_DONGGOP
AFTER INSERT
ON DONGGOP
FOR EACH ROW
EXECUTE PROCEDURE INSERT_DONGGOP();




-- Gọi stored procedure không có giá trị trả về

INSERT INTO tamvang values (1, 50, 'Bß║»c Ninh', '2023-12-1', '2023-12-20');

INSERT INTO khaitu values (1, 50, 51, '2023-12-1', '2023-11-1', 'bi benh');