package ru.itis;

public class GeneratorMain {
    public static void main(String[] args){
        FileWithOfLinksGenerator generator = new FileWithOfLinksGenerator("links.txt", "https://habr.com/ru");
        generator.generate();
    }
}