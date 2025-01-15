package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import logger.SimpleLogger;

public class ClsTokenGenerater {

    // 类的属性
    private String article;  // 存储文章内容
    private String outputFileName;  // 存储输出文件名
    private static final String VECTOR_DIR = "D:\\java_hm_work\\tokens\\";
    private static final String PYTHON_SCRIPT = "D:\\java_hm_work\\getClsToken.py";
    private static final String PYTHON_PATH = "D:\\java_hm_work\\python\\python.exe";

    // 构造函数，初始化文章内容和输出文件名
    public ClsTokenGenerater(String article, String outputFileName) {
        this.article = article;
        this.outputFileName = outputFileName;
        SimpleLogger.log("Vector directory: " + VECTOR_DIR);
        SimpleLogger.log("Python interpreter: " + PYTHON_PATH);
        SimpleLogger.log("Python script path: " + PYTHON_SCRIPT);
        
        // 验证所有路径
        validatePaths();
    }

    private void validatePaths() {
        File pythonExe = new File(PYTHON_PATH);
        if (!pythonExe.exists()) {
            SimpleLogger.log("ERROR: Python interpreter not found at: " + PYTHON_PATH);
        }
        
        File scriptFile = new File(PYTHON_SCRIPT);
        if (!scriptFile.exists()) {
            SimpleLogger.log("ERROR: Python script not found at: " + PYTHON_SCRIPT);
        }
        
        File vectorDir = new File(VECTOR_DIR);
        if (!vectorDir.exists()) {
            vectorDir.mkdirs();
        }

        // 添加环境变量检查
        logEnvironmentVariables();
    }

    private void logEnvironmentVariables() {
        SimpleLogger.log("=== Environment Variables ===");
        // 检查系统环境变量
        SimpleLogger.log("System PATH: " + System.getenv("PATH"));
        SimpleLogger.log("PYTHON_HOME: " + System.getenv("PYTHON_HOME"));
        SimpleLogger.log("PYTHONPATH: " + System.getenv("PYTHONPATH"));
        
        // 检查Java属性
        SimpleLogger.log("=== Java Properties ===");
        SimpleLogger.log("Java user.dir: " + System.getProperty("user.dir"));
        SimpleLogger.log("Java java.library.path: " + System.getProperty("java.library.path"));
        
        // 尝试执行Python版本检查
        try {
            ProcessBuilder versionCheck = new ProcessBuilder(PYTHON_PATH, "--version");
            Process process = versionCheck.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String version = reader.readLine();
                SimpleLogger.log("Python version check: " + version);
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String error = reader.readLine();
                if (error != null) {
                    SimpleLogger.log("Python version check error: " + error);
                }
            }
            
            int exitCode = process.waitFor();
            SimpleLogger.log("Python version check exit code: " + exitCode);
            
        } catch (IOException | InterruptedException e) {
            SimpleLogger.log("Failed to check Python version: " + e.getMessage());
        }
        
        // 检查Python包
        try {
            ProcessBuilder packageCheck = new ProcessBuilder(
                PYTHON_PATH,
                "-c",
                "import sys; import transformers; print('Python path:', sys.executable); print('transformers version:', transformers.__version__)"
            );
            Process process = packageCheck.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    SimpleLogger.log("Package check: " + line);
                }
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    SimpleLogger.log("Package check error: " + line);
                }
            }
            
            int exitCode = process.waitFor();
            SimpleLogger.log("Package check exit code: " + exitCode);
            
        } catch (IOException | InterruptedException e) {
            SimpleLogger.log("Failed to check Python packages: " + e.getMessage());
        }
        
        SimpleLogger.log("=== End Environment Check ===");
    }

    public String generateClsToken() {
        StringBuilder output = new StringBuilder();
        File tempFile = null;

        SimpleLogger.log("Starting vector generation for file: " + outputFileName);
        SimpleLogger.log("Vector directory: " + VECTOR_DIR);

        // 使用绝对路径创建目录
        File customDir = new File(VECTOR_DIR);
        if (!customDir.exists()) {
            boolean created = customDir.mkdirs();
            SimpleLogger.log("Creating vector directory: " + (created ? "success" : "failed"));
        }

        try {
            tempFile = File.createTempFile("article", ".txt", customDir);
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(article);
            }
            
            File outputFile = new File(VECTOR_DIR, outputFileName);
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                    PYTHON_PATH,
                    PYTHON_SCRIPT,
                    tempFile.getAbsolutePath(),
                    outputFile.getAbsolutePath());
            
            processBuilder.directory(new File(PYTHON_SCRIPT).getParentFile());
            
            String command = String.join(" ", processBuilder.command());
            SimpleLogger.log("Executing command: " + command);
            
            Process process = processBuilder.start();
            
            // 创建两个独立的线程来读取输出，避免缓冲区阻塞
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        SimpleLogger.log("Python stdout: " + line);
                        output.append(line).append("\n");
                    }
                } catch (IOException e) {
                    SimpleLogger.log("Error reading Python stdout: " + e.getMessage());
                }
            });

            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        SimpleLogger.log("Python stderr: " + line);
                    }
                } catch (IOException e) {
                    SimpleLogger.log("Error reading Python stderr: " + e.getMessage());
                }
            });

            // 启动线程
            outputThread.start();
            errorThread.start();

            // 等待进程完成
            int exitCode = process.waitFor();
            
            // 等待线程完成
            outputThread.join();
            errorThread.join();

            if (exitCode == 0) {
                SimpleLogger.log("Vector generation successful for file: " + outputFileName);
                if (outputFile.exists()) {
                    SimpleLogger.log("Vector file created successfully at: " + outputFile.getAbsolutePath());
                    // 记录向量文件的内容
                    try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                        String vectorContent = reader.readLine();
                        SimpleLogger.log("Vector content: " + vectorContent);
                    }
                } else {
                    SimpleLogger.log("WARNING: Vector file not found after generation: " + outputFile.getAbsolutePath());
                }
            } else {
                SimpleLogger.log("Vector generation failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            SimpleLogger.log("Error during vector generation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                SimpleLogger.log("Temporary file cleanup: " + (deleted ? "success" : "failed"));
            }
        }

        return output.toString();
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
