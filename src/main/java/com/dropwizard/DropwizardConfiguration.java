package com.dropwizard;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 4/24/14
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class DropwizardConfiguration extends Configuration implements AssetsBundleConfiguration {
    private static final Logger LOGGER = Logger.getLogger(DropwizardConfiguration.class);

    public void init() {
        // TODO: Add initialization codes here
    }

    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}
