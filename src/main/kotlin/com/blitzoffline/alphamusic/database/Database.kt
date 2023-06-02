package com.blitzoffline.alphamusic.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object Database {
    private val dbHost = System.getenv("ALPHAMUSIC_MYSQL_HOST")
    private val dbName = System.getenv("ALPHAMUSIC_MYSQL_DATABASE")
    private val dbUsername = System.getenv("ALPHAMUSIC_MYSQL_USERNAME")
    private val dbPassword = System.getenv("ALPHAMUSIC_MYSQL_PASSWORD")

    private val config = HikariConfig().apply {
        jdbcUrl         = "jdbc:mysql://$dbHost/$dbName"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username        = dbUsername
        password        = dbPassword
        maximumPoolSize = 5
    }
    private val dataSource = HikariDataSource(config)

    val instance by lazy {
        Database.connect(dataSource)
    }
}