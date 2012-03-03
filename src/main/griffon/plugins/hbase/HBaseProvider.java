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

package griffon.plugins.hbase;

import griffon.util.CallableWithArgs;
import groovy.lang.Closure;

/**
 * @author Andres Almiray
 */
public interface HBaseProvider {
    Object withHBase(Closure closure);

    Object withHBase(String configName, Closure closure);

    <T> T withHBase(CallableWithArgs<T> callable);

    <T> T withHBase(String configName, CallableWithArgs<T> callable);

    Object withHTable(String tableName, Closure closure);

    Object withHTable(String configName, String tableName, Closure closure);

    <T> T withHTable(String tableName, CallableWithArgs<T> callable);

    <T> T withHTable(String configName, String tableName, CallableWithArgs<T> callable);
}
