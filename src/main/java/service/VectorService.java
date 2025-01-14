package service;

import java.io.*;
import java.util.*;
// import java.util.stream.Collectors;

public class VectorService {
    private static final String VECTOR_DIR = "D:/java_hm_work/tokens/";

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

    // 从文件中读取向量
    private double[] readVectorFromFile(String fileName) throws IOException {
        File vectorFile = new File(VECTOR_DIR, fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(vectorFile))) {
            String vectorStr = reader.readLine();
            String[] values = vectorStr.replaceAll("[\\[\\]]", "").split(",\\s*");
            return Arrays.stream(values)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        }
    }

    // 找到与给定文件列表最相似的前几个文章
    public List<String> findMostSimilarArticles(List<String> fileNames, String directoryPath, int topN) throws IOException {
        Map<String, double[]> vectors = new HashMap<>();
        double[] sumVector = new double[0];
        int count = 0;

        // 读取输入文件的向量
        for (String fileName : fileNames) {
            double[] vector = readVectorFromFile(fileName);
            if (sumVector.length == 0) {
                sumVector = vector.clone();
            } else {
                for (int i = 0; i < vector.length; i++) {
                    sumVector[i] += vector[i];
                }
            }
            count++;
        }

        // 计算平均向量
        final int finalCount = count;
        double[] averageVector = Arrays.stream(sumVector).map(v -> v / finalCount).toArray();

        // 读取所有文件的向量
        File directory = new File(VECTOR_DIR);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                if (!fileNames.contains(file.getName())) {
                    double[] vector = readVectorFromFile(file.getName());
                    vectors.put(file.getName(), vector);
                }
            }
        }

        // 计算相似度并排序
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : vectors.entrySet()) {
            double similarity = cosineSimilarity(averageVector, entry.getValue());
            similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
        }

        similarities.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // 返回最相似的报告ID列表
        List<String> mostSimilarArticles = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, similarities.size()); i++) {
            mostSimilarArticles.add(similarities.get(i).getKey());
        }

        return mostSimilarArticles;
    }

    public double[] calculateAverageVector(List<double[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            return null;
        }
        
        int dimension = vectors.get(0).length;
        double[] avgVector = new double[dimension];
        final int vectorCount = vectors.size();
        
        for (double[] vector : vectors) {
            for (int i = 0; i < dimension; i++) {
                avgVector[i] += vector[i];
            }
        }
        
        for (int i = 0; i < dimension; i++) {
            avgVector[i] /= vectorCount;
        }
        
        return avgVector;
    }
}