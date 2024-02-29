package ru.itis.task2;

import com.github.demidko.aot.MorphologyTag;
import org.jsoup.Jsoup;
import ru.itis.Util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;


public class HTMLPagesCleaner {
    private final String pathToFilesDirectory;
    private final String pathToResultFilesDirectory;

    public HTMLPagesCleaner(String pathToFilesDirectory,
                            String pathToResultFilesDirectory) {
        this.pathToFilesDirectory = pathToFilesDirectory;
        this.pathToResultFilesDirectory = pathToResultFilesDirectory;
    }

    public void generateCleanText() {
        var fileNames = Util.listFilesUsingJavaIO(pathToFilesDirectory);
        Set<String> tokens = new HashSet<>();
        for (String fileName : fileNames) {
            var html = Util.loadTextFromFile(pathToFilesDirectory.concat("/").concat(fileName));
            var res = Jsoup.parse(html).text();
            StringTokenizer st = new StringTokenizer(res, " .,!:;\'\"\\@#$%&*()<>?`~{}[]0123456789");

            StringBuilder tokenizedRes = new StringBuilder();
            while (st.hasMoreTokens()) {
                tokenizedRes.append(st.nextToken()).append("\n");
            }

            String regex = "[А-ЯЁ]*[а-яё]+";
            String text = tokenizedRes.toString();
            Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String word = text.substring(matcher.start(), matcher.end()).toLowerCase();
                var meanings = lookupForMeanings(word);
                System.out.println(lookupForMeanings("не").get(0).getMorphology());
                try {
                    String meaning = meanings.get(0).getMorphology().get(0).toString();
                    if (!(meaning.equals("СОЮЗ")) & !(meaning.equals("ПРЕДЛ")) &
                            !(meaning.equals("МЕЖД")) & !(meaning.equals("ЧАСТ"))) {
                        tokens.add(word);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

            }
        }
        Util.writeToFile(String.join("\n", tokens), pathToResultFilesDirectory.concat("/")
                .concat("tokens.txt"));
    }
}
