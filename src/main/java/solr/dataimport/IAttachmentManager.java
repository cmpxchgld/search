package solr.dataimport;

import java.io.File;
import java.util.Map;

public interface IAttachmentManager {
	Map<String, Object> file2img(File file);
	void callBack2ReceiveNotify(String id, int status, String args);
}
