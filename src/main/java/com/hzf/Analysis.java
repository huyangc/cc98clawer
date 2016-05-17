package com.hzf;

import com.hzf.bean.Tiezi;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;

import java.io.*;
import java.util.*;

/**
 * Created by ZhiFeng Hu on 2016/4/29.
 */
public class Analysis {
    static ArrayList<Tiezi> tiezis;

    public static void loadLocal(int i) throws Exception {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("tiezis" + i));
        tiezis = (ArrayList<Tiezi>) is.readObject();
    }

    public static void init(String boardName) {

        try {
            tiezis = CC98Crawler.loadLocal(boardName,2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Integer> analysisTitles(ArrayList<Tiezi> tiezis) {
        StringBuilder title = new StringBuilder();
        for (Tiezi tiezi : tiezis) {
            title.append(tiezi.getName());
        }
        FilterModifWord.insertStopNatures("ud", "uj");
        List<Term> terms = NlpAnalysis.parse(title.toString());
        FilterModifWord.modifResult(terms);
        HashMap<String, Integer> ret = new HashMap<>();
        for (Term t : terms) {
            if (t.getName().length() > 1) {
                if (ret.get(t.getName()) == null) {
                    ret.put(t.getName(), 1);
                } else
                    ret.put(t.getName(), ret.get(t.getName()) + 1);
            }
        }
        return ret;
    }
    public static HashMap<String,Integer> analysisContent(ArrayList<Tiezi> tiezis){
        StringBuilder content = new StringBuilder();
        for(Tiezi t:tiezis){
            content.append(t.getContent());
        }
        FilterModifWord.insertStopNatures("ud", "uj");
        List<Term> terms = NlpAnalysis.parse(content.toString());
        FilterModifWord.modifResult(terms);
        HashMap<String, Integer> ret = new HashMap<>();
        for (Term t : terms) {
            if (t.getName().length() > 1) {
                if (ret.get(t.getName()) == null) {
                    ret.put(t.getName(), 1);
                } else
                    ret.put(t.getName(), ret.get(t.getName()) + 1);
            }
        }
        return ret;
    }
    public static void sortAndSave(HashMap<String,Integer> wordFreq) throws IOException {
        List<Map.Entry<String,Integer>> list=new ArrayList<>();
        list.addAll(wordFreq.entrySet());
        Analysis.ValueComparator vc=new ValueComparator();
        Collections.sort(list,vc);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("title"));
            for(Iterator<Map.Entry<String,Integer>> it=list.iterator();it.hasNext();)
            {
                Map.Entry<String,Integer> k = it.next();
                String content = k.getKey()+":"+k.getValue();
                bw.write(content+'\n');
                System.out.println(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(null!=bw)
                bw.close();
        }


    }
    public static void main(String[] args) {
        String boardName = "xinling";
        init(boardName);

        try {
            HashMap<String, Integer> wordsFreq = analysisTitles(tiezis);
            sortAndSave(wordsFreq);
            HashMap<String,Integer> contentWordFreq = analysisContent(tiezis);
            sortAndSave(contentWordFreq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static class ValueComparator implements Comparator<Map.Entry<String, Integer>> {
        public int compare(Map.Entry<String, Integer> m, Map.Entry<String, Integer> n) {
            return n.getValue() - m.getValue();
        }
    }
}
