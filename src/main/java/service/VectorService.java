package service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import logger.SimpleLogger;

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
    private double[] readVectorFromFile(int documentId) throws IOException {
        File vectorFile = new File(VECTOR_DIR, documentId + ".txt");
        SimpleLogger.log("Reading vector from file: " + vectorFile.getAbsolutePath());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(vectorFile))) {
            String vectorStr = reader.readLine();
            String[] values = vectorStr.replaceAll("[\\[\\]]", "").split(",\\s*");
            return Arrays.stream(values)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        }
    }

    // 找到与给定文档最相似的前几个文章
    public List<Integer> findMostSimilarArticles(List<Integer> sourceDocIds, List<Integer> candidateDocIds, int topN) throws IOException {
        SimpleLogger.log("Finding similar articles for source docs: " + sourceDocIds);
        
        // 读取源文档的向量并计算平均向量
        List<double[]> sourceVectors = new ArrayList<>();
        for (Integer docId : sourceDocIds) {
            try {
                double[] vector = readVectorFromFile(docId);
                sourceVectors.add(vector);
            } catch (IOException e) {
                SimpleLogger.log("Error reading vector for document " + docId + ": " + e.getMessage());
            }
        }
        
        if (sourceVectors.isEmpty()) {
            SimpleLogger.log("No source vectors found");
            return new ArrayList<>();
        }

        // 计算平均向量
        double[] averageVector = calculateAverageVector(sourceVectors);
        
        // 计算候选文档的相似度
        Map<Integer, Double> similarities = new HashMap<>();
        for (Integer docId : candidateDocIds) {
            if (!sourceDocIds.contains(docId)) {  // 排除源文档
                try {
                    double[] vector = readVectorFromFile(docId);
                    double similarity = cosineSimilarity(averageVector, vector);
                    similarities.put(docId, similarity);
                } catch (IOException e) {
                    SimpleLogger.log("Error reading vector for candidate " + docId + ": " + e.getMessage());
                }
            }
        }

        // 排序并返回前topN个文档ID
        return similarities.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double[] calculateAverageVector(List<double[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            return null;
        }
        
        int dimension = vectors.get(0).length;
        double[] avgVector = new double[dimension];
        
        for (double[] vector : vectors) {
            for (int i = 0; i < dimension; i++) {
                avgVector[i] += vector[i];
            }
        }
        
        for (int i = 0; i < dimension; i++) {
            avgVector[i] /= vectors.size();
        }
        
        return avgVector;
    }
}