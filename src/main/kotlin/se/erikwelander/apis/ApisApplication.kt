package se.erikwelander.apis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages=["se.erikwelander.apis.configuration"])
class ApisApplication {
	companion object {
		const val APPLICATION_URL = "https://api.d3ff.se"
		const val UPLOAD_PATH = "C:/Users/kira/Desktop/usb/uploads"
	}
}

fun main(args: Array<String>) {
	val ctx = runApplication<ApisApplication>(*args)
}
