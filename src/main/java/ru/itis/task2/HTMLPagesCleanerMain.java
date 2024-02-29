package ru.itis.task2;

import java.io.IOException;

public class HTMLPagesCleanerMain {
    public static void main(String[] args) throws IOException {
        HTMLPagesCleaner htmlPagesCleaner = new HTMLPagesCleaner("src/main/resources/pages", "src/main/resources/texts");
        htmlPagesCleaner.generateCleanText();
        LemmaParser lemmaParser = new LemmaParser("src/main/resources/texts");
        lemmaParser.generateLemmaFile();
    }

}
