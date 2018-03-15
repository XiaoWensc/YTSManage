package asuper.yt.cn.supermarket.modules.contacts;

/**
 * Created by Administrator on 2017/3/8.
 */

public class Phone {

    /**
     * 用户ID
     */
    private int userId;

    /**
     * 用户姓名
     */
    private String employeeName;

    /**
     * 用户姓名拼音
     */
    private String mName;
    /**
     * 用户手机号码
     */
    private String employeePhonenumber;

    /**
     *
     */
    private int employeeCompanyId;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeePhonenumber() {
        return employeePhonenumber;
    }

    public void setEmployeePhonenumber(String employeePhonenumber) {
        this.employeePhonenumber = employeePhonenumber;
    }

    public int getEmployeeCompanyId() {
        return employeeCompanyId;
    }

    public void setEmployeeCompanyId(int employeeCompanyId) {
        this.employeeCompanyId = employeeCompanyId;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "userId=" + userId +
                ", employeeName='" + employeeName + '\'' +
                ", employeePhonenumber='" + employeePhonenumber + '\'' +
                ", employeeCompanyId=" + employeeCompanyId +
                '}';
    }
}
