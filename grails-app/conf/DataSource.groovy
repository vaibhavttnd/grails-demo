dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    username = "root"
    password = "igdefault"
//    logSql = true
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {
        dataSource {
                 dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
                 url = "jdbc:mysql://localhost:3306/tweetamp"
            //url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://localhost:3306/tweetamp"
            String mysqlUrl = System.getenv("DATABASE_URL")
            if (mysqlUrl) {
                println ">>>>>> DATABASE_URL: ${mysqlUrl}"
                URI dbUri = new URI(mysqlUrl);
                username = dbUri.userInfo.split(":")[0]
                password = dbUri.userInfo.split(":")[1]
                String databaseUrl = "jdbc:${dbUri.scheme}://${dbUri.host}${dbUri.path}"
                if (dbUri.port > 0) {
                    databaseUrl += ":${dbUri.port}"
                }
                String query = dbUri.query ?: "reconnect=true"
                query += "&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
                databaseUrl += "?${query}"
                url = databaseUrl
            }
            properties {
               // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
               jmxEnabled = true
               initialSize = 5
               maxActive = 50
               minIdle = 5
               maxIdle = 25
               maxWait = 10000
               maxAge = 10 * 60000
               timeBetweenEvictionRunsMillis = 5000
               minEvictableIdleTimeMillis = 60000
               validationQuery = "SELECT 1"
               validationQueryTimeout = 3
               validationInterval = 15000
               testOnBorrow = true
               testWhileIdle = true
               testOnReturn = false
               jdbcInterceptors = "ConnectionState"
               defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
}
