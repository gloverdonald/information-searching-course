package ru.itis.task3;

import java.util.Scanner;
import java.util.Set;

public class BooleanSearchMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BooleanSearch bs = new BooleanSearch();
        while (true) {
            String query;
            System.out.print("Запрос: ");
            query = sc.nextLine();
            if (query.equals("!exit")) break;
            Set<Integer> result = bs.search(query);
            System.out.println("Found " + result.size() + " files: " + result);
        }
    }
}
