package solr.bean;

/**
 * @author 890211
 * @version 1.0
 * @discription 用户bean
 */
public class SolrUser {
    private String userId;
    private String name;
    private String job;
    private String title;
    private String email;
    private String mobile;
    private String officeTel;
    private String micorTel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOfficeTel() {
        return officeTel;
    }

    public void setOfficeTel(String officeTel) {
        this.officeTel = officeTel;
    }

    public String getMicorTel() {
        return micorTel;
    }

    public void setMicorTel(String micorTel) {
        this.micorTel = micorTel;
    }
}
