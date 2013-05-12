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

package griffon.plugins.hbase

import griffon.util.CallableWithArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 */
final class HBaseEnhancer {
    private static final String DEFAULT = 'default'
    private static final Logger LOG = LoggerFactory.getLogger(HBaseEnhancer)

    private HBaseEnhancer() {}
    
    static void enhance(MetaClass mc, HBaseProvider provider = DefaultHBaseProvider.instance) {
        if (LOG.debugEnabled) LOG.debug("Enhancing $mc with $provider")
        mc.withHBase = {Closure closure ->
            provider.withHBase(DEFAULT, closure)
        }
        mc.withHBase << {String configName, Closure closure ->
            provider.withHBase(configName, closure)
        }
        mc.withHBase << {CallableWithArgs callable ->
            provider.withHBase(DEFAULT, callable)
        }
        mc.withHBase << {String configName, CallableWithArgs callable ->
            provider.withHBase(configName, callable)
        }
        mc.withHTable = {String tableName, Closure closure ->
            provider.withHTable('default', tableName, closure)
        }
        mc.withHTable << {String configName, String tableName, Closure closure ->
            provider.withHTable(configName, tableName, closure)
        }
        mc.withHTable << {String tableName, CallableWithArgs callable ->
            provider.withHTable('default', tableName, callable)
        }
        mc.withHTable << {String configName, String tableName, CallableWithArgs callable ->
            provider.withHTable(configName, tableName, callable)
        }
    }
}