/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lombok.core.handlers;

import lombok.core.BaseConstants;
import lombok.core.util.MethodDescriptor;

import static lombok.core.util.MethodDescriptor.args;
import static lombok.core.util.MethodDescriptor.type;
import static lombok.core.util.MethodDescriptor.typeParams;

/**
 * @author Andres Almiray
 */
public interface HBaseAwareConstants extends BaseConstants {
    String HBASE_PROVIDER_TYPE = "griffon.plugins.hbase.HBaseProvider";
    String DEFAULT_HBASE_PROVIDER_TYPE = "griffon.plugins.hbase.DefaultHBaseProvider";
    String HBASE_CONTRIBUTION_HANDLER_TYPE = "griffon.plugins.hbase.HBaseContributionHandler";
    String HBASE_PROVIDER_FIELD_NAME = "this$hbaseProvider";
    String METHOD_GET_HBASE_PROVIDER = "getHBaseProvider";
    String METHOD_SET_HBASE_PROVIDER = "setHBaseProvider";
    String METHOD_WITH_HBASE = "withHBase";
    String METHOD_WITH_HTABLE = "withHTable";
    String PROVIDER = "provider";

    MethodDescriptor[] METHODS = new MethodDescriptor[] {
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HBASE,
            args(type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HBASE,
            args(
                type(JAVA_LANG_STRING),
                type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HBASE,
            args(type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HBASE,
            args(
                type(JAVA_LANG_STRING),
                type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HTABLE,
            args(
                type(JAVA_LANG_STRING),
                type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HTABLE,
            args(
                type(JAVA_LANG_STRING),
                type(JAVA_LANG_STRING),
                type(GROOVY_LANG_CLOSURE, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HTABLE,
            args(
                type(JAVA_LANG_STRING),
                type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        ),
        MethodDescriptor.method(
            type(R),
            typeParams(R),
            METHOD_WITH_HTABLE,
            args(
                type(JAVA_LANG_STRING),
                type(JAVA_LANG_STRING),
                type(GRIFFON_UTIL_CALLABLEWITHARGS, R))
        )
    };
}
