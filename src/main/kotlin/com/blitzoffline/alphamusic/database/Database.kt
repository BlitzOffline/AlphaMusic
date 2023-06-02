package com.blitzoffline.alphamusic.database

import com.blitzoffline.alphamusic.utils.EnvironmentVariables
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class Database(private val environmentVariables: EnvironmentVariables) {
    private val config = HikariConfig().apply {
        this.jdbcUrl = "jdbc:mysql://${environmentVariables.databaseHost}:${environmentVariables.databasePort}/${environmentVariables.databaseName}?useSsl=false&serverTimezone=UTC"
        this.driverClassName = "com.mysql.cj.jdbc.Driver"
        this.username = environmentVariables.databaseUsername
        this.password = environmentVariables.databasePassword
        this.maximumPoolSize = 5
    }
    private val dataSource = HikariDataSource(config)

    val instance by lazy {
        Database.connect(dataSource)
    }
}