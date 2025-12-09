package org.dows.gpt;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.util.PathUtil;
import org.dromara.autotable.springboot.EnableAutoTable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@EnableAutoTable // 开启自动建表
@EnableAsync // 开启异步处理
@EnableCaching // 开启缓存
@SpringBootApplication(scanBasePackages = {"org.dows.gpt", "org.dows.llm","org.dows.rade","org.dows.oss"})
//@EnableFeignClients() //开启Feign客户端
//@EnableFeignClients(basePackages={"org.dows.uim.feign","org.dows.uim.api"})
public class GptApplication {

    private static volatile ConfigurableApplicationContext context;
    private static ClassLoader mainThreadClassLoader;

    public static void main(String[] args) {
        // 设置JVM默认时区
        // 在应用启动时设置JVM默认时区
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        System.out.println(LocalDateTime.now());
        String property = System.getProperty("user.home");
        Dotenv dotenv = Dotenv.configure()
                .directory(property + File.separator + "env") // 指定 env 目录路径
                .ignoreIfMissing()
                .load();
        // 将 .env 文件中的键值对设为系统属性
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        mainThreadClassLoader = Thread.currentThread().getContextClassLoader();
        context = SpringApplication.run(GptApplication.class, args);
    }


    public Class<?> getAppClass() {
        return GptApplication.class;
    }

    /**
     * 通过关闭当前上下文并启动新上下文来重启应用程序。
     */
    public void restart(List<String> javaPathList) {
        // 从当前上下文获取应用程序参数
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        // 创建新线程来重启应用程序
        Thread thread = new Thread(() -> {
            try {
                // 关闭当前应用程序上下文
                context.close();
                // 等待上下文完全关闭
                while (context.isActive()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 加载动态生成的代码
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            javaPathList.forEach(javaPath -> {
                try {
                    classLoader.loadClass(PathUtil.getClassName(javaPath));
                } catch (ClassNotFoundException e) {
                    log.error("loadClassErr {}", javaPath, e);
                }
            });
            // 使用相同的参数运行Spring Boot应用程序并设置上下文
            context = SpringApplication.run(GptApplication.class, args.getSourceArgs());
        });
        // 设置线程的上下文类加载器
        thread.setContextClassLoader(mainThreadClassLoader);
        // 确保线程不是守护线程
        thread.setDaemon(false);
        // 启动线程
        thread.start();
    }
} 