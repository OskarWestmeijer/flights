package westmeijer.oskar.service.importer

import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object FlightsImportScheduler : ImportScheduler {

    private val log = KtorSimpleLogger("westmeijer.oskar.redis.westmeijer.oskar.service.importer.FlightsImportScheduler")

    override fun start(scope: CoroutineScope) {

        scope.launch {
            log.info("Starting scheduler loop.")
            while (isActive) {
                try {
                    FlightsImportService.import()
                    delay(600000)
                } catch (e: Exception) {
                    log.error("Scheduled run threw exception.", e)
                }
            }
            log.info("Exiting scheduler loop.")
        }

    }
}