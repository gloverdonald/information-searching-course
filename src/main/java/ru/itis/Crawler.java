package ru.itis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;

import static ru.itis.Util.getHTML;

public class Crawler {
    String fileName;


    public Crawler(String fileName) {
        this.fileName = fileName;
    }

    public void crawl() {
        var links = getLinksFromFile();

        var i = 0;
        for (String link : links) {
            var fileName = String.format("index%s", i);
            writeToFile(rmTags(getHTML(link)), fileName);
            i++;
        }

    }

    private String rmTags(String html) {
        Document doc = Jsoup.parse(html);
        doc.select("script").remove();
        doc.select("style").remove();
        doc.select("img").remove();
        doc.select("link").remove();
        doc.select("a").remove();
        doc.select("meta").remove();
        doc.select("li").remove();
        doc.select("svg").remove();
        return doc.html();
    }


    private void writeToFile(String html, String fileName) {
        String path = String.format("src/main/resources/pages/%s.html", fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getLinksFromFile() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().toList();
            }
        } catch (IOException e) {
            System.out.println("Error reading links file");
            return Collections.emptyList();
        }
    }
}
