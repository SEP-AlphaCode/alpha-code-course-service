package site.alphacode.alphacodecourseservice;

import org.springframework.boot.SpringApplication;

public class TestAlphaCodeCourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(AlphaCodeCourseServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
