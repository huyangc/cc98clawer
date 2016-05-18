package com.hzf.bean;

import java.io.Serializable;

/**
 * Created by ZhiFeng Hu on 2016/4/27.
 */
public class Tiezi implements Serializable{
    private String name;
    private String content;
    private String author;
    private String url;
    private String time;
    private int pages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public void setPages(int page){
        pages = page;
    }
    public int getPages(){
        return pages;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n").append(author).append("\t").append(time).append("\n");
        sb.append(url).append("\n");
        sb.append(content);
        return sb.toString();
    }
}
