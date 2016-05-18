package com.hzf;

import com.hzf.bean.Tiezi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class CC98Crawler implements JDBCVariable
{
    public static final String URLPREFIX = "http://www.cc98.org/list.asp?boardid=@&page=";
    public static final String TIEURLPREFIX = "http://www.cc98.org/";
    public static final HashMap<String,String> cookies = new HashMap<>();
    public static final int MAXNUMS = 3698;
    public static void main( String[] args ) throws Exception {
        cookies.put("aspsky", "username=huyangc&usercookies=3&userid=510095&useranony=&userhidden=2&password=5aecb1b01cc10669");
//        try{
//            for(int i = 1;i<=MAXNUMS;++i){
//                Document doc = Jsoup.connect(URLPREFIX + i).cookies(cookies).get();
//                Elements eles = doc.select(".list-topic-table");
//                if(eles == null||eles.isEmpty())
//                    continue;
//                dealWithEach(eles);
//            }
//            saveToLocal(1,tiezis);
////            loadLocal(1);
//            getContent();
//            saveToLocal(2,tiezis);
////            loadLocal(2);
//            saveToDB(tiezis,"xinling2");
//        }catch(Exception ex){
//                ex.printStackTrace();
//        }
//        try {
//            BufferedReader br = new BufferedReader(new FileReader("mainboard"));
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                String[] words = line.split(" ");
//                int maxnum = Integer.parseInt(words[2]);
//                String boardid = words[0];
//                String boardname = words[1];
//                String urlprefix = URLPREFIX.replace("@",boardid);
//                ArrayList<Tiezi> tiezis = new ArrayList<>();
//                for (int i = 1; i <= maxnum; ++i) {
//                    Document doc = Jsoup.connect(urlprefix + i).cookies(cookies).get();
//                    Elements eles = doc.select(".list-topic-table");
//                    if (eles == null || eles.isEmpty())
//                        continue;
//                    dealWithEach(eles,tiezis);
//                }
//                saveToLocal(1,boardname,tiezis);
//                getContent(tiezis);
//                saveToLocal(2,boardname,tiezis);
//                saveToDB(tiezis,boardname);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ArrayList<Tiezi> tiezis = (ArrayList<Tiezi>)loadLocal("xinling",2);
        saveToDB(tiezis,"xinling");
    }
    public static final String NAME_CONTENT_PATTERN = "《(.*)》\n作者：(.*)\n发表于(.*)\n最后跟贴：";
    public static void dealWithEach(Elements eles,ArrayList<Tiezi> tiezis){
        Elements ties = eles.get(0).select("tr");
        for(Element ele:ties){
            Elements urls = ele.select("td:nth-child(2)>a");
            if(urls == null||urls.isEmpty())
                continue;
            Element urle = urls.get(0);
            String nameContent = urle.attr("title");
            Pattern p = Pattern.compile(NAME_CONTENT_PATTERN);
            Matcher m = p.matcher(nameContent);
            if(m.find()){
                Tiezi temp = new Tiezi();
                temp.setName(m.group(1));
                temp.setAuthor(m.group(2));
                temp.setTime(m.group(3));
                temp.setUrl(TIEURLPREFIX+urle.attr("href"));
                System.out.println(temp);
                tiezis.add(temp);
            }
        }
    }
    public static final String URLSUF = "&star=";
    public static boolean flag = false;
    public static void getContent(ArrayList<Tiezi> tiezis){
        for(Tiezi tiezi:tiezis) {
            StringBuilder ret = new StringBuilder();
            try {
                String totalurl = tiezi.getUrl();
                System.out.println(totalurl);
                Document doc = Jsoup.connect(totalurl).cookies(cookies).get();
                Element ele = doc.getElementById("topicPagesNavigation");
                Elements lasts = ele.select("a:last-child");
                int pagenums = 1;
                if (lasts != null && !lasts.isEmpty()){
                    String content = lasts.get(0).text();
                    if (content != null && !content.isEmpty()) {
                        pagenums = Integer.parseInt(content.substring(content.indexOf('[') + 1, content.indexOf(']')));
                    }
                }
                Elements articles = doc.select("article");
                String tempstr = articles.text();
//                StringBuilder tempstr = new StringBuilder(articles.text());
//                while (tempstr.indexOf("[quotex]") != -1 || tempstr.indexOf("[quote]") != -1) {
//                    if (tempstr.indexOf("[quotex]") != -1)
//                        tempstr.replace(tempstr.indexOf("[quotex]"),tempstr.indexOf("[/quotex]") + 9,"");
//                    if (tempstr.indexOf("[quote]") != -1)
//                        tempstr.replace(tempstr.indexOf("[quote]"),tempstr.indexOf("[/quote]") + 8,"");
//                }
                System.out.println(tempstr);
                ret.append(tempstr);
                tiezi.setPages(pagenums);
                for (int i = 2; i <= pagenums; ++i) {
                    String nowurl = totalurl + URLSUF + i;
                    doc = Jsoup.connect(nowurl).cookies(cookies).get();
                    articles = doc.select("article");
                    tempstr = articles.text();
//                    tempstr = new StringBuilder(articles.text());
//                    while (tempstr.indexOf("[quotex]") != -1 || tempstr.indexOf("[quote]") != -1) {
//                        if (tempstr.indexOf("[quotex]") != -1)
//                            tempstr.replace(tempstr.indexOf("[quotex]"),tempstr.indexOf("[/quotex]") + 9,"");
//                        if (tempstr.indexOf("[quote]") != -1)
//                            tempstr.replace(tempstr.indexOf("[quote]"),tempstr.indexOf("[/quote]") + 8,"");
//                    }
                    System.out.println(tempstr);
                    ret.append(tempstr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tiezi.setContent(ret.toString());
        }
    }
    public static void saveToDB(ArrayList<Tiezi> tis,String table) throws Exception{
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url,user,password);
        Statement stmt  = conn.createStatement();
        String createSql = "CREATE TABLE "+table+" (id  int(11) NOT NULL ," +
                "name  mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL ,\n" +
                "author  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL ,\n" +
                "time  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL ,\n" +
                "content  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL ,\n" +
                "url  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL ,\n" +
                "pagenum  int(11) NULL DEFAULT NULL ,\n" +
                "PRIMARY KEY (id)\n" +
                ")";
        try{
            stmt.execute(createSql);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        conn.setAutoCommit(false);
        String sql = "INSERT INTO "+table+" (id, name, author, time,content,pagenum, url) VALUES (?, ?, ?, ?, ?,?,?)";
        PreparedStatement insertstmt = conn.prepareStatement(sql);
        for(int i = 0;i<tis.size();++i){
            Tiezi tiezi = tis.get(i);
            insertstmt.setInt(1,i+1);
            insertstmt.setString(2,tiezi.getName());
            insertstmt.setString(3,tiezi.getAuthor());
            insertstmt.setString(4,tiezi.getTime());
            insertstmt.setString(5,tiezi.getContent());
            insertstmt.setInt(6,tiezi.getPages());
            insertstmt.setString(7,tiezi.getUrl());
            System.out.println(insertstmt);
            insertstmt.execute();
        }
        conn.commit();
    }
    public static void saveToLocal(int i,String boardName,ArrayList<Tiezi> ti){
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(boardName+"_tiezis"+i));
            os.writeObject(ti);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public static ArrayList<Tiezi> loadLocal(String boardName,int i) throws Exception{
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(boardName+"_tiezis"+i));
        ArrayList<Tiezi> ret = (ArrayList<Tiezi>) is.readObject();
        return ret;
    }
}
