package ru.itis;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class Util {
    public static String loadTextFromFile(String filePath) {
        return String.join("\n", loadTextLinesFromFile(filePath));
    }

    public static List<String> loadTextLinesFromFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("Error reading links file");
            return Collections.emptyList();
        }
    }

    // запись html в файл
    public static void writeToFile(String content, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .toList();
    }

    public static String loadHTML(String stringUrl) throws RuntimeException {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();

            System.out.printf("Response code: %s from %s\n", code, stringUrl);

            if (code > 300) {
                throw new RuntimeException("Error to get html");
            }

            var br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> generateCleanText(String fileText) {
        Set<String> tokens = new HashSet<>();

        var res = fileText;
        StringTokenizer st = new StringTokenizer(res, " .,!:;\'\"\\@#$%&*()<>?`~{}[]0123456789");

        StringBuilder tokenizedRes = new StringBuilder();
        while (st.hasMoreTokens()) {
            tokenizedRes.append(st.nextToken()).append("\n");
        }

        String regex = "[А-ЯЁ]*[а-яё]{2,}";
        String text = tokenizedRes.toString();
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String word = text.substring(matcher.start(), matcher.end()).toLowerCase();
            var meanings = lookupForMeanings(word);
            try {
                if (meanings.get(0).getTransformations().size() > 1) {
                    String meaning = meanings.get(0).getPartOfSpeech().toString();
                    if (!(meaning.equals("Предлог")) & !(meaning.equals("Союз")) &
                            !(meaning.equals("Некая часть речи")) & !(meaning.equals("Частица")) & !(meaning.equals("Междометие"))) {
                        tokens.add(word);
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return tokens;
    }
}
