/*
 * Copyright 2012 the original author or authors.
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

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import griffon.util.CallableWithArgs
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.HTable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
@Singleton
class ConfigurationHolder implements HBaseProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationHolder)
    private static final Object[] LOCK = new Object[0]
    private final Map<String, Configuration> configurations = [:]

    Object withHBase(String configName = 'default', Closure closure) {
        Configuration configuration = fetchConfiguration(configName)
        if (LOG.debugEnabled) LOG.debug("Executing statement on '$configName'")
        return closure(configName, configuration)
    }

    public <T> T withHBase(String configName = 'default', CallableWithArgs<T> callable) {
        Configuration configuration = fetchConfiguration(configName)
        if (LOG.debugEnabled) LOG.debug("Executing statement on '$configName'")
        callable.args = [configName, configuration] as Object[]
        return callable.call()
    }

    Object withHTable(String configName = 'default', String tableName, Closure closure) {
        Configuration configuration = fetchConfiguration(configName)
        HTable table = new HTable(configuration, tableName)
        if (LOG.debugEnabled) LOG.debug("Executing statement on '$configName' and table '$tableName'")
        return closure(configName, tableName, configuration, table)
    }

    public <T> T withHTable(String configName = 'default', String tableName, CallableWithArgs<T> callable) {
        Configuration configuration = fetchConfiguration(configName)
        HTable table = new HTable(configuration, tableName)
        if (LOG.debugEnabled) LOG.debug("Executing statement on '$configName' and table '$tableName'")
        callable.args = [configName, tableName, configuration, table] as Object[]
        return callable.call()
    }

    String[] getConfigurationNames() {
        List<String> configNames = new ArrayList().addAll(configurations.keySet())
        configNames.toArray(new String[configNames.size()])
    }

    Configuration getConfiguration(String configName = 'default') {
        if (isBlank(configName)) configName = 'default'
        retrieveConfiguration(configName)
    }

    void setConfiguration(String configName = 'default', Configuration configuration) {
        if (isBlank(configName)) configName = 'default'
        storeConfiguration(configName, configuration)
    }

    boolean isConfigurationConnected(String configName) {
        if (isBlank(configName)) configName = 'default'
        retrieveConfiguration(configName) != null
    }

    void disconnectConfiguration(String configName) {
        if (isBlank(configName)) configName = 'default'
        storeConfiguration(configName, null)
    }

    private Configuration fetchConfiguration(String configName) {
        Configuration configuration = retrieveConfiguration(configName)
        if (configuration == null) {
            GriffonApplication app = ApplicationHolder.application
            ConfigObject config = HBaseConnector.instance.createConfig(app)
            configuration = HBaseConnector.instance.connect(app, config, configName)
        }

        if (configuration == null) {
            throw new IllegalArgumentException("No such Configuration configuration for name $configName")
        }
        configuration
    }

    private void storeConfiguration(String configName, Configuration configuration) {
        synchronized (LOCK) {
            configurations[configName] = configuration
        }
    }

    private Configuration retrieveConfiguration(String tableName) {
        synchronized (LOCK) {
            configurations[tableName]
        }
    }
}
