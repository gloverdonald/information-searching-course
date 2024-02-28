package ru.itis.task2;

import org.jsoup.Jsoup;
import ru.itis.Util;

import java.util.*;

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
        for (String fileName : fileNames) {
            var html = Util.loadTextFromFile(pathToFilesDirectory.concat("/").concat(fileName));
            var res = Jsoup.parse(html).text();
            StringTokenizer st = new StringTokenizer(res, " .,0123456789");

            StringBuilder tokenizedRes = new StringBuilder();
            while (st.hasMoreTokens()) {
                tokenizedRes.append(st.nextToken()).append("\n");
            }
            Util.writeToFile(tokenizedRes.toString(), pathToResultFilesDirectory.concat("/").concat(fileName));
        }
    }
}
