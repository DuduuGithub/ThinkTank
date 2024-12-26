package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClsTokenGenerater {

    // 类的属性
    private String article;  // 存储文章内容
    private String outputFileName;  // 存储输出文件名

    // 构造函数，初始化文章内容和输出文件名
    public ClsTokenGenerater(String article, String outputFileName) {
        this.article = article;
        this.outputFileName = outputFileName;
    }

    public String generateClsToken() {
        StringBuilder output = new StringBuilder();
        File tempFile = null;

        // 指定目录
        File customDir = new File("webapp-db\\src\\main\\java\\com\\thinktank\\clsToken\\"); // 修改为你希望的目录
        if (!customDir.exists()) {
            customDir.mkdirs(); // 如果目录不存在，则创建
        }

        try {
            // 创建临时文件，临时文件会生成在clsToken目录下
            tempFile = File.createTempFile("article", ".txt", customDir);
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(article); // 将文章内容写入临时文件
            }
            
            // 输出文件位置在clsToken/tokens目录下
            File outputFile = new File("webapp-db\\src\\main\\java\\com\\thinktank\\clsToken\\tokens\\", outputFileName);
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python", "webapp-db\\src\\main\\java\\com\\thinktank\\clsToken\\getClsToken.py",
                    tempFile.getAbsolutePath(),
                    outputFile.getAbsolutePath());
            
            // 启动进程
            Process process = processBuilder.start();
            
            // 读取Python脚本的标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 读取Python脚本的错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);  // 输出错误信息
            }

            // 等待进程完成并获取退出码
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return output.toString(); // 返回Python脚本的输出
    }


    //可以用来测试的main方法
    public static void main(String[] args) {
        // 创建 ClsTokenGenerater 的实例，并传入文章和输出文件名
        String article = "这是测试文章内容，包含一些要提取的token。";  // 示例输入文本
        String outputFileName = "cls_token_output.txt";  // 输出文件名

        // 创建生成器实例并调用 generateClsToken 方法
        ClsTokenGenerater clsTokenGenerater = new ClsTokenGenerater(article, outputFileName);
        System.out.println("CLS token获取中...");
        
        // 调用 generateClsToken 方法生成 CLS token
        String result = clsTokenGenerater.generateClsToken();
        System.out.println("Python脚本输出：\n" + result);  // 打印Python脚本的输出
    }
}
