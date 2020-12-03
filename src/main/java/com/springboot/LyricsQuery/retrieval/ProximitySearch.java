package com.springboot.LyricsQuery.retrieval;

import com.springboot.LyricsQuery.retrieval.Classes.Document;
import com.springboot.LyricsQuery.retrieval.Classes.Path;
import com.springboot.LyricsQuery.retrieval.IndexingLucene.MyIndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.*;

public class ProximitySearch {

    private Directory directory;
    private DirectoryReader ireader;
    private IndexSearcher indexSearcher;

    public ProximitySearch() {
        try {
            directory = FSDirectory.open(Paths.get(Path.IndexTextDir));
            ireader = DirectoryReader.open(directory);
            indexSearcher = new IndexSearcher(ireader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> retrieveQuery(String query) throws Exception{
        String[] words = query.split(" ");
        SpanQuery[] spanQueries = new SpanQuery[words.length];
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (int i = 0; i < words.length; i++) {
            Term term = new Term("LYRICS", words[i].toLowerCase());
            SpanQuery spanQuery = new SpanTermQuery(term);
            spanQueries[i] = spanQuery;
            builder.add(spanQuery, BooleanClause.Occur.SHOULD);
        }
        BooleanQuery booleanQuery = builder.build();
        SpanQuery spanQuery = new SpanNearQuery(spanQueries, 1, true);

        Directory directory = FSDirectory.open(Paths.get(Path.IndexTextDir));
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(ireader);

        // song name, artist name
        List<String> res = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        // first conduct span query
        if (words.length > 1) {
            ScoreDoc[] scoreDocSpan = indexSearcher.search(spanQuery, 20).scoreDocs;
            for (ScoreDoc score : scoreDocSpan) {
                if (set.add(score.doc)) {
                    String song = ireader.document(score.doc).get("SONG");
                    res.add(song);
                    System.out.println("span: "+song + ", "+score.score);
                }
            }
        }

        ScoreDoc[] scoreDoc = indexSearcher.search(booleanQuery, 20).scoreDocs;
        for (ScoreDoc score : scoreDoc) {
            if (set.add(score.doc)) {
                String song = ireader.document(score.doc).get("SONG");
                res.add(song);
                System.out.println(song + ", "+score.score);
            }
        }


        return res;
    }
    public static void main(String[] args) throws Exception{
        System.out.println("proximity search");
        SpanQuery that   = new SpanTermQuery(new Term("LYRICS", "that".toLowerCase()));
        SpanQuery you  = new SpanTermQuery(new Term("LYRICS", "you".toLowerCase()));
        SpanQuery were = new SpanTermQuery(new Term("LYRICS", "were".toLowerCase()));
        SpanQuery Romeo   = new SpanTermQuery(new Term("LYRICS", "Romeo".toLowerCase()));

        SpanQuery johnKerry =
                new SpanNearQuery(new SpanQuery[] {that, you, were, Romeo}, 2, true);



        Directory directory = FSDirectory.open(Paths.get(Path.IndexTextDir));
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(ireader);
        // conduct span query

        // conduct boolean query
        ScoreDoc[] scoreDoc = indexSearcher.search(johnKerry, 20).scoreDocs;
        for (ScoreDoc score : scoreDoc) {
            System.out.println(score.doc + " "+ ireader.document(score.doc).get("SONG")+" "+ score.score);
        }
    }
}
