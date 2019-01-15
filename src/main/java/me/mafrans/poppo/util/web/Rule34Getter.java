package me.mafrans.poppo.util.web;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rule34Getter {
    private static String baseUrl = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&pid=${page}&tags=${tags}";

    public static Element[] getR34Posts(int limit, String[] tags) {
        List<Element> allPosts = new ArrayList<>();
        boolean hasPosts = true;
        int pid = 0;
        while(hasPosts && pid < 10) {
            hasPosts = false;
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(baseUrl.replace("${page}", String.valueOf(pid)).replace("${tags}", StringUtils.join(tags, " ")));

                System.out.println(baseUrl.replace("${page}", String.valueOf(pid)).replace("${tags}", StringUtils.join(tags, " ")));

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("post");

                int i = 0;
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nNode;

                        allPosts.add(element);
                        i++;
                        hasPosts = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            pid++;
        }
        Element[] out = new Element[limit];
        Random random = new Random();
        for(int i = 0; i < Math.min(allPosts.size(), limit); i++) {
            out[i] = allPosts.get(random.nextInt(allPosts.size()));
        }
        return out;
    }
}
