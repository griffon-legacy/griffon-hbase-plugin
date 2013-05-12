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

import griffon.core.GriffonApplication
import griffon.util.ConfigUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration

/**
 * @author Andres Almiray
 */
@Singleton
final class HBaseConnector {
    private static final String DEFAULT = 'default'
    private static final Logger LOG = LoggerFactory.getLogger(HBaseConnector)
    private bootstrap

    ConfigObject createConfig(GriffonApplication app) {
        if (!app.config.pluginConfig.hbase) {
            app.config.pluginConfig.hbase = ConfigUtils.loadConfigWithI18n('HBaseConfig')
        }
        app.config.pluginConfig.hbase
    }

    private ConfigObject narrowConfig(ConfigObject config, String configName) {
        if (config.containsKey('database') && configName == DEFAULT) {
            return config.database
        } else if (config.containsKey('databases')) {
            return config.databases[configName]
        }
        return config
    }

    Configuration connect(GriffonApplication app, ConfigObject config, String configName = DEFAULT) {
        if (ConfigurationHolder.instance.isConfigurationConnected(configName)) {
            return ConfigurationHolder.instance.getConfiguration(configName)
        }

        config = narrowConfig(config, configName)
        app.event('HBaseConnectStart', [config, configName])
        Configuration configuration = startHBase(config)
        ConfigurationHolder.instance.setConfiguration(configName, configuration)
        bootstrap = app.class.classLoader.loadClass('BootstrapHBase').newInstance()
        bootstrap.metaClass.app = app
        resolveHBaseProvider(app).withHBase { cn, c -> bootstrap.init(cn, c) }
        app.event('HBaseConnectEnd', [configName, configuration])
        configuration
    }

    void disconnect(GriffonApplication app, ConfigObject config, String configName = DEFAULT) {
        if (ConfigurationHolder.instance.isConfigurationConnected(configName)) {
            config = narrowConfig(config, configName)
            Configuration configuration = ConfigurationHolder.instance.getConfiguration(configName)
            app.event('HBaseDisconnectStart', [config, configName, configuration])
            resolveHBaseProvider(app).withHBase { cn, c -> bootstrap.destroy(cn, c) }
            app.event('HBaseDisconnectEnd', [config, configName])
            ConfigurationHolder.instance.disconnectConfiguration(configName)
        }
    }

    HBaseProvider resolveHBaseProvider(GriffonApplication app) {
        def hbaseProvider = app.config.hbaseProvider
        if (hbaseProvider instanceof Class) {
            hbaseProvider = hbaseProvider.newInstance()
            app.config.hbaseProvider = hbaseProvider
        } else if (!hbaseProvider) {
            hbaseProvider = DefaultHBaseProvider.instance
            app.config.hbaseProvider = hbaseProvider
        }
        hbaseProvider
    }

    private Configuration startHBase(ConfigObject config) {
        Configuration hadoopConfig = HBaseConfiguration.create()
        for (entry in config) {
            if (entry.key == 'resources') {
                for (resource in entry.value) {
                    hadoopConfig.addResource(resource)
                }
            } else {
                hadoopConfig.set(entry.key, String.valueOf(entry.value))
            }
        }
        hadoopConfig
    }
}