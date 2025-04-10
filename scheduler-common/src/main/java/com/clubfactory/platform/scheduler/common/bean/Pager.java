package com.clubfactory.platform.scheduler.common.bean;


import java.io.Serializable;

public class Pager implements Serializable {
    private static final long serialVersionUID = 5883636778638787974L;
    private int totalPage = 1;
    private int totalCount = 0;
    private int pageSize = 10;
    private int pageNo = 1;
    private int startIndex;
    private String sidx;
    private String sord;

    public Pager() {
    }

    public Pager(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Pager(int pageNo, int pageSize, String sidx, String sord) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.sidx = sidx;
        this.sord = sord;
    }

    public int getStartIndex() {
        return this.getPageNo() <= 0 ? 0 : this.getPageSize() * (this.getPageNo() - 1);
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        if (pageNo == 0) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }

    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        if (this.totalCount != 0 || this.pageSize != 0) {
            this.totalPage = this.totalCount / this.pageSize;
            if (this.totalCount % this.pageSize != 0) {
                ++this.totalPage;
            }
        }

        if (this.totalPage == 0) {
            this.totalPage = 1;
        }

        return this.totalPage;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getSidx() {
        return this.sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return this.sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }
}
