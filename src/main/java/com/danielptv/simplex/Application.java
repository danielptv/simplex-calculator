package com.danielptv.simplex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.danielptv.simplex.dev.Banner.TEXT;

@SpringBootApplication
@SuppressWarnings({"UncommentedMain", "HideUtilityClassConstructor"})
public class Application {
    public static void main(final String[] args) {
        final var app = new SpringApplication(Application.class);
        app.setBanner((environment, sourceClass, out) -> out.println(TEXT));
        app.run(args);
    }
}
