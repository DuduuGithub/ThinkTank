package service;

import java.util.List;
import java.util.ArrayList;

public class AiReportService {

    // 存储论文和描述
    private String query;
    private List<String> papers;

    // 控制每次生成的最大字数
    private int maxTokensPerCycle=7000;

    // 构造函数
    public AiReportService(String query, List<String> papers) {
        this.query = query;
        this.papers = papers;
    }

    // 生成最终的报告
    public String generateReport() throws Exception {
        String finalReport = new String();
        
        // 初始的描述作为报告的开头
        String currentInput = "这是第一次输入，尚无报告内容，生成时请将这句话忽略。";

        // 遍历每篇论文
        for (String paper : papers) {
            // 控制每次输入的字数，避免超出模型限制
            String paperToInput = paper.length() > maxTokensPerCycle ? paper.substring(0, maxTokensPerCycle) : paper;

            // 构建 AI prompt 来生成与描述相关的报告内容，并引用论文
            String prompt = buildPrompt(currentInput, paperToInput);

            // 调用 BigModelNew 生成当前部分的报告
            finalReport = BigModelNew.askQuestion(prompt);
            
            // 更新下一轮的输入：当前的报告
            currentInput = finalReport;

            // 可选：如果需要删去之前的内容，可以注释掉以下两行代码，恢复原始输入逻辑
            // currentInput = currentInput.substring(0, Math.min(currentInput.length(), maxTokensPerCycle)); // 限制最大字数
        }

        // 最终生成的报告
        return finalReport;
    }

    // 构建用于生成报告的 prompt
    private String buildPrompt(String currentInput, String paper) {
        // 假设模型要求一个格式化的提示来生成报告。以下 prompt 需要根据您的具体需求进一步调整。
        String prompt = "用户的问题如下："+query+"\n\n" +
                        "根据以下论文，针对用户提出的问题，在已有报告的基础上生成一份新报告，如果引用相关论文的部分需要加上引文标记。\n\n" +
                        "已有报告：\n" + currentInput + "\n\n" +
                        "论文内容：\n" + paper + "\n\n" +
                        "请在已有报告的基础上添加，生成一份新报告，如果你认为已有报告的一些内容与用户的问题相关性不大，可以删去，字数不能超出3000字，并确保引用论文时保持论文内容不变，并在报告中标注出处（例如：引用自[xx文章]）。";
        return prompt;
    }

    // 调用示例
    public static void main(String[] args) throws Exception {
        // 示例描述和论文内容
        String description = "本报告旨在探讨人工智能对劳动力市场中的作用。";
        List<String> papers = new ArrayList<>();
        papers.add("论文1：人工智能技术在教育领域的应用...");
        papers.add("论文2：人工智能在医疗行业的潜力...");
        papers.add("论文3：人工智能对劳动力市场的影响...");

        AiReportService aiReportService = new AiReportService(description, papers); 
        String finalReport = aiReportService.generateReport();

        System.out.println("生成的报告：\n" + finalReport);
    }
}
