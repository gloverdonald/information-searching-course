package ru.itis.task2;

public class HTMLPagesCleanerMain {
    public static void main(String[] args) {
        HTMLPagesCleaner htmlPagesCleaner = new HTMLPagesCleaner("src/main/resources/pages", "src/main/resources/texts");
        htmlPagesCleaner.generateCleanText();
    }

}
