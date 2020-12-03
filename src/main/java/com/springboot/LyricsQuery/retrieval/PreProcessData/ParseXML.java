package com.springboot.LyricsQuery.retrieval.PreProcessData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;

public class ParseXML {
    public static void main(String[] args) {

        try {
            File inputFile = new File("data/result.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("TRACK");
            System.out.println("----------------------------");
            FileWriter wr = new FileWriter("data/lyrics-result.trectext");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("ID : "
                            + eElement.getAttribute("ID"));
                    System.out.println("ARTIST : "
                            + eElement
                            .getElementsByTagName("ARTIST")
                            .item(0)
                            .getTextContent());
                    System.out.println("SONG : "
                            + eElement
                            .getElementsByTagName("SONG")
                            .item(0)
                            .getTextContent());
                    String song = eElement
                            .getElementsByTagName("SONG")
                            .item(0)
                            .getTextContent() + ",:" + eElement
                            .getElementsByTagName("ARTIST")
                            .item(0)
                            .getTextContent();
                    System.out.println("LYRICS : "
                            + eElement
                            .getElementsByTagName("LYRICS")
                            .item(0)
                            .getTextContent());
                    String lyrics = eElement
                            .getElementsByTagName("LYRICS")
                            .item(0)
                            .getTextContent();
                    lyrics = lyrics.replace("\n", "").replace("\r", "");
                    wr.append(song+"\n");
                    wr.append(lyrics+"\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
