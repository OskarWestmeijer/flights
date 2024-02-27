package westmeijer.oskar

fun main() {
    println("Started application")
    Scheduler
    while (true) {
        // Keep the main function running indefinitely
        Thread.sleep(Long.MAX_VALUE)
    }

    println("Stopping application")
}