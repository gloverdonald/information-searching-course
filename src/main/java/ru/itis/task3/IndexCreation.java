package ru.itis.task3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.itis.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IndexCreation {
    private final String lemmasFile;
    private final String outFile;

    public IndexCreation(String lemmasFile, String outFile) {
        this.lemmasFile = lemmasFile;
        this.outFile = outFile;
    }

    public void writeInvertedIndex() throws IOException {
        Map<String, Set<String>> invertedIndex = createInvertedIndex(lemmasFile);

        try (PrintWriter writer = new PrintWriter(outFile, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                List<String> valueList = new ArrayList<>(value);
                Collections.sort(valueList, Comparator.comparingInt(Integer::parseInt));
                String valueStr = String.join(", ", valueList);
                writer.println(key + " " + valueStr);
            }
        }
    }

    public static Map<String, Set<String>> createInvertedIndex(String lemmasFile) throws IOException {
        Map<String, Set<String>> invertedIndex = new HashMap<>();

        List<String> termsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(lemmasFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                termsList.add(line);
            }
        }

        for (int i = 1; i <= 150; i++) {
            String htmlFile = "src/main/resources/pages/index" + i + ".html";
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFile)), StandardCharsets.UTF_8);
            List<String> text = extractTextFromHtml(htmlContent);

            for (String word : text) {
                for (String term : termsList) {
                    String lemma = term.split(" ")[0];
                    String number = Integer.toString(i);
                    if (term.contains(word)) {
                        invertedIndex.computeIfAbsent(lemma, k -> new HashSet<>()).add(number);
                    }
                }
            }
        }

        return invertedIndex;
    }

    public static List<String> extractTextFromHtml(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Elements body = doc.select("body");

        body.select("script, style, a, span, button, label, footer, article").remove();

        String text = body.text().replaceAll("\\s+", " ");

        List<String> tokensPage = new ArrayList<>(Arrays.asList(text.split(" ")));
        tokensPage.replaceAll(String::toLowerCase);
        tokensPage.removeIf(word -> !word.matches("[а-яА-ЯёЁ]+"));

        return tokensPage;
    }
}
