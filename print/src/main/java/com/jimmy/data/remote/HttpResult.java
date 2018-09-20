package com.jimmy.data.remote;

import java.util.List;

/**
 * Created by APJ on 2018-9-20.
 */

public class HttpResult<T> {
    private int stats;
    private String message;
    private int totalPage;
    private String main;
    private List<T> list;
    private String others;
    private String totalPageList;

    public int getStats() {
        return stats;
    }

    public void setStats(int stats) {
        this.stats = stats;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getTotalPageList() {
        return totalPageList;
    }

    public void setTotalPageList(String totalPageList) {
        this.totalPageList = totalPageList;
    }
}
