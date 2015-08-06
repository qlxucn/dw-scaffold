package com.dropwizard;

import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.File;

import org.junit.ClassRule;

import com.google.common.io.Resources;

public abstract class AbstractControllerTest {

    @ClassRule
    public static final DropwizardAppRule<DropwizardConfiguration> RULE = new DropwizardAppRule<DropwizardConfiguration>(TestMain.class,
            resourceFilePath("dw-config-test.yml"));

    public static String resourceFilePath(String resourceClassPathLocation) {
        try {
            return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
