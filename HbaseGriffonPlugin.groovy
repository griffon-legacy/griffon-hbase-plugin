/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */
class HbaseGriffonPlugin {
    // the plugin version
    String version = '0.1'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '0.9.5 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache Software License 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-hbase-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'HBase support'
    String description = '''
The HBase plugin enables lightweight access to [HBase][1] databases.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * HBaseConfig.groovy - contains the database definitions.
 * BootstrapHBase.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

Two dynamic methods named `withHBase` and `withHTable` will be injected into all controllers,
giving you access to a `org.apache.hadoop.conf.Configuration` object, with which you'll be able
to make calls to the database. In particular `withHTable` will automatically close the table for your.
Remember to make all database calls off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.
These methods are aware of multiple databases. If no configName is specified when calling
it then the default database will be selected. Here are two example usages, the first
queries against the default database while the second queries a database whose name has
been configured as 'internal'

	package sample
	class SampleController {
	    def queryAllDatabases = {
	        withHBase { configName, configuration -> ... }
	        withHBase('internal') { configName, configuration -> ... }
	    }
	}

Querying a particular table can be done in the following way

	package sample
	class SampleController {
	    def queryPersonTable = {
	        withHTable('person') { configName, configuration, tableName, htable -> ... }
	        withHTable('default', 'person') { configName, configuration, tableName, htable -> ... }
	    }
	}	
	
These methods are also accessible to any component through the singleton `griffon.plugins.hbase.HBaseConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`HBaseEnhancer.enhance(metaClassInstance, hbaseProviderInstance)`.

Configuration
-------------
### Dynamic method injection

Dynamic methods will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.hbase.injectInto = ['controller', 'service']

### Events

The following events will be triggered by this addon

 * HBaseConnectStart[config, configName] - triggered before connecting to the database
 * HBaseConnectEnd[configName, configuration] - triggered after connecting to the database
 * HBaseDisconnectStart[config, configName, configuration] - triggered before disconnecting from the database
 * HBaseDisconnectEnd[config, configName] - triggered after disconnecting from the database

### Multiple Stores

The config file `HBaseConfig.groovy` defines a default datastore block. As the name
implies this is the database used by default, however you can configure named datastores
by adding a new config block. For example connecting to a datastore whose name is 'internal'
can be done in this way

    clients {
        internal {
            resources = [
                '/opt/hbase/conf/hbase-site.xml'
            ]
        }
    }

This block can be used inside the `environments()` block in the same way as the
default datastore block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/hbase][2]

Testing
-------
Dynamic methods will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `HBaseEnhancer.enhance(metaClassInstance, hbaseProviderInstance)` where 
`hbaseProviderInstance` is of type `griffon.plugins.hbase.HBaseProvider`. The contract for this interface looks like this

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

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyHBaseProvider implements HBaseProvider {
        Object withHBase(String configName = 'default', Closure closure) { null }
        public <T> T withHBase(String configName = 'default', CallableWithArgs<T> callable) { null }
        Object withHTable(String configName = 'default', String tableName, Closure closure) { null }
        public <T> T withHTable(String configName = 'default', String tableName, CallableWithArgs<T> callable) { null }     
    }
    
This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            HBaseEnhancer.enhance(service.metaClass, new MyHBaseProvider())
            // exercise service methods
        }
    }


[1]: http://hbase.apache.org/
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/hbase
'''
}
