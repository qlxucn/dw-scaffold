package com.dropwizard;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:lib.xml"})
@ComponentScan(basePackages = { "com.dropwizard" })
public class SpringConfiguration {

}
