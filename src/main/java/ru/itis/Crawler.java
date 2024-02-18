package ru.itis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;

import static ru.itis.Util.loadHTML;

public class Crawler {
    String fileName;


    public Crawler(String fileName) {
        this.fileName = fileName;
    }

    // обходим по ссылкам, загружаем каждую из них, удаляя при этом ненужные теги
    public void crawl() {
        var links = getLinksFromFile();

        var i = 1;
        for (String link : links) {
            var fileName = String.format("index%s", i);
            writeToFile(rmTags(loadHTML(link)), fileName);
            i++;
        }

    }

    // удаление ненужных html тегов
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


    // запись html в файл
    private void writeToFile(String html, String fileName) {
        String path = String.format("src/main/resources/pages/%s.html", fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // загрузка ссылок из файла
    private List<String> getLinksFromFile() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().map(this::clearRow).toList();
            }
        } catch (IOException e) {
            System.out.println("Error reading links file");
            return Collections.emptyList();
        }
    }

    private String clearRow(String row) {
        int start = row.indexOf(")");
        return row.substring(start + 1).trim();
    }
}
