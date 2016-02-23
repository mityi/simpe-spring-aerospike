package rda.aerospike.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Boot {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Boot.class, args);
    }
}