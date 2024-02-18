package ru.itis;

public class CrawlerMain {
    public static void main(String[] args) {
        var crawler = new Crawler("index.txt");
        crawler.crawl();
    }
}
