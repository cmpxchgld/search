package solr.dataimport;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 09:16
 */

public class PdfConvertText implements FileConvert {
    public String fileConvertText(InputStream fileStream, String fileType) {
        try {
            PDFParser parser = new PDFParser(new RandomAccessBuffer(fileStream));
            parser.parse();
            PDDocument document = parser.getPDDocument();
            // 获取页码
            int pages = document.getNumberOfPages();
            // 读文本内容
            PDFTextStripper text=new PDFTextStripper();
            // 设置按顺序输出
            text.setSortByPosition(false);
            text.setStartPage(1);
            text.setEndPage(pages);
            String content = text.getText(document);
            document.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
