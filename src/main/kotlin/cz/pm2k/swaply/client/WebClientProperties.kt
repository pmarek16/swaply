package cz.pm2k.swaply.client

abstract class WebClientProperties {

    lateinit var url: String
    var connectionTimeout: Long = 3000
    var readTimeout: Long = 3000
    var writeTimeout: Long = 3000

}
