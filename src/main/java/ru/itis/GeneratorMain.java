package ru.itis;

public class GeneratorMain {
    public static void main(String[] args){
        FileWithListOfLinksGenerator generator = new FileWithListOfLinksGenerator("links", "https://habr.com/ru");
        generator.generate();
    }
}