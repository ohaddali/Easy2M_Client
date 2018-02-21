package nok.easy2m.models;

import java.sql.Time;

/**
 * Created by pc on 2/21/2018.
 */

public class Report
{
    private long reportId;
    private long relatedId; //WorkerId or companyId
    private String date;
    private String url;
    private boolean workerReport;


    public Report() {
    }


    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(long relatedId) {
        this.relatedId = relatedId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isWorkerReport() {
        return workerReport;
    }

    public void setWorkerReport(boolean workerReport) {
        this.workerReport = workerReport;
    }
}
