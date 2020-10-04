package com.fitsoft.shop.utils;

/**
 * @author Joker
 * @since 2019/8/16 0016 19:59
 */
public class Pager {

    //1.当前页码
    private int pageIndex = 1;
    //2.一页需要展示多少条数据
    private int pageSize = 3;
    //3.当前条件下总的数据量
    private int totalCount;
    //4.总共可以分多少页
    private int totalPages;

    public int getPageIndex() {
        //取页码的时候做一些判断
        pageIndex = pageIndex < 1 ?1:pageIndex;
        //判断页码是否越界了
        pageIndex = pageIndex >= this.getTotalPages()? this.getTotalPages():pageIndex;
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        //总页数
        //根据总数量和每页最多展示多少条来确定
        //卧槽，这里简直精髓啊，之前得做多少判断啊
        return (this.getTotalCount()-1)/this.getPageSize() + 1;
    }

    public int getFirstParam(){
        return (this.getPageIndex()-1)*this.getPageSize();
    }
}
