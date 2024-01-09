package cn.keking.utils;

import cn.keking.config.ConfigConstants;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ConvertPicUtil {

    private static final int FIT_WIDTH = 500;
    private static final int FIT_HEIGHT = 900;
    private final static Logger logger = LoggerFactory.getLogger(ConvertPicUtil.class);
    private final static String fileDir = ConfigConstants.getFileDir();
    /**
     * Tif 转  JPG。
     *
     * @param strInputFile  输入文件的路径和文件名
     * @param strOutputFile 输出文件的路径和文件名
     * @return boolean 是否转换成功
     */
    public static List<String> convertTif2Jpg(String strInputFile, String strOutputFile, boolean forceUpdatedCache) throws Exception {
        List<String> listImageFiles = new ArrayList<>();
        return listImageFiles;
    }

    /**
     * 将Jpg图片转换为Pdf文件
     *
     * @param strJpgFile 输入的jpg的路径和文件名
     * @param strPdfFile 输出的pdf的路径和文件名
     */
    public static String convertJpg2Pdf(String strJpgFile, String strPdfFile) throws Exception {
        Document document = new Document();
        RandomAccessFileOrArray rafa = null;
        FileOutputStream outputStream = null;
        try {
            RandomAccessFile aFile = new RandomAccessFile(strJpgFile, "r");
            FileChannel inChannel = aFile.getChannel();
            FileChannelRandomAccessSource fcra =  new FileChannelRandomAccessSource(inChannel);
            rafa = new RandomAccessFileOrArray(fcra);
            int pages = TiffImage.getNumberOfPages(rafa);
            outputStream = new FileOutputStream(strPdfFile);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Image image;
            for (int i = 1; i <= pages; i++) {
             image = TiffImage.getTiffImage(rafa, i);
             image.scaleToFit(FIT_WIDTH, FIT_HEIGHT);
             document.add(image);
            }
        } catch (IOException e) {
            if (!e.getMessage().contains("Bad endianness tag (not 0x4949 or 0x4d4d)") ) {
                logger.error("TIF转JPG异常，文件路径：" + strPdfFile, e);
            }
            throw new Exception(e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (rafa != null) {
                rafa.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return strPdfFile;
    }
}
