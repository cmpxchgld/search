package solr.bean.esb;

public class EmpBean {
	/**
	 * 工号
	 */
	String employeenumber;
	/**
	 * 姓名
	 */
	String employeename;
	/**
	 * 所属部门编码
	 */
	String departmentcode;
	/**
	 * 所属部门名称
	 */
	String departmentname;
	/**
	 * 所属公司简码 ITC
	 */
	String compcode;
	/**
	 * 排序字段
	 */
	int sortnum;

	/**
	 * oa排序
	 */
	int sortnumoa;
	/**
	 * 管理人员标记
	 */
	String manager;
	/**
	 * 职称
	 */
	String employeejob;
	/**
	 * 头衔
	 */
	String employeetitle;
	/**
	 * 邮件地址
	 */
	String email;
	/**
	 * 创建时间
	 */
	String createdate;
	/**
	 * 更新时间
	 */
	String updatedate;
	/**
	 * 移动电话
	 */
	String mobilephone;
	/**
	 * 办公电话
	 */
	String officeTelephone;
	/**
	 * 微波电话
	 */
	String microTelephone;
	/**
	 * 办公地址
	 */
	String officeAddress;
	/**
	 * 所属公司名称
	 */
	String targetName;
	/**
	 * 所属公司全称
	 */
	String sourceName;
	/**
	 * 所属公司简称
	 */
	String shortComName;
	/**
	 * 性别
	 */
	String gender;
	/**
	 * 传真
	 */
	String fax;

	public String getEmployeenumber() {
		return employeenumber;
	}

	public void setEmployeenumber(String employeenumber) {
		this.employeenumber = employeenumber;
	}

	public String getEmployeename() {
		return employeename;
	}

	public void setEmployeename(String employeename) {
		this.employeename = employeename;
	}

	public String getDepartmentcode() {
		return departmentcode;
	}

	public void setDepartmentcode(String departmentcode) {
		this.departmentcode = departmentcode;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getCompcode() {
		return compcode;
	}

	public void setCompcode(String compcode) {
		this.compcode = compcode;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getEmployeejob() {
		return employeejob;
	}

	public void setEmployeejob(String employeejob) {
		this.employeejob = employeejob;
	}

	public String getEmployeetitle() {
		return employeetitle;
	}

	public void setEmployeetitle(String employeetitle) {
		this.employeetitle = employeetitle;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getOfficeTelephone() {
		return officeTelephone;
	}

	public void setOfficeTelephone(String officeTelephone) {
		this.officeTelephone = officeTelephone;
	}

	public String getMicroTelephone() {
		return microTelephone;
	}

	public void setMicroTelephone(String microTelephone) {
		this.microTelephone = microTelephone;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getShortComName() {
		return shortComName;
	}

	public void setShortComName(String shortComName) {
		this.shortComName = shortComName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}

	public int getSortnumoa() {
		return sortnumoa;
	}

	public void setSortnumoa(int sortnumoa) {
		this.sortnumoa = sortnumoa;
	}

	@Override
	public String toString() {
		return "EmpBean [employeenumber=" + employeenumber + ", employeename=" + employeename + ", departmentcode=" + departmentcode + ", departmentname=" + departmentname
				+ ", compcode=" + compcode + ", sortnum=" + sortnum + ", sortnumoa=" + sortnumoa + ", manager=" + manager + ", employeejob=" + employeejob + ", employeetitle="
				+ employeetitle + ", email=" + email + ", createdate=" + createdate + ", updatedate=" + updatedate + ", mobilephone=" + mobilephone + ", officeTelephone="
				+ officeTelephone + ", microTelephone=" + microTelephone + ", officeAddress=" + officeAddress + ", targetName=" + targetName + ", sourceName=" + sourceName
				+ ", shortComName=" + shortComName + ", gender=" + gender + ", fax=" + fax + "]";
	}
}