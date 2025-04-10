package com.clubfactory.platform.scheduler.common.bean;


import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;

public class PageUtils<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int pageSize = 10;
    private int size;
    private int totalCount;
    private int totalPage;
    private int pageNo;
    private List<T> rows = Lists.newArrayList();

    public PageUtils() {
    }

    public PageUtils(int pageSize, int pageNo) {
        this.pageSize = pageSize;
        this.totalCount = 0;
        this.rows = Lists.newArrayList();
        this.pageNo = pageNo;
        this.size = 0;
        this.totalPage = 0;
    }

    public PageUtils(List<T> rows, int totalCount, int pageSize, int pageNo) {
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.rows = rows;
        this.pageNo = pageNo;
        this.size = rows.size();
        if (pageSize != 0) {
            this.totalPage = totalCount % pageSize > 0 ? totalCount / pageSize + 1 : totalCount / pageSize;
        } else {
            this.totalPage = 0;
        }

    }

    public int getPageNo() {
        return this.pageNo;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getRows() {
        return this.rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("PageInfo{");
        sb.append(", size=").append(this.size);
        sb.append(", totalCount=").append(this.totalCount);
        sb.append(", totalPage=").append(this.totalPage);
        sb.append(", rows=").append(this.rows);
        sb.append("}");
        return sb.toString();
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
