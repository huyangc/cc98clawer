package com.hzf;

import com.hzf.bean.Tiezi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by ZhiFeng Hu on 2016/5/16.
 */
public class loadFromDB implements JDBCVariable {

    public static void main(String[] args) throws Exception {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url,user,password);
        Statement stmt = conn.createStatement();
        String sql = "select * from xinling";
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<Tiezi> tiezis = new ArrayList<>();
        while(rs.next()){
            Tiezi tie = new Tiezi();
            tie.setName(rs.getString("name"));
            tie.setContent(rs.getString("content"));
            tie.setPages(rs.getInt("pagenum"));
            tie.setUrl(rs.getString("url"));
            tie.setTime(rs.getString("time"));
            tie.setAuthor(rs.getString("author"));
            tiezis.add(tie);
        }
//        CC98Crawler.saveToLocal(2,tiezis);

    }
}
