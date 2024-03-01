import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import westmeijer.oskar.Scheduler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class SchedulerTest {

    private val originalOut = System.out
    private val outputCapture = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputCapture))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun testSchedulerInitialization() {
        // Redirect stdout for capturing logs
        val logs = outputCapture.use {
            // Call the Scheduler initialization code
            Scheduler


            println("Waiting 2 seconds")
            Thread.sleep(2000)

            // Simulate shutting down the application
            Scheduler.shutdownSignal.complete(Unit)

            // Get captured logs
            it.toString().trim()
        }

        // Verify that the logs contain expected messages
        assertTrue(logs.contains("Redis url:"))
        assertTrue(logs.contains("Launching scheduler with SADD."))
        assertTrue(logs.contains("Adding to set: ${Scheduler.SCHEDULER_SET}, msg: ${Scheduler.EXPECTED_MSG}"))
    }
}
