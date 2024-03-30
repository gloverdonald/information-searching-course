package ru.itis.task5;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class VectorSearch {
    private static final String INVERTED_INDEX_PATH = "src/main/resources/inverted_index.txt";
    private static final String TF_IDF_LEMMA_PATH = "src/main/resources/tf_idf_lemmas/";
    private static final String INDEX_PATH = "src/main/resources/index.txt";
    private final Map<Integer, List<Double>> vectors;
    private final Map<String, Integer> lemmasDocumentCount;
    private final List<String> lemmaList;
    private final List<String> allPagesURL;
    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();

    public VectorSearch() {
        lemmasDocumentCount = getLemmasAndAllDocumentsCount();
        lemmaList = loadLemmas();
        vectors = loadVectors();
        allPagesURL = getAllPagesURL();
    }

    public Map<String, Integer> getLemmasAndAllDocumentsCount() {
        Map<String, Integer> lemmasList = new TreeMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(INVERTED_INDEX_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                lemmasList.put(parts[0], parts[1].split(" *, *").length);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lemmasList;
    }

    private List<String> loadLemmas() {
        List<String> lemmaList = lemmasDocumentCount.keySet().stream().toList();
        return lemmaList;
    }

    private Map<Integer, List<Double>> loadVectors() {
        Map<Integer, List<Double>> vectors = new TreeMap<>();
        for (int i = 1; i <= 150; i++) {
            Map<String, Double> lemmasDocList = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    TF_IDF_LEMMA_PATH + "lemma" + i + ".txt"), "UTF8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ", 3);
                    Double tfIdf = Double.valueOf(parts[2]);
                    lemmasDocList.put(parts[0], tfIdf);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            List<Double> curDocTfIdf = new ArrayList<>();
            for (int j = 0; j < lemmaList.size(); j++) {
                var curLemma = lemmaList.get(j);
                curDocTfIdf.add(lemmasDocList.getOrDefault(curLemma, 0.0));
            }
            vectors.put(i, curDocTfIdf);

        }
        return vectors;
    }

    //Подсчет tf для запроса
    public Map<String, Double> calculateTf(List<String> text) {
        Map<String, Double> tfList = new TreeMap<>();
        Map<String, Integer> countTokens = new TreeMap<>();
        var totalTokens = text.size();

        for (int i = 0; i < totalTokens; i++) {
            var word = text.get(i);
            countTokens.compute(word, (key, count) -> count == null ? 1 : count + 1);
        }

        for (Map.Entry<String, Integer> word : countTokens.entrySet()) {
            double termFrequency = word.getValue();
            var tf = termFrequency / totalTokens;
            tfList.put(word.getKey(), tf);
        }
        return tfList;
    }

    //Подсчет idf для запроса
    public Map<String, Double> calculateIdf(List<String> text) {
        Map<String, Double> idfList = new TreeMap<>();
        var totalTokens = text.size();

        for (int i = 0; i < totalTokens; i++) {
            var word = text.get(i);
            double allDocCount = lemmasDocumentCount.get(word);
            var idf = Math.log(150 / allDocCount);
            idfList.put(word, idf);
        }
        return idfList;
    }

    //Подсчет tf-idf для запроса
    public Map<String, Double> calculateTfIdf(Map<String, Double> tfList, Map<String, Double> idfList) {
        Map<String, Double> tfIdfList = new TreeMap<>();
        for (Map.Entry<String, Double> entry : tfList.entrySet()) {
            String token = entry.getKey();
            Double currentTf = entry.getValue();
            Double currentIdf = idfList.get(token);
            Double tfIdf = currentIdf * currentTf;
            tfIdfList.put(token, tfIdf);
        }
        return tfIdfList;
    }

    public List<String> getTokensFromQuery(String query) {
        var tokens = query.split("\s+");
        var text = new LinkedList<String>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                token = token.trim();
                var meanings = lookupForMeanings(token);
                try {
                if (meanings.get(0).getTransformations().size() > 1) {
                    String meaning = meanings.get(0).getPartOfSpeech().toString();
                    if (!(meaning.equals("Предлог")) & !(meaning.equals("Союз")) &
                            !(meaning.equals("Некая часть речи")) & !(meaning.equals("Частица")) & !(meaning.equals("Междометие"))) {

                            String lemma = meanings.get(0).getLemma().toString();
                            text.add(lemma);

                    }
                }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return text;
    }

    //Cоздание вектора запроса
    public List<Double> createVector(Map<String, Double> tfIdfList) {
        List<Double> curTfIdf = new ArrayList<>();
        for (int j = 0; j < lemmaList.size(); j++) {
            var curLemma = lemmaList.get(j);
            curTfIdf.add(tfIdfList.getOrDefault(curLemma, 0.0));
        }
        return curTfIdf;
    }

    public List<String> searchPages(String query) {
        var tokens = getTokensFromQuery(query);
        var queryVector = createVector(calculateTfIdf(calculateTf(tokens), calculateIdf(tokens)));
        List<Double> vectorSimilarity = new ArrayList<>();

        for (int i = 1; i <= 150; i++) {
            var similarity = cosineSimilarity.calculateCosineSimilarity(queryVector, vectors.get(i));
            vectorSimilarity.add(similarity);
        }

        List<Map.Entry<Double, Integer>> pairList = new ArrayList<>();
        for (int i = 0; i < vectorSimilarity.size(); i++) {
            Double value = vectorSimilarity.get(i);
            if (!value.equals(0.0)) {
                pairList.add(new SimpleEntry<>(value, i + 1));
            }
        }

        // Сортируем список пар по убыванию значений
        pairList.sort((e1, e2) -> e2.getKey().compareTo(e1.getKey()));

        // Выводим отсортированные значения и номера документов
        pairList.forEach(entry ->
                System.out.println("Документ номер: " + entry.getValue() + " Сходство: " + entry.getKey() + " Ссылка: " + getPageURL(entry.getValue())));

        List<String> firstTenPages = new ArrayList<>();
        if (pairList.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                firstTenPages.add(getPageURL(pairList.get(i).getValue()));
            }
            return firstTenPages;
        } else {
            for (int i = 0; i < pairList.size(); i++) {
                firstTenPages.add(getPageURL(pairList.get(i).getValue()));
            }
            return firstTenPages;
        }
    }

    public List<String> getAllPagesURL() {
        List<String> pages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(INDEX_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                pages.add(parts[1]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return pages;
    }

    public String getPageURL(int docNum) {
        return allPagesURL.get(docNum - 1);
    }

}
