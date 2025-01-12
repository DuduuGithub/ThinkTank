package service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class VectorService {

    // 向量化函数，假设已经实现
    public double[] vectorize(String input) {
        // 这里是向量化函数
        // 返回一个double数组表示向量
        return new double[]{1.0, 2.0, 3.0}; // 示例的向量
    }

    // 计算两个向量之间的余弦相似度
    private double cosineSimilarity(double[] vecA, double[] vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += Math.pow(vecA[i], 2);
            normB += Math.pow(vecB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // 按照余弦相似度对向量数组进行排序
    public List<double[]> sortVectorsByCosineSimilarity(double[] vector, List<double[]> vectors) {
        return vectors.stream()
                .sorted(Comparator.comparingDouble(v -> -cosineSimilarity(vector, v)))
                .collect(Collectors.toList());
    }

    // 将字符串向量化并保存到文本文件，filePath是文件路径
    public void saveVectorToFile(String input, String filePath) throws IOException {
        double[] vector = vectorize(input);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(Arrays.toString(vector));
            writer.newLine();
        }
    }

    // 从文本文件中读取向量
    public List<double[]> readVectorsFromFile(String filePath) throws IOException {
        List<double[]> vectors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("[\\[\\]]", ""); // 去掉方括号
                String[] values = line.split(",\\s*");
                double[] vector = Arrays.stream(values)
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                vectors.add(vector);
            }
        }
        return vectors;
    }
}
