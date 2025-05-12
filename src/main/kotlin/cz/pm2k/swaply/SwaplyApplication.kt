package cz.pm2k.swaply

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SwaplyApplication

fun main(args: Array<String>) {
    runApplication<SwaplyApplication>(*args)
}
