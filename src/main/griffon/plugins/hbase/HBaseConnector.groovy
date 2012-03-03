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
import griffon.util.CallableWithArgs
import griffon.util.Environment
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
@Singleton
final class HBaseConnector implements HBaseProvider {
    private bootstrap

    private static final Logger LOG = LoggerFactory.getLogger(HBaseConnector)

    Object withHTable(String configName = 'default', String storeName, Closure closure) {
        ConfigurationHolder.instance.withHTable(configName, storeName, closure)
    }

    public <T> T withHTable(String configName = 'default', String storeName, CallableWithArgs<T> callable) {
        return ConfigurationHolder.instance.withHTable(configName, storeName, callable)
    }

    Object withHBase(String configName = 'default', Closure closure) {
        ConfigurationHolder.instance.withHBase(configName, closure)
    }

    public <T> T withHBase(String configName = 'default', CallableWithArgs<T> callable) {
        return ConfigurationHolder.instance.withHBase(configName, callable)
    }

    // ======================================================

    ConfigObject createConfig(GriffonApplication app) {
        def clientClass = app.class.classLoader.loadClass('HBaseConfig')
        new ConfigSlurper(Environment.current.name).parse(clientClass)
    }

    private ConfigObject narrowConfig(ConfigObject config, String configName) {
        return configName == 'default' ? config.datastore : config.datastores[configName]
    }

    Configuration connect(GriffonApplication app, ConfigObject config, String configName = 'default') {
        if (ConfigurationHolder.instance.isConfigurationConnected(configName)) {
            return ConfigurationHolder.instance.getConfiguration(configName)
        }

        config = narrowConfig(config, configName)
        app.event('HBaseConnectStart', [config, configName])
        Configuration configuration = startHBase(config)
        ConfigurationHolder.instance.setConfiguration(configName, configuration)
        bootstrap = app.class.classLoader.loadClass('BootstrapHBase').newInstance()
        bootstrap.metaClass.app = app
        bootstrap.init(configName, configuration)
        app.event('HBaseConnectEnd', [configName, configuration])
        configuration
    }

    void disconnect(GriffonApplication app, ConfigObject config, String configName = 'default') {
        if (ConfigurationHolder.instance.isConfigurationConnected(configName)) {
            config = narrowConfig(config, configName)
            Configuration configuration = ConfigurationHolder.instance.getConfiguration(configName)
            app.event('HBaseDisconnectStart', [config, configName, configuration])
            bootstrap.destroy(configName, configuration)
            // stopHBase(config, configuration)
            app.event('HBaseDisconnectEnd', [config, configName])
            ConfigurationHolder.instance.disconnectConfiguration(configName)
        }
    }

    private Configuration startHBase(ConfigObject config) {
        Configuration hadoopConfig = new Configuration()
        for (entry in config) {
            if (entry.key == 'resources') {
                for (resource in entry.value) {
                    hadoopConfig.addResource(resource)
                }
            } else {
                hadoopConfig.set(entry.key, String.valueOf(entry.value))
            }
        }
        return HBaseConfiguration.create(hadoopConfig)
    }

    /*
    private void stopHBase(ConfigObject config, Configuration configuration) {

    }
    */
}
