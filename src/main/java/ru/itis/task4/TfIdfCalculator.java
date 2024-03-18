package ru.itis.task4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;
import static ru.itis.Util.generateCleanText;

public class TfIdfCalculator {
    private static final String TOKEN_PATH = "src/main/resources/texts/tokens.txt";
    private static final String INVERTED_INDEX_PATH = "src/main/resources/inverted_index.txt";

    public List<String> getTokens() throws IOException {
        List<String> termsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TOKEN_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                termsList.add(line);
            }
        }
        return termsList;
    }

    //получение терминов и их числа в одном документе
    public Map<String, Integer> getTokensAndCount(List<String> text) throws IOException {
        Map<String, Integer> invertedIndex = new HashMap<>();
        List<String> termsList = getTokens();

        for (int j = 0; j < text.size(); j++) {
            for (String term : termsList) {
                if (text.get(j).equals(term)) {
                    invertedIndex.compute(term, (key, count) -> count == null ? 1 : count + 1);
                }
            }
        }

        return invertedIndex;
    }

    //получение лемм и сумм вхождения числа терминов в одном документе
    public Map<String, Integer> getLemmasAndCount(Map<String, Integer> tokensCount) {
        Map<String, Integer> lemmaList = new HashMap<>();

        for (Map.Entry<String, Integer> token : tokensCount.entrySet()) {
            var meanings = lookupForMeanings(token.getKey());
            try {
                String lemma = meanings.get(0).getLemma().toString();
                lemmaList.compute(lemma, (key, count) -> count == null ? token.getValue() : lemmaList.get(lemma) + token.getValue());
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return lemmaList;
    }

    //список лемм и количество документов, где встрчаются термины
    public Map<String, Integer> getLemmasAndAllDocumentsCount() throws IOException {
        Map<String, Integer> lemmasList = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(INVERTED_INDEX_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                lemmasList.put(parts[0], parts[1].split(" *, *").length);
            }
        }
        return lemmasList;
    }

    //список терминов и количество документов, где они встрчаются
    public Map<String, Integer> getTokensAndAllDocumentsCount() throws IOException {
        List<String> termsList = getTokens();
        Map<String, Integer> tokensList = new HashMap<>();

        for (int i = 1; i <= 150; i++) {
            String htmlFile = "src/main/resources/pages/index" + i + ".html";
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFile)), StandardCharsets.UTF_8);
            List<String> text = generateCleanText(htmlContent);
            for (int j = 0; j < termsList.size(); j++) {
                var word = termsList.get(j);
                if (text.contains(word)) {
                    tokensList.compute(word, (key, count) -> count == null ? 1 : count + 1);

                }
            }
        }
        return tokensList;
    }

    //подсчет tf
    public Map<String, Double> calculateTf(List<String> text, Map<String, Integer> words) {
        Map<String, Double> tfList = new HashMap<>();

        for (Map.Entry<String, Integer> word : words.entrySet()) {
            double termFrequency = word.getValue();
            var tf = termFrequency / text.size();
            tfList.put(word.getKey(), tf);
        }
        return tfList;
    }

    //подсчет idf
    public Map<String, Double> calculateIdf(Map<String, Integer> currentDocumentWords, Map<String, Integer> allDocumentsWords) {
        Map<String, Double> idfList = new HashMap<>();

        for (Map.Entry<String, Integer> word : currentDocumentWords.entrySet()) {
            double currentDocCount = word.getValue();
            double allDocCount = allDocumentsWords.get(word.getKey());
            var idf = Math.log(allDocCount / currentDocCount);
            idfList.put(word.getKey(), idf);
        }
        return idfList;
    }

    //запись в файл
    public void writeTfIdfToFile(Map<String, Double> idf, Map<String, Double> tf, String outFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(outFile, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, Double> entry : tf.entrySet()) {
                String token = entry.getKey();
                Double currentTf = entry.getValue();
                Double currentIdf = idf.get(token);
                Double tfIdf = currentIdf * currentTf;
                String valueStr = String.join(" ", currentIdf.toString(), tfIdf.toString());
                writer.println(token + " " + valueStr);
            }
        }
    }
}

