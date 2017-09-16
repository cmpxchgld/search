package solr.dataimport;

import java.io.InputStream;

public interface FileConvert {
	public String fileConvertText(InputStream fileStream, String fileType);
}
