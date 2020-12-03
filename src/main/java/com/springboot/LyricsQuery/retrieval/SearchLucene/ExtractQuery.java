package com.springboot.LyricsQuery.retrieval.SearchLucene;

import java.util.ArrayList;

import com.springboot.LyricsQuery.retrieval. Classes.Query;

public class ExtractQuery {

	ArrayList<Query> queries;

	int idx = 0;

	public ExtractQuery() {
		// you should extract the 4 queries from the Path.TopicDir
		// NT: the query content of each topic should be 1) tokenized, 2) to
		// lowercase, 3) remove stop words, 4) stemming
		// NT: you can simply pick up title only for query, or you can also use
		// title + description + narrative for the query content.
		queries = new ArrayList<>();
		Query aQuery = new Query();
		aQuery.SetTopicId("901");
		aQuery.SetQueryContent("a love story");
		queries.add(aQuery);
		aQuery = new Query();
		aQuery.SetTopicId("902");
		aQuery.SetQueryContent("that you are romeo");
		queries.add(aQuery);
		aQuery = new Query();
		aQuery.SetTopicId("903");
		aQuery.SetQueryContent("And I was crying on the staircase");
		queries.add(aQuery);
		aQuery = new Query();
		aQuery.SetTopicId("904");
		aQuery.SetQueryContent("I was begging you Please don't go and I said");
		queries.add(aQuery);
	}

	public boolean hasNext() {
		if (idx == queries.size()) {
			return false;
		} else {
			return true;
		}
	}

	public Query next() {
		return queries.get(idx++);
	}

}
