import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import sun.misc.BASE64Encoder;

/**
 * 二维码工具类
 *
 * @author gu
 */
public class QrCodeUtils {

    /**
     * 黑色
     */
    private static final int BLACK = 0xFF000000;
    /**
     * 白色
     */
    private static final int WHITE = 0xFFFFFFFF;
    /**
     * 宽
     */
    private static final int WIDTH = 3000;
    /**
     * 高
     */
    private static final int HEIGHT = 3000;

    /**
     *
     * 图片高度增加60
     *
     */
    private static final int PIC_HEIGHT=HEIGHT+300;

    /**
     * 二维码传图片
     *
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        BufferedImage image = new BufferedImage(width, PIC_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < PIC_HEIGHT; y++) {
                image.setRGB(x, y,WHITE);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 生成二维码
     *
     * @param content
     *            扫描二维码的内容
     * @param format
     *            图片格式 jpg
     *            文件
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static BufferedImage generateQrCode(String content, String format) throws Exception {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        @SuppressWarnings("rawtypes")
        Map hints = new HashMap();
        // 设置UTF-8， 防止中文乱码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 设置二维码四周白色区域的大小
        hints.put(EncodeHintType.MARGIN, 1);
        // 设置二维码的容错性
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 画二维码
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
        BufferedImage image = toBufferedImage(bitMatrix);
        return image;
    }


    /**
     * 把生成的图片写到本地磁盘
     *
     * @param qrcFile 路径
     * @param qrCodeContent 二维码内容
     * @param pressText 增加的文字
     * @throws Exception
     */
    public static void generateQrCode(File qrcFile,String qrCodeContent,String pressText) throws Exception {


        BufferedImage image=generateQrCode(qrCodeContent, "jpg");

        Graphics g = image.getGraphics();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        //设置字体
        Font font=new Font("宋体", Font.BOLD, 300);
        g.setFont(font);
        g.setColor(Color.black);
        FontMetrics metrics = g.getFontMetrics(font);
        // 文字在图片中的坐标 这里设置在中间
        int startX = (WIDTH - metrics.stringWidth(pressText)) / 2;
        int startY=HEIGHT+(PIC_HEIGHT-HEIGHT)/2 + 100 ;
        g.drawString(pressText, startX, startY);

        g.dispose();
        image.flush();
        try {
            ImageIO.write(image, "jpg",qrcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /**
     *
     * 生成二维码并使用Base64编码
     *
     *
     * @param content 二维码内容
     *
     * @return 返回base64图片
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String getBase64QRCode(String content) throws Exception {


        String format = "png";

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        @SuppressWarnings("rawtypes")
        Map hints = new HashMap();

        // 设置二维码四周白色区域的大小
        hints.put(EncodeHintType.MARGIN, 1);
        // 设置二维码的容错性
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 画二维码
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream os = new ByteArrayOutputStream();//新建流。
        ImageIO.write(image, format, os);//利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
        byte b[] = os.toByteArray();//从流中获取数据数组。
        String base64String  = new BASE64Encoder().encode(b);

        // Base64编码
        return base64String;

    }


    /**
     * test
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Date d1 = df.parse("2018-01-02 00:00:00");
//
//
//        String str = df.format(d1);
//        System.out.println(d1);
//        System.out.println(str);
//

        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下
        File file = new File("F:/1.xls");
        List<String> list = new ArrayList<>();

        list = readExcel(file);

        for (int i = 1;i < list.size();i++){
            String qrCodeContent="http://wxofficial.lyghxny.com/order" + "?gunId=" + list.get(i);
            String pressText=qrCodeContent.substring(qrCodeContent.length()-20);
            File qrcFile = new File("f:/"+pressText + ".png");
            generateQrCode(qrcFile, qrCodeContent, pressText);
        }
//



//        String carvinNew = new String(hex2Byte("4C39474342463647384832303031333338"));
//        System.out.println(carvinNew);

//        String str = "4,4,4,4,4,4,4,4,4,4,4,4,4,4,3,3,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1,1,1,1,2,2,2,2,4,4";
//        String [] str1 = str.split(",");
//        int sum = 0;
//        for ( int  i= 0; i< str1.length;i++){
//            sum += Integer.parseInt(str1[i]);
//            System.out.println(""  + (i+1) + "  " + str1[i]);
//        }
//        System.out.println(sum);






    }

    /**
     * 2018/7/30
     * @param file
     * @return
     */
    public static List<String> readExcel(File file) {

        List<String> list = new ArrayList<>();
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                System.out.println(sheet);
                for (int i = 0; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
//                    for (int j = 0; j < sheet.getColumns(); j++) {
//                        String cellinfo = sheet.getCell(j, i).getContents();
//                        System.out.println(cellinfo);
//                    }
                    String cellinfo = sheet.getCell(0, i).getContents();
                    System.out.println(cellinfo);
                    list.add(cellinfo);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;


    }

    public static byte[] hex2Byte(String str) {
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
                //byte temp = (byte) (b[i / 2] & 0xff);
                //b[i / 2] = temp;
            }
				/*StringBuffer test = new StringBuffer();
				for(int i = 0;i < b.length;i ++){
					test.append(b[i] + ",");
				}
				System.out.println(test);*/
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
