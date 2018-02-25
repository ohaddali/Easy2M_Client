package nok.easy2m.models;

/**
 * Created by pc on 2/25/2018.
 */
public class Shift {

    private long id;
    private long companyId;
    private long roleId;
    private String startTime;
    private String endTime;
    private int dayInTheWeek;


    public Shift(long companyId , long roleId, String startTime , String endTime , int dayInTheWeek)
    {
        this.companyId = companyId;
        this.roleId = roleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayInTheWeek = dayInTheWeek;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDayInTheWeek() {
        return dayInTheWeek;
    }

    public void setDayInTheWeek(int dayInTheWeek) {
        this.dayInTheWeek = dayInTheWeek;
    }
}
