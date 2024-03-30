package ru.itis.task5;

import java.util.List;

public class CosineSimilarity {

    public Double getNorm(List<Double> values) {
        return Math.sqrt(
                values.stream()
                        .mapToDouble(element -> Math.pow(element, 2))
                        .sum()
        );
    }

    public Double multiplyVectors(List<Double> v1, List<Double> v2) {
        Double multiplication = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            multiplication += v1.get(i) * v2.get(i);
        }
        return multiplication;
    }

    public Double calculateCosineSimilarity(List<Double> v1, List<Double> v2) {
        var multiplication = multiplyVectors(v1, v2);
        var v1Norm = getNorm(v1);
        var v2Norm = getNorm(v2);
        if (v1Norm == 0.0 || v2Norm == 0.0) {
            return 0.0;
        }
        return multiplication / (v1Norm * v2Norm);
    }

}
