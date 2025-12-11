package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    //Метод addViewControllers позволяет нам добавить маршрут / и связать его с представлением "indexName".
    //  Это означает, что при обращении к корневому пути приложения (http://localhost:8080/"indexName"),
    // будет отображаться страница "indexName".
//т.е настраивает по умолчанию страницу (без этой настройки будет всегда ошибка при входе на эту ссылку)
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/admin").setViewName("users");
    }
}
