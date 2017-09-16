package solr.bean;

import java.util.List;

/**
 * The type Q SolrUser.
 *
 * @author 890211
 * @version 1.0
 */
public class SolrUserResult {
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    long total;
    List<SolrUser> rows;
    String msg;
    int retcode;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<SolrUser> getRows() {
        return rows;
    }

    public void setRows(List<SolrUser> rows) {
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

}
