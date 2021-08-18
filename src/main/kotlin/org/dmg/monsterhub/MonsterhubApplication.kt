package org.dmg.monsterhub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MonsterhubApplication

fun main(args: Array<String>) {
  runApplication<MonsterhubApplication>(*args)
}
