package com.fangzhe.community.entity;

/**
 * @author fang
 * 封装分页相关信息
 */
public class Page {
    public final Integer LIMITEST = 100;
    //当前页
    private Integer current = 1;
    //当前页存放上限
    private Integer limit = 10;
    //计算总的post数、或总的评论数（需要分页的总条目数）
    private Integer rows;
    //查询路径，复用分页链接
    private String path;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if(current >= 1){
        this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if(limit >= 1 && limit <= LIMITEST){
        this.limit = limit;
        }
    }

    public Integer getRows() {
        return rows;
    }

    /**
     * 总条目数
     * @param rows
     */
    public void setRows(Integer rows) {
        if(rows >= 0){
        this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    /**
     *
     *获取当前页的起始行
     *
     */
    public int getOffset(){
        return  (current - 1) * limit;
    }
    /**
     *
     *获取总页数
     *
     */
    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }
    /**
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1:from;
    }

    /**
     * @return
     */
    public int getTo(){
        int to = current + 2;
        return to > getTotal()? getTotal():to;
    }
}
