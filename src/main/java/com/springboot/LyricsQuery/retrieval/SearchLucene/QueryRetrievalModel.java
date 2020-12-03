package com.springboot.LyricsQuery.retrieval.SearchLucene;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.springboot.LyricsQuery.retrieval.Classes.Document;
import com.springboot.LyricsQuery.retrieval.Classes.Path;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.springboot.LyricsQuery.retrieval.IndexingLucene.MyIndexReader;


public class QueryRetrievalModel {

	protected MyIndexReader indexReader;

	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher indexSearcher;

	public QueryRetrievalModel(MyIndexReader ixreader) {
		try {
			directory = FSDirectory.open(Paths.get(Path.IndexTextDir));
			ireader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(ireader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Document> retrieveQuery(com.springboot.LyricsQuery.retrieval.Classes.Query aQuery, int TopN) throws Exception {
		List<Document> results = new ArrayList<Document>();
		Query theQ = new QueryParser("LYRICS", new WhitespaceAnalyzer()).parse(aQuery.GetQueryContent());
		ScoreDoc[] scoreDoc = indexSearcher.search(theQ, TopN).scoreDocs;
		for (ScoreDoc score : scoreDoc) {
			results.add(new Document(score.doc + "", ireader.document(score.doc).get("SONG"), score.score));
		}
		return results;
	}

}
