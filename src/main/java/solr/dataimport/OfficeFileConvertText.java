package solr.dataimport;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 08:36
 */

public class OfficeFileConvertText implements FileConvert {
    public String fileConvertText(InputStream fileStream, String fileType) {
        switch (fileType.toLowerCase()){
            case "doc":
                return docConvertText(fileStream);
            case "docx":
                return docxConvertText(fileStream);
            default:
                return null;
        }
    }
    private String docConvertText(InputStream fileStream){
        try {
            HWPFDocument doc = new HWPFDocument(fileStream);
            StringBuilder content = doc.getText();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String docxConvertText(InputStream fileStream){
        try {
            XWPFDocument docx = new XWPFDocument(fileStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
            return extractor.getText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    private String excelConvertText(Workbook workbook){

        return null;
    }

}
