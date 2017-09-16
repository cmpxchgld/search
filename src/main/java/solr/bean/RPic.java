package solr.bean;

import java.util.List;
import java.util.Map;

public class RPic {
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    String msg;
    int code;
    int retcode;
    List<String> images;
    List<Map<String, String>> pic;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Map<String, String>> getPic() {
        return pic;
    }

    public void setPic(List<Map<String, String>> pic) {
        this.pic = pic;
    }
}
