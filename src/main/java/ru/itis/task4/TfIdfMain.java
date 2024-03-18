package ru.itis.task4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static ru.itis.Util.generateCleanText;

public class TfIdfMain {
    public static void main(String[] args) throws IOException {
        TfIdfCalculator tfIdfCalculator = new TfIdfCalculator();
        var lemmasDocumentCount = tfIdfCalculator.getLemmasAndAllDocumentsCount();
        var tokensDocumentCount = tfIdfCalculator.getTokensAndAllDocumentsCount();

        for (int i = 1; i <= 150; i++) {
            String htmlFile = "src/main/resources/pages/index" + i + ".html";
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFile)), StandardCharsets.UTF_8);
            List<String> text = generateCleanText(htmlContent);
            var tokens = tfIdfCalculator.getTokensAndCount(text);
            var lemmas = tfIdfCalculator.getLemmasAndCount(tokens);
            var tfTokens = tfIdfCalculator.calculateTf(text, tokens);
            var tfLemmas = tfIdfCalculator.calculateTf(text, lemmas);
            var idfTokens = tfIdfCalculator.calculateIdf(tokens, tokensDocumentCount);
            var idfLemmas = tfIdfCalculator.calculateIdf(lemmas, lemmasDocumentCount);
            tfIdfCalculator.writeTfIdfToFile(idfTokens, tfTokens, "src/main/resources/tf_idf_tokens/token" + i + ".txt");
            tfIdfCalculator.writeTfIdfToFile(idfLemmas, tfLemmas, "src/main/resources/tf_idf_lemmas/lemma" + i + ".txt");
        }
    }
}
