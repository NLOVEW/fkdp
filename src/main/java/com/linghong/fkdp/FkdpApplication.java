package com.linghong.fkdp;

import com.linghong.fkdp.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@PropertySource("classpath:application.properties")
@MapperScan("com.linghong.fkdp.mapper")
@EntityScan("com.linghong.fkdp.*")
@EnableJpaRepositories("com.linghong.fkdp.*")
@SpringBootApplication
@EnableScheduling
public class FkdpApplication {
    @Bean
    public SpringUtil getSpringUtil() {
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(FkdpApplication.class, args);
    }
}
