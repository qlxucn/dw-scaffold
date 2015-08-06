package com.dropwizard;

public class TestMain extends Main {
    @Override
    protected Class getSpringConfigurationClass() {
        return TestSpringConfiguration.class;
    }
}
