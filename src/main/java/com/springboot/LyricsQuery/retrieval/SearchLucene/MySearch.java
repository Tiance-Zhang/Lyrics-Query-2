package com.springboot.LyricsQuery.retrieval.SearchLucene;

import java.io.IOException;
import java.util.*;
import com.springboot.LyricsQuery.retrieval.Classes.Query;
import com.springboot.LyricsQuery.retrieval. Classes.Document;
import com.springboot.LyricsQuery.retrieval. IndexingLucene.MyIndexReader;



public class MySearch {

    protected MyIndexReader indexReader;
    private final long COLLECTION_LENGTH;
    private final int DOC_NUMBER = 30746;
    private final int MU = 65; // the score is tuned in "TrainMu.java"

    public MySearch (MyIndexReader ixreader) throws Exception {

        indexReader = ixreader;
        // get the collection length -> total count of words in the collection
        int length = 0;
        for (int i = 0; i < DOC_NUMBER; i++) {
            length += indexReader.docLength(i);
        }
        COLLECTION_LENGTH = length;
    }


    /**
     * Search for the topic information.
     * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
     * TopN specifies the maximum number of results to be returned.
     *
     * @param aQuery The query to be searched for.
     * @param TopN The maximum number of returned document
     * @return
     */

    public List<Document> retrieveQuery(Query aQuery, int TopN) throws IOException {
        // NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
        // implement your retrieval model here, and for each input query, return the topN retrieved documents
        // sort the docs based on their relevance score, from high to low
        String q = aQuery.GetQueryContent();
        String[] words = q.split(" ");
        Set<String> query = new HashSet<>();
        query.addAll(Arrays.asList(words));
        Map<Integer, Map<String, Integer>> doc2query= new HashMap<>();
        Map<String, Long> collectionFreq = new HashMap<>();
        // probability of document: p(qi | d) = p(q1|d)p(q2|d) ... p(qi|d)
        // p(w|d) = (c(w;d) + miu*p(w|c))/(|d| + miu)
        for (String word: words) {
            long colFreq = indexReader.CollectionFreq(word);
            collectionFreq.put(word, colFreq);
            if (colFreq == 0) continue;
            int[][] posting = indexReader.getPostingList(word);
            for(int ix=0;ix<posting.length;ix++){
                int docid = posting[ix][0];
                int freq = posting[ix][1];
                Map<String, Integer> wordFreq = doc2query.getOrDefault(docid, new HashMap<>());
                wordFreq.put(word, freq);
                doc2query.put(docid, wordFreq);
            }
        }

        Comparator<Document> comparator = new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Double.compare(o1.score(), o2.score());
            }
        };
        // calculate the probability of each document
        // use a priority queue to sort the document score
        PriorityQueue<Document> pq = new PriorityQueue<>(TopN, comparator);
        for (int docid: doc2query.keySet()) {
            Map<String, Integer> wordFreq = doc2query.get(docid);
            String docno = indexReader.getDocno(docid);
            double score = 1;
            int docLength = indexReader.docLength(docid);
            for (String word : query) {
                long colFreq = collectionFreq.get(word);
                if (colFreq == 0) continue; // some words may not in the collection
                int docFreq = wordFreq.getOrDefault(word, 0);
                double pc = (double) colFreq / (double) COLLECTION_LENGTH;
                double pd = (double) (docFreq + MU * pc) / (double) (docLength + MU);
                score *= pd;
            }
            Document document = new Document(String.valueOf(docid), docno, score);
            if (pq.size() < TopN) {
                pq.add(document);
            }
            else if (pq.peek().score() < score) {
                pq.poll();
                pq.add(document);
            }
        }
        List<Document> ans = new ArrayList<>();
        while (!pq.isEmpty()) {
            ans.add(0, pq.poll());
        }
        return ans;
    }

}
