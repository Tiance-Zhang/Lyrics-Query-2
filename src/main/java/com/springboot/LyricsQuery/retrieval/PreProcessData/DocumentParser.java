package com.springboot.LyricsQuery.retrieval.PreProcessData;

import com.springboot.LyricsQuery.retrieval.Classes.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Xueming Yang
 * This class parses the docno and content for the trectext and trectweb format files
 */

public class DocumentParser {

    /**
     * @param input Scanner for reading the file
     * @return the parsed DOCNO of the current document
     */
    public static String parseDocNo(BufferedReader input) throws IOException {
        String docNo = null;
        Pattern pDocNo = Pattern.compile("\\>(.*?)\\<");
        Matcher m = pDocNo.matcher(input.readLine());
        while (m.find()) {
            docNo = m.group(1).trim();
        }
        return docNo;
    }

    /**
     * parse the trectext format file and extract the content between <TEXT></TEXT>
     * @param input Scanner for reading the file
     * @return the content of current document
     */
    public static String parseTextContent(BufferedReader input) throws IOException {
        // skip the line that is not the <TEXT>
        String line;
        while (!(line = input.readLine()).equals("<TEXT>")) {
            // skip the lines from <DOC> to <TEXT>
        }
        //line = "<TEXT>"
        StringBuilder content = new StringBuilder();
        while(!(line = input.readLine()).equals("</TEXT>")) {
            line = line.trim();
            content.append(line);
            content.append(" ");
        }
        String text = null;
        if (content.length() != 0) {
            text = content.substring(0, content.length() - 1);
        }
        return text;
    }

    /**
     * parse the trecweb format file and extract content between </DOCHDR></DOC>
     * this will remove all the html tags which is in '<>' format in the content
     * @param input
     * @return
     */
    public static String parseWebContent(BufferedReader input) throws IOException{
        String line;
        while (!(line = input.readLine()).equals("</DOCHDR>")) {
            // skip the line from <DOCNO> to </DOCHDR>
        }
        // line = "</DOCHDR>"
        StringBuilder content = new StringBuilder();
        while(!(line = input.readLine()).equals("</DOC>")) {
            line = line.trim();
            // remove tags in '<>' format
            if (line == null || line.length() == 0) continue;
            content.append(line);
            content.append(" ");
        }
        // now Line="</DOC>"
        String text = null;
        if (content.length() > 1) {
            text = content.substring(0, content.length() - 1);
            // final check html checks - some html tags consists of multiple lines
            text = text.replaceAll("\\<.*?\\>", "").trim();
        }
        return text;
    }


}
