/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emr.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component("featureToggles")
public class FeatureToggleProperties {

    private static final String FEATURE_TOGGLE_PROPERTIES_FILE = "FEATURE_TOGGLE_PROPERTIES_FILE";
    private Log log = LogFactory.getLog(getClass());
    private String propertiesFile;

    FeatureToggleProperties() {
        propertiesFile = System.getenv(FEATURE_TOGGLE_PROPERTIES_FILE);
    }

    public boolean isFeatureEnabled(String key) {
        Properties toggles = loadToggles();
        return Boolean.parseBoolean(toggles.getProperty(key, "false"));
    }

    public Map<Object,Object> getToggleMap() {
        Properties toggles = loadToggles();
        return Collections.unmodifiableMap(toggles);
    }

    private Properties loadToggles() {
        Properties toggles = new Properties();
        try {
            if (propertiesFile != null) {
                toggles.load(new FileInputStream(propertiesFile));
            }
        }
        catch (IOException e) {
            log.error("File feature_toggles.properties not found. Error: ", e);
        }

        return toggles;
    }
}
