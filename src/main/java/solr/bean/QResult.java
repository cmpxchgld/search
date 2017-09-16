package solr.bean;

import java.util.List;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 15:23
 */

public class QResult {
    public static final int SUCCESS=1;
    public static final int FAILURE=0;
    long total;
    List<Doc> rows;
    String msg;
    int retcode;
    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Doc> getRows() {
        return rows;
    }

    public void setRows(List<Doc> rows) {
        this.rows = rows;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public class Doc {
        String title;
        String site;
        String label;
        String content;
        String author;
        String size;
        String url;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
