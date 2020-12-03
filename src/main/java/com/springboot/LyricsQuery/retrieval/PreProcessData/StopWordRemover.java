package com.springboot.LyricsQuery.retrieval.PreProcessData;
import com.springboot.LyricsQuery.retrieval.Classes.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * This is for INFSCI-2140 in 2020
 *
 */

public class StopWordRemover {
	//you can add essential private methods or variables.
	private Set<String> stopSet;
	public StopWordRemover( ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir

		// use a hashset to check stopword
		stopSet = new HashSet<>();
		try {
			loadStopWords();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		String w = String.valueOf(word);
		if (stopSet.contains(w)) return true;
		return false;
	}

	/**
	 * this function load stopwords file to an in memory hashset
	 * @throws IOException
	 */
	private void loadStopWords() throws IOException {
		String path = Path.StopwordDir;
		BufferedReader input = new BufferedReader(new FileReader(path));
		String line;
		while ((line = input.readLine()) != null) {
			stopSet.add(line);
		}
		input.close();
	}
}
