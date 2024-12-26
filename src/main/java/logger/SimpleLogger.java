package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

    // 日志文件路径
    private static final String LOG_FILE_PATH = "D:\\logs\\app.log";

    // 写入日志的方法
    public static void log(String message) {
        try {
            // 获取当前时间
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logMessage = timestamp + " - " + message;

            // 创建文件对象
            File logFile = new File(LOG_FILE_PATH);

            // 如果文件不存在，则创建文件
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();  // 确保目录存在
                logFile.createNewFile();  // 创建文件
            }

            // 使用 BufferedWriter 进行写入操作
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(logMessage);
                writer.newLine();  // 换行
            }
        } catch (IOException e) {
            e.printStackTrace();  // 打印异常信息
        }
    }

    public static void main(String[] args) {
        // 测试写入日志
        log("This is a test log message.");
        log("Another log entry.");
    }
}
