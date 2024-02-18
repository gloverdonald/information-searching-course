package ru.itis;

public class LinkGeneratorMain {
    public static void main(String[] args){
        FileWithLinksGenerator generator = new FileWithLinksGenerator("index.txt", "https://habr.com/ru");
        generator.generate();
    }
}