griffon.project.dependency.resolution = {
    inherits "global"
    log "warn"
    repositories {
        griffonHome()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        compile('org.apache.hbase:hbase:0.94.6.1',
                'org.apache.zookeeper:zookeeper:3.4.5') {
            transitive = false
        }
        compile('org.apache.hadoop:hadoop-core:1.1.2') {
            excludes('commons-cli', 'jetty', 'jetty-util', 'jasper-runtime', 'jasper-compiler',
                     'jsp-api-2.1', 'jsp-2.1', 'commons-el', 'jets3t', 'kfs', 'hsqldb', 'commons-logging')
            exclude module: 'org.eclispse.jdt', name: 'core'
        }
        compile 'commons-lang:commons-lang:2.6',
                'com.google.guava:guava:14.0.1',
                'com.google.protobuf:protobuf-java:2.5.0'
        build('org.eclipse.jdt:org.eclipse.jdt.core:3.6.0.v_A58') {
            export = false
        }
        String lombokIdea = '0.5'
        build("de.plushnikov.lombok-intellij-plugin:processor-api:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:processor-core:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:intellij-facade-factory:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:intellij-facade-api:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:intellij-facade-9:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:intellij-facade-10:$lombokIdea",
              "de.plushnikov.lombok-intellij-plugin:intellij-facade-11:$lombokIdea") {
            export = false
            transitive = false
        }
        String ideaVersion = '11.1.4'
        build("org.jetbrains.idea:idea-openapi:$ideaVersion",
              "org.jetbrains.idea:extensions:$ideaVersion",
              "org.jetbrains.idea:util:$ideaVersion",
              "org.jetbrains.idea:annotations:$ideaVersion") {
            export = false
        }
    }
}

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error 'org.codehaus.griffon',
          'org.springframework',
          'org.apache.karaf',
          'groovyx.net'
    warn  'griffon'
}