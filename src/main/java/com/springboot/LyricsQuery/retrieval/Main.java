package com.springboot.LyricsQuery.retrieval;

import com.springboot.LyricsQuery.retrieval.PreProcessData.DocumentCollection;
import com.springboot.LyricsQuery.retrieval.PreProcessData.StopWordRemover;
import com.springboot.LyricsQuery.retrieval.PreProcessData.XMLCollection;
import com.springboot.LyricsQuery.retrieval.PreProcessData.WordNormalizer;
import com.springboot.LyricsQuery.retrieval.PreProcessData.WordTokenizer;

import java.io.FileWriter;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        // main entrance
        long startTime=System.currentTimeMillis(); //star time of running code
        Main hm1 = new Main();
        hm1.PreProcess("data/result.xml");//1.96min 503473 files
        long endTime=System.currentTimeMillis(); //end time of running code
        System.out.println("text corpus running time: "+(endTime-startTime)/60000.0+" min");
        hm1.PreProcess("data/result-2.xml");//1.96min 503473 files
        endTime=System.currentTimeMillis(); //end time of running code
        System.out.println("text corpus running time: "+(endTime-startTime)/60000.0+" min");

    }

    public void PreProcess(String path) throws Exception {
        // Loading the collection file and initiate the DocumentCollection class
        DocumentCollection corpus = new XMLCollection(path);


        // loading stopword list and initiate the StopWordRemover and WordNormalizer class
        WordNormalizer normalizerObj = new WordNormalizer();

        // initiate the BufferedWriter to output result
        FileWriter wr = new FileWriter("data/result.trectext", true);

        // initiate a doc object, which can hold document number and document content of a document
        Map<String, Object> doc = null;

        // process the corpus, document by document, iteractively
        int count=0;
        while ((doc = corpus.nextDocument()) != null) {
            // load document number of the document
            String docno = doc.keySet().iterator().next();
            System.out.println(docno);

            // load document content
            char[] content = (char[]) doc.get(docno);
            //content = content.toLowerCase();
            //System.out.println(String.valueOf(content));

            // write docno into the result file
            wr.append(docno + "\n");

            // initiate the WordTokenizer class
            WordTokenizer tokenizer = new WordTokenizer(content);

            // initiate a word object, which can hold a word
            char[] word = null;

            // process the document word by word iteratively
            while ((word = tokenizer.nextWord()) != null) {
                //System.out.println("original word: " + String.valueOf(word));
                // each word is transformed into lowercase
                word = normalizerObj.lowercase(word);
                //System.out.println("lowercase word: "+ String.valueOf(word));

                // filter out stopword, and only non-stopword will be written
                // into result file
                //System.out.println("is stop word ? "+stopwordRemoverObj.isStopword(word));
//                wr.append(normalizerObj.stem(word) + " ");
                wr.append(String.valueOf(word) + " ");
                //stemmed format of each word is written into result file
            }
            wr.append("\n");// finish processing one document
            count++;
            if(count%10000==0)
                System.out.println("Finish "+count+" docs");
        }
        System.out.println("Totaly document count:  "+count);
        wr.close();
    }
}
