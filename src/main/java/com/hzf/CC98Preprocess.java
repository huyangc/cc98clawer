package com.hzf;

import com.hzf.bean.Tiezi;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by ZhiFeng Hu on 2016/4/29.
 */
public class CC98Preprocess {
    static ArrayList<Tiezi> tiezis;
    public static void init(){
        try {
            loadLocal(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadLocal(int i) throws Exception{
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("tiezis"+i));
        tiezis = (ArrayList<Tiezi>) is.readObject();
    }
    public static void main(String[] args) {
        init();
        for(Tiezi tie:tiezis){
            StringBuilder sb = new StringBuilder(tie.getContent());
            try {
                while (sb.indexOf("[quotex]") != -1 || sb.indexOf("[quote]") != -1||sb.indexOf("[right]")!=-1) {
                    if (sb.indexOf("[quotex]") != -1) {
                        int index1 = sb.indexOf("[quotex]");
                        int index2 = sb.indexOf("[/quotex]");
                        int next;
                        while((next = sb.indexOf("[/quotex]",index2+9))<sb.indexOf("[quotex]",index2+9)){
                            if(next == -1)
                                break;
                            index2 = next;
                        }
                        sb.replace(index1, index2 + 9, "");
                    }
                    else if(sb.indexOf("[quote]") != -1){
                        int index1 = sb.indexOf("[quote]");
                        int index2 = sb.indexOf("[/quote]");
                        int next;
                        while((next = sb.indexOf("[/quote]",index2+8))<sb.indexOf("[quote]",index2+8)){
                            if(next == -1)
                                break;
                            index2 = next;
                        }
                        sb.replace(index1, index2 + 8, "");
                    }
                    else{
                        int index1 = sb.indexOf("[right]");
                        int index2 = sb.indexOf("[/right]");
                        int next;
                        while((next = sb.indexOf("[/right]",index2+8))<sb.indexOf("[right]",index2+8)){
                            if(next == -1)
                                break;
                            index2 = next;
                        }
                        sb.replace(index1, index2 + 8, "");
                    }
                    System.out.println(tie.getUrl());
                    System.out.println();
                }
            }catch(Exception ex){
                System.out.println(tie.getUrl());
                System.out.println(tie.getContent());
                System.out.println();
            }
            tie.setContent(sb.toString());
        }
        try {
            CC98Crawler.saveToDB(tiezis,"xinling");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
