package com.springboot.LyricsQuery.retrieval.PreProcessData;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLCollection implements DocumentCollection{
    private BufferedReader input;
    private int temp;
    private final int doclength;
    private NodeList nList;
    // YOU SHOULD IMPLEMENT THIS METHOD
    public XMLCollection(String path) throws Exception {
        // This constructor should open the file existing in Path.DataTextDir
        // and also should make preparation for function nextDocument()
        // you cannot load the whole corpus into memory here!!
        temp = 0;
        File inputFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        nList = doc.getElementsByTagName("TRACK");
        System.out.println("----------------------------");
        doclength = nList.getLength();
    }

    // YOU SHOULD IMPLEMENT THIS METHOD
    public Map<String, Object> nextDocument() throws IOException {
        // this method should load one document from the corpus, and return this document's number and content.
        // the returned document should never be returned again.
        // when no document left, return null
        // NTT: remember to close the file that you opened, when you do not use it any more
        if (temp >= doclength) return null;
        Map<String, Object> collection = new HashMap<>();
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            String song = eElement
                    .getElementsByTagName("SONG")
                    .item(0)
                    .getTextContent() + ",:" + eElement
                    .getElementsByTagName("ARTIST")
                    .item(0)
                    .getTextContent();
            char[] chars = new char[0];
            String lyrics = eElement
                    .getElementsByTagName("LYRICS")
                    .item(0)
                    .getTextContent();
            lyrics = lyrics.replace("\n", " ").replace("\r", " ");
            //System.out.println(lyrics);
            chars = lyrics.toCharArray();
            collection.put(song, chars);
        }
        temp++;
        return collection;

    }
}
