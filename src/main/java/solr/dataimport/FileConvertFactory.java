package solr.dataimport;

import java.io.InputStream;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 09:42
 */

public class FileConvertFactory {
    public String fileCovertText(InputStream fileStream,String fileName){
        String[] fileChips = fileName.split("\\.");
        if(fileChips.length<=1)return null;
        String fileType = fileChips[fileChips.length-1];
        switch (fileType.toLowerCase()){
            case "pdf":
                PdfConvertText pdfConvertText = new PdfConvertText();
                return pdfConvertText.fileConvertText(fileStream,null);
            case "docx":
            case "doc":
            case "xls":
            case "xlsx":
                OfficeFileConvertText officeFileConvertText = new OfficeFileConvertText();
                return officeFileConvertText.fileConvertText(fileStream,fileType);
            default:
                return null;
        }
    }

    /*public static void main(String[] args) {
        FileConvertFactory fileConvertFactory = new FileConvertFactory();
        File path = new File("G:\\game");
        File[] files = path.listFiles();
        //String[] filelist = path.list();
        for(File file : files){
            String fileName = file.getName();
            String[] fileType = fileName.split("\\.");
            try {
                InputStream fileStream = new FileInputStream(file);
                System.out.println(fileConvertFactory.fileCovertText(fileStream,fileType[fileType.length-1]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
}
