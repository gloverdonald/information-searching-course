package ru.itis.task1.linksloader;

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

import static ru.itis.Util.loadHTML;

public class FileWithLinksGenerator {
    Set<String> links = new HashSet<>();
    private final String fileName;
    private final String baseURL;

    public FileWithLinksGenerator(String fileName, String baseURL) {
        this.fileName = fileName;
        this.baseURL = baseURL;
    }

    public void generate() {
        habrCrowl(baseURL);
        createFileWithLinks(links);
        System.out.println(links);
    }

    // запись результата обхода сайта в файл со списком ссылок
    private void createFileWithLinks(Set<String> links) {
        String path = String.format("src/main/resources/%s", fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            int i = 1;
            for (String link : links) {
                writer.write(i + ") " + link + "\n");
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // рекурсивный обход страницы и добавление всех подходящих ссылок в множество links, если такой ссылки уже нет
    private void habrCrowl(String url) {
        if (links.size() >= 150) {
            return;
        }
        String html = "";
        try {
            html = loadHTML(url);
        } catch (Exception e) {
            System.out.printf("error to get html from %s\n", url);
            return;
        }
        if (!baseURL.equals(url))
            links.add(url);
        // получение всех ссылок на странице
        var pages = getLinksOnPage(html);
        //обход всех ссылок и запуск рекурсии по каждой из них
        for (String page : pages) {
            if (!links.contains(page)) {
                habrCrowl(page);
            }
        }
    }

    // получение всего, что похоже на ссылоку на странице
    private List<String> getLinksOnPage(final String page) {
        try {
            final Parser htmlParser = new Parser(page);
            final List<String> result = new LinkedList<>();
            final NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));

            for (int j = 0; j < tagNodeList.size(); j++) {
                final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
                final String loopLinkStr = loopLink.getLink();

                if (isGoodHabrURL(loopLinkStr))
                    result.add(loopLinkStr);
            }
            return result;
        } catch (ParserException e) {
            System.out.println("error to get links");
            throw new RuntimeException(e);
        }
    }


    // фильтр на подходящие ссылки
    private boolean isGoodHabrURL(String url) {
        return !url.contains("comment") &&
                url.startsWith("https://habr.com") &&
                url.matches("\\S+ru\\S+\\/\\d+.\\S\\/");
    }
}
