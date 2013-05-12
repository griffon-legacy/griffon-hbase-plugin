/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.plugins.hbase

import org.apache.hadoop.conf.Configuration

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class ConfigurationHolder {
    private static final String DEFAULT = 'default'
    private static final Object[] LOCK = new Object[0]
    private final Map<String, Configuration> configurations = [:]

    private static final ConfigurationHolder INSTANCE

    static {
        INSTANCE = new ConfigurationHolder()
    }

    static ConfigurationHolder getInstance() {
        INSTANCE
    }

    private ConfigurationHolder() {}

    String[] getConfigurationNames() {
        List<String> configNames = new ArrayList().addAll(configurations.keySet())
        configNames.toArray(new String[configNames.size()])
    }

    Configuration getConfiguration(String configName = DEFAULT) {
        if (isBlank(configName)) configName = DEFAULT
        retrieveConfiguration(configName)
    }

    void setConfiguration(String configName = DEFAULT, Configuration configuration) {
        if (isBlank(configName)) configName = DEFAULT
        storeConfiguration(configName, configuration)
    }

    boolean isConfigurationConnected(String configName) {
        if (isBlank(configName)) configName = DEFAULT
        retrieveConfiguration(configName) != null
    }
    
    void disconnectConfiguration(String configName) {
        if (isBlank(configName)) configName = DEFAULT
        storeConfiguration(configName, null)
    }

    Configuration fetchConfiguration(String configName) {
        if (isBlank(configName)) configName = DEFAULT
        Configuration configuration = retrieveConfiguration(configName)
        if (configuration == null) {
            GriffonApplication app = ApplicationHolder.application
            ConfigObject config = HBaseConnector.instance.createConfig(app)
            configuration = HBaseConnector.instance.connect(app, config, configName)
        }

        if (configuration == null) {
            throw new IllegalArgumentException("No such HBase configuration configuration for name $configName")
        }
        configuration
    }

    private Configuration retrieveConfiguration(String configName) {
        synchronized(LOCK) {
            configurations[configName]
        }
    }

    private void storeConfiguration(String configName, Configuration configuration) {
        synchronized(LOCK) {
            configurations[configName] = configuration
        }
    }
}
