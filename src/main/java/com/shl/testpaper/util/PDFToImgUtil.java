package com.shl.testpaper.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PDFToImgUtil {

    private static Logger logger = LoggerFactory.getLogger(PDFToImgUtil.class);


    /**
     * 获取PDF总页数
     * @throws IOException
     */
    public static int getPDFNum(String fileUrl) throws IOException {
        PDDocument pdDocument = null;
        int pages = 0;
        try {
            pdDocument = getPDDocument(fileUrl);
            pages = pdDocument.getNumberOfPages();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        } finally {
            if (pdDocument != null) {
                pdDocument.close();
            }
        }
        return pages;
    }


    /**
     * PDF转图片 根据页码一页一页转
     * @throws IOException
     * imgType:转换后的图片类型 jpg,png
     */
    public static void PDFToImg(OutputStream sos, String fileUrl, int page, String imgType) throws IOException {
        PDDocument pdDocument = null;
        /* dpi越大转换后越清晰，相对转换速度越慢 */
        int dpi = 300;
        try {
            pdDocument = getPDDocument(fileUrl);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            int pages = pdDocument.getNumberOfPages();
//            && page > 0
            if (page <= pages) {
                BufferedImage image = renderer.renderImageWithDPI(page,dpi);
                ImageIO.write(image, imgType, sos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        } finally {
            if (pdDocument != null) {
                pdDocument.close();
            }
        }

    }


    private static PDDocument getPDDocument(String fileUrl) throws IOException {
        File file = new File(fileUrl);
        FileInputStream inputStream = new FileInputStream(file);
        return PDDocument.load(inputStream);
    }

//    public static void main(String[] args) {
//        String pdfPath = "/users/long/Desktop/110.pdf";
//        String outputPath = "/users/long/Desktop/";
//        try {
//            int totalPage = getPDFNum(pdfPath);
//            logger.debug("总页数：" + totalPage);
//            FileOutputStream fos = null;
//            for (int index = 0; index < totalPage; index++) {
//                fos = new FileOutputStream(outputPath + "110_" + index + ".jpg");
//                PDFToImg(fos, pdfPath, index, "jpg");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void splitImage(File pic, int rows, int cols, String outputPath, String outputName) throws IOException {
        // 加载图片
        FileInputStream fis = new FileInputStream(pic);
        BufferedImage image = ImageIO.read(fis);

        int chunks = rows * cols;

        // 计算每个小图的宽度和高度
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //设置小图的大小和类型
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                //写入图像内容
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0,
                        chunkWidth, chunkHeight,
                        chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth,
                        chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }

        //输出小图
        for (int i = 0, iLen = imgs.length; i < iLen; i++) {
            ImageIO.write(imgs[i], "jpg", new File(outputPath + outputName + "-" + i + ".jpg"));
        }
    }

    public static void main(String[] args) {
        String imgPath = "/users/long/Desktop/110_0.jpg";
        File pic = new File(imgPath);
        try {
//            BufferedImage sourceImg = ImageIO.read(new FileInputStream(pic));
//            System.out.println("width="+ sourceImg.getWidth());
//            System.out.println("height=" + sourceImg.getHeight());
//            int imgWidth = sourceImg.getWidth();
//            int imgHeight = sourceImg.getHeight();

            splitImage(pic, 1, 2, "/users/long/Desktop/images/", "img110");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
