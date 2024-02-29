package ru.itis.task2;

import ru.itis.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class LemmaParser {
    private final String pathToFilesDirectory;

    public LemmaParser(String pathToFilesDirectory) {
        this.pathToFilesDirectory = pathToFilesDirectory;
    }

    public void generateLemmaFile() throws IOException {
        var tokenFile = pathToFilesDirectory.concat("/tokens.txt");
        Map<String, String> lemmas = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(tokenFile));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            var meanings = lookupForMeanings(currentLine);
            try {
                String lemma = meanings.get(0).getLemma().toString();
                if (!lemmas.containsKey(lemma)) {
                    lemmas.put(lemma, currentLine);
                } else {
                    currentLine = currentLine.concat(" ").concat(lemmas.get(lemma));
                    lemmas.put(lemma, currentLine);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        String lemmaFile = "";
        for (Map.Entry<String, String> lemma : lemmas.entrySet()) {
            lemmaFile = lemmaFile.concat(lemma.getKey().concat(": ").concat(lemma.getValue()).concat("\n"));
        }
        Util.writeToFile(lemmaFile, pathToFilesDirectory.concat("/")
                .concat("lemmas.txt"));
    }
}
