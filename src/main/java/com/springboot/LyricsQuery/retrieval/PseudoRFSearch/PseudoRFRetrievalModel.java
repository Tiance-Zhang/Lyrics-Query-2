package com.springboot.LyricsQuery.retrieval.PseudoRFSearch;

import java.io.IOException;
import java.util.*;

import com.springboot.LyricsQuery.retrieval.Classes.Document;
import com.springboot.LyricsQuery.retrieval.Classes.Query;
import com.springboot.LyricsQuery.retrieval.IndexingLucene.MyIndexReader;
import com.springboot.LyricsQuery.retrieval.SearchLucene.*;

import javax.print.Doc;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private final int DOC_NUMBER = 30746;
	private final int MU = 65;
	private final long COLLECTION_LENGTH;

	public PseudoRFRetrievalModel (MyIndexReader ixreader)
	{
		this.ixreader = ixreader;
		int length = 0;
		try{
			for (int i = 0; i < DOC_NUMBER; i++) {
				length += ixreader.docLength(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		COLLECTION_LENGTH = length;

	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2020 Fall assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> retrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')


		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore = GetTokenRFScore(aQuery,TopK);

		// calculate each query token's original retrieval probability p(w|d)
		// the relevance feedback model: p(token | d') = alpha*p(token|d) + (1-alpha)*p(token|feedback documents)
		String q = aQuery.GetQueryContent();
		String[] words = q.split(" ");
		Set<String> query = new HashSet<>();
		query.addAll(Arrays.asList(words));
		Map<Integer, Map<String, Integer>> doc2query= new HashMap<>();
		Map<String, Long> collectionFreq = new HashMap<>();
		// original probability p(w|d) = (c(w;d) + miu*p(w|c))/(|d| + miu)
		for (String word: words) {
			long colFreq = ixreader.CollectionFreq(word);
			collectionFreq.put(word, colFreq);
			if (colFreq == 0) continue;
			int[][] posting = ixreader.getPostingList(word);
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
			String docno = ixreader.getDocno(docid);
			double score = 1;
			int docLength = ixreader.docLength(docid);
			for (String word : query) {
				long colFreq = collectionFreq.get(word);
				if (colFreq == 0) continue; // some words may not in the collection
				int docFreq = wordFreq.getOrDefault(word, 0);
				double pc = (double) colFreq / (double) COLLECTION_LENGTH;
				double pd = (double) (docFreq + MU * pc) / (double) (docLength + MU);
				double pfd = TokenRFScore.get(word);
				score *= alpha*pd+(1-alpha)*pfd;
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

		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();
		while (!pq.isEmpty()) {
			results.add(0, pq.poll());
		}
		return results;
	}

	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it

		// retrieve TopK feedback documents
		MySearch PRFSearchModel = new MySearch(ixreader);
		List<Document> fd = PRFSearchModel.retrieveQuery(aQuery, TopK);
		Set<Integer> fdSet = new HashSet<>();
		int fdLength = 0;
		for (Document doc: fd) {
			int docid = Integer.parseInt(doc.docid());
			fdLength += ixreader.docLength(docid);
			fdSet.add(docid);
		}
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
		String query = aQuery.GetQueryContent();
		String[] tokens = query.split(" ");
		// calculate probability using Dirichlet smoothing
		// P(token|feedback documents) = (c(token; fd) + mu*p(token|C)) / (|fd| + mu)
		for (String token: tokens) {
			long wordFreqCol = ixreader.CollectionFreq(token);
			if (wordFreqCol == 0) {
				TokenRFScore.put(token, 0.0);
				continue;
			}
			int[][] posting = ixreader.getPostingList(token);
			long totalFreq = 0;
			for (int[] pair: posting) {
				int docid = pair[0];
				int freq = pair[1];
				if (fdSet.contains(docid)) {
					totalFreq += freq;
				}
			}
			double up =  (double) totalFreq + MU * (wordFreqCol/COLLECTION_LENGTH);
			double down = (double)fdLength + MU;
			double score = up/down;
			TokenRFScore.put(token, score);
		}
		return TokenRFScore;
	}


}
