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

import griffon.util.CallableWithArgs;
import groovy.lang.Closure;

import java.util.Map;

/**
 * @author Andres Almiray
 */
public class HBaseContributionAdapter implements HBaseContributionHandler {
    private static final String DEFAULT = "default";

    private HBaseProvider provider = DefaultHBaseProvider.getInstance();

    public void setHBaseProvider(HBaseProvider provider) {
        this.provider = provider != null ? provider : DefaultHBaseProvider.getInstance();
    }

    public HBaseProvider getHBaseProvider() {
        return provider;
    }

    public <R> R withHBase(Closure<R> closure) {
        return withHBase(DEFAULT, closure);
    }

    public <R> R withHBase(String configName, Closure<R> closure) {
        return provider.withHBase(configName, closure);
    }

    public <R> R withHBase(CallableWithArgs<R> callable) {
        return withHBase(DEFAULT, callable);
    }

    public <R> R withHBase(String configName, CallableWithArgs<R> callable) {
        return provider.withHBase(configName, callable);
    }

    public <R> R withHTable(String tableName, Closure<R> closure) {
        return withHTable(DEFAULT, tableName, closure);
    }

    public <R> R withHTable(String configName, String tableName, Closure<R> closure) {
        return provider.withHTable(configName, tableName, closure);
    }

    public <R> R withHTable(String tableName, CallableWithArgs<R> callable) {
        return withHTable(DEFAULT, tableName, callable);
    }

    public <R> R withHTable(String configName, String tableName, CallableWithArgs<R> callable) {
        return provider.withHTable(configName, tableName, callable);
    }
}