package ru.itis.task3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class BooleanSearch {

    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String NOT = "NOT";
    private static final String INVERTED_INDEX_PATH = "src/main/resources/inverted_index.txt";
    private final Map<String, Set<String>> invertedIndex;

    public BooleanSearch() {
        invertedIndex = loadInvertedIndex();
    }

    public Set<Integer> search(String query) {
        return searchPages(query);
    }


    private Map<String, Set<String>> loadInvertedIndex() {
        Map<String, Set<String>> index = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(INVERTED_INDEX_PATH), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                Set<String> newSet = new HashSet<>(Arrays.asList(parts[1].split(" *, *")));
                index.put(parts[0], newSet);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return index;
    }

    public Set<Integer> searchPages(String query) {
        var tokens = query.split("(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|\s+");
        var text = new LinkedList<String>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                token = token.trim();
                var meanings = lookupForMeanings(token);
                try {
                    String lemma = meanings.get(0).getLemma().toString();
                    text.add(lemma);
                } catch (IndexOutOfBoundsException ignored) {
                    text.add(token);
                }

            }
        }

        return new TreeSet<>(parse(text).stream().map(Integer::parseInt).toList());
    }

    private Set<String> parse(Queue<String> tokens) {
        Set<String> set = new HashSet<>();
        boolean shouldUnion = false;
        boolean shouldIntersect = false;

        while (!tokens.isEmpty()) {
            String token = tokens.remove();
            System.out.println(token);
            switch (token) {
                case AND: {
                    shouldUnion = false;
                    shouldIntersect = true;
                    break;
                }
                case OR: {
                    shouldUnion = true;
                    shouldIntersect = false;
                    break;
                }
                case NOT: {
                    Set<String> set2 = parse(tokens);
                    if (set.isEmpty()){
                        for (int i = 1; i <= 150; i++) {
                            set.add("" + i);
                        }
                    }
                    set.removeAll(set2);
                    break;
                }
                case "(": {
                    Set<String> nestedSet = parse(tokens);
                    if (shouldUnion) {
                        set.addAll(nestedSet);
                    } else if (shouldIntersect) {
                        set.retainAll(nestedSet);
                    } else {
                        set = nestedSet;
                    }
                    break;
                }
                case ")":
                    return set;
                default:
                    Set<String> tokenSet = getInputSet(token);
                    if (shouldUnion) {
                        set.addAll(tokenSet);
                    } else if (shouldIntersect) {
                        Set<String> intersectedSet = new HashSet<>(set);
                        intersectedSet.retainAll(tokenSet);
                        set = intersectedSet;
                    } else {
                        set = tokenSet;
                    }
                    break;
            }
        }
        return set;
    }

    private Set<String> getInputSet(String token) {
        return invertedIndex.getOrDefault(token, Collections.emptySet());
    }

}

