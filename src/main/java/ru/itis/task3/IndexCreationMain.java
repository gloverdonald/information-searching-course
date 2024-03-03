package ru.itis.task3;

import java.io.IOException;

public class IndexCreationMain {
    public static void main(String[] args) throws IOException {
        String lemmasFile = "src/main/resources/texts/lemmas.txt";
        String outFile = "src/main/resources/inverted_index.txt";
        IndexCreation indexCreation = new IndexCreation(lemmasFile, outFile);
        indexCreation.writeInvertedIndex();
    }
}
