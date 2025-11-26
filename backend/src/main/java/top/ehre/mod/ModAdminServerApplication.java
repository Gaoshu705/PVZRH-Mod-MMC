package top.ehre.mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class ModAdminServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ModAdminServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ModAdminServerApplication.class, args);
    }


    @Value("${file.localPath}")
    private String localPath;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            File localDir = new File(localPath);
            if (!localDir.exists()) localDir.mkdirs();
            log.info("ModAdminServer init success");
        };
    }
}
