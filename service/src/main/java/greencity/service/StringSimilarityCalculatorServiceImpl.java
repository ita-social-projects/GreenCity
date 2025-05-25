package greencity.service;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StringSimilarityCalculatorServiceImpl implements StringSimilarityCalculatorService{
    @Override
    public double calculateWordSimilarity(String word1, String word2) {
        if (word1.equals(word2)) {
            return 1.0;
        }
        int length1 = word1.length();
        int length2 = word2.length();
        int minLength = Math.min(length1, length2);
        int maxLength = Math.max(length1, length2);
        double score = 0.0;
        double prefixSuffixScore = calculatePrefixSuffixSimilarity(word1, word2, minLength, maxLength);
        score = Math.max(score, prefixSuffixScore);
        int levenshteinDistance = calculateLevenshteinDistance(word1, word2);
        double levenshteinSimilarity = 1.0 - (levenshteinDistance / (double) maxLength);

        if (levenshteinSimilarity > 0.7) {
            score = Math.max(score, levenshteinSimilarity * 0.8);
        }

        double ngramSimilarity = calculateNGramsSimilarity(word1, word2, 3);
        score = Math.max(score, ngramSimilarity * 0.5);
        double lengthWeight = 0.5 + Math.min(0.5, maxLength / 20.0);
        return score * lengthWeight;
    }

    private double calculatePrefixSuffixSimilarity(String word1, String word2, int minLength, int maxLength) {
        double score = 0.0;
        double lengthRatio = (double) minLength / maxLength;

        if (word1.startsWith(word2) || word2.startsWith(word1)) {
            score = Math.max(score, 0.7 * lengthRatio);
        }
        if (word1.endsWith(word2) || word2.endsWith(word1)) {
            score = Math.max(score, 0.6 * lengthRatio);
        }
        return score;
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    @SuppressWarnings("SameParameterValue")
    private double calculateNGramsSimilarity(String s1, String s2, int n) {
        if (s1.length() < n || s2.length() < n) {
            return 0.0;
        }

        Set<String> ngrams1 = generateNGrams(s1, n);
        Set<String> ngrams2 = generateNGrams(s2, n);
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private Set<String> generateNGrams(String str, int n) {
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= str.length() - n; i++) {
            ngrams.add(str.substring(i, i + n));
        }
        return ngrams;
    }
}
