package com.springboot.LyricsQuery.retrieval.PreProcessData;
import com.springboot.LyricsQuery.retrieval.Classes.Stemmer;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class WordNormalizer {
	//you can add essential private methods or variables

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) {
		//transform the uppercase characters in the word to lowercase
		for (int i=0; i<chars.length; i++) {
			char c = chars[i];
			if (c >= 'A' && c <= 'Z') chars[i] = (char) (c - 'A' + 'a');
		}
		return chars;
	}

	public String stem(char[] chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
		Stemmer s = new Stemmer();
		s.add(chars, chars.length);
		s.stem();
		return s.toString();
	}

}
