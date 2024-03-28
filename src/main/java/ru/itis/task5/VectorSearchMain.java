package ru.itis.task5;

import java.util.Scanner;

public class VectorSearchMain {
    public static void main(String[] args) {
        VectorSearch vectorSearch = new VectorSearch();

        Scanner sc = new Scanner(System.in);
        while (true) {
            String query;
            System.out.print("Запрос: ");
            query = sc.nextLine();
            if (query.equals("!exit")) break;
            vectorSearch.searchPages(query);
        }
    }
}
