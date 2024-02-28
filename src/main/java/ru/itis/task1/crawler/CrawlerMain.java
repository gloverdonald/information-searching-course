package ru.itis.task1.crawler;

public class CrawlerMain {
    public static void main(String[] args) {
        var crawler = new Crawler("index.txt");
        crawler.crawl();
    }
}
