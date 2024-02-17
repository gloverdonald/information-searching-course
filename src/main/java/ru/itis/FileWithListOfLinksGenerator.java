package ru.itis;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static ru.itis.Util.getHTML;

public class FileWithListOfLinksGenerator {
    Set<String> links = new HashSet<>();
    private final String fileName;
    private final String baseURL;

    public FileWithListOfLinksGenerator(String fileName, String baseURL) {
        this.fileName = fileName;
        this.baseURL = baseURL;
    }

    public void generate() {
        habrCrowl(baseURL);
        createFileWithLinks(links);
        System.out.println(links);
    }

    private void createFileWithLinks(Set<String> links) {
        String path = String.format("src/main/resources/%s.txt", fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String link : links) {
                writer.write(link + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void habrCrowl(String url) {
        if (links.size() > 100) {
            return;
        }
        String html = "";
        try {
            html = getHTML(url);
        } catch (Exception e) {
            System.out.printf("error to get html from %s\n", url);
            return;
        }

        links.add(url);
        var pages = getLinksOnPage(html);
        for (String page : pages) {
            if (!page.startsWith("https://")) {
                page = baseURL + page;
            }
            if (!links.contains(page)) {
                habrCrowl(page);
            }
        }
    }

    private List<String> getLinksOnPage(final String page) {
        try {
            final Parser htmlParser = new Parser(page);
            final List<String> result = new LinkedList<>();
            final NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));


            for (int j = 0; j < tagNodeList.size(); j++) {
                final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
                final String loopLinkStr = loopLink.getLink();

                if (isGoodURL(loopLinkStr))
                    result.add(loopLinkStr);
            }
            return result;
        } catch (ParserException e) {
            System.out.println("error to get links");
            throw new RuntimeException(e);
        }
    }

    private boolean isGoodURL(String url) {
        return !url.contains("comments") &&
                !url.contains("register") &&
                !url.contains("auth") &&
                url.startsWith("https://habr.com");
    }
}
