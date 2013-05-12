/*
 * Copyright 2012-2013 the original author or authors.
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

package griffon.plugins.hbase;

import griffon.exceptions.GriffonException;
import griffon.util.CallableWithArgs;
import groovy.lang.Closure;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public abstract class AbstractHBaseProvider implements HBaseProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHBaseProvider.class);
    private static final String DEFAULT = "default";

    public <R> R withHBase(Closure<R> closure) {
        return withHBase(DEFAULT, closure);
    }

    public <R> R withHBase(String configName, Closure<R> closure) {
        if (isBlank(configName)) configName = DEFAULT;
        if (closure != null) {
            Configuration configuration = getConfiguration(configName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statement on '" + configName + "'");
            }
            return closure.call(configName, configuration);
        }
        return null;
    }

    public <R> R withHBase(CallableWithArgs<R> callable) {
        return withHBase(DEFAULT, callable);
    }

    public <R> R withHBase(String configName, CallableWithArgs<R> callable) {
        if (isBlank(configName)) configName = DEFAULT;
        if (callable != null) {
            Configuration configuration = getConfiguration(configName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statement on '" + configName + "'");
            }
            callable.setArgs(new Object[]{configName, configuration});
            return callable.call();
        }
        return null;
    }

    public <R> R withHTable(String tableName, Closure<R> closure) {
        return withHTable(DEFAULT, tableName, closure);
    }

    public <R> R withHTable(String configName, String tableName, Closure<R> closure) {
        if (isBlank(configName)) configName = DEFAULT;
        if (closure != null) {
            Configuration configuration = getConfiguration(configName);
            try {
                HTable table = new HTable(configuration, tableName);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Executing statement on '" + configName + "' and table '" + tableName + "'");
                }
                try {
                    return closure.call(configName, configuration, tableName, table);
                } finally {
                    table.close();
                }
            } catch (IOException ioe) {
                throw new GriffonException(ioe);
            }
        }
        return null;
    }

    public <R> R withHTable(String tableName, CallableWithArgs<R> callable) {
        return withHTable(DEFAULT, tableName, callable);
    }

    public <R> R withHTable(String configName, String tableName, CallableWithArgs<R> callable) {
        if (isBlank(configName)) configName = DEFAULT;
        if (callable != null) {
            Configuration configuration = getConfiguration(configName);
            try {
                HTable table = new HTable(configuration, tableName);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Executing statement on '" + configName + "' and table '" + tableName + "'");
                }
                try {
                    callable.setArgs(new Object[]{configName, configuration, tableName, table});
                    return callable.call();
                } finally {
                    table.close();
                }
            } catch (IOException ioe) {
                throw new GriffonException(ioe);
            }
        }
        return null;
    }

    protected abstract Configuration getConfiguration(String configName);
}