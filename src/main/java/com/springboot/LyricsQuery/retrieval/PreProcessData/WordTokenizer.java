package com.springboot.LyricsQuery.retrieval.PreProcessData;


/**
 * This is for INFSCI-2140 in 2020
 *
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	char[] texts;
	int index;
	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		this.texts = texts;
		index = 0;
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document

		// check index of the char array
		if (index == texts.length) return null;
		StringBuilder word = new StringBuilder();
		while (index < texts.length) {
			char c = texts[index];
			index++;
			// c must be a alphabetic and digit
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				word.append(c);
			}
			else if (c == ' ' && word.length() != 0) {
				break;
			}
		}
		return word.toString().toCharArray();
	}

}
