public class MedicineUtil {
  private String[] medType = {"ยาเม็ด", "ยาแคปซูล", "ยาน้ำ", "ยาแบบฉีด"};
  private String[] medColor = {"ขาว", "ใส", "น้ำเงิน", "เขียว", "เหลือง", "ชมพู", "ส้ม", "น้ำตาล", "ไม่ระบุ"};
  private String[] medTime = {"เช้า", "กลางวัน", "เย็น", "ก่อนนอน"};
  private String[] medAmountStr = {"ก่อนอาหาร", "หลังอาหาร", "พร้อมอาหาร/หลังอาหารทันที"};

  public String[] getMedType() {
    return medType;
  }

  public String[] getMedColor() {
    return medColor;
  }

  public String[] getMedTime() {
    return medTime;
  }

  public String[] getMedAmountStr() {
    return medAmountStr;
  }
}
