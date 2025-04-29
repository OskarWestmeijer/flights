package westmeijer.oskar.service.importer

import kotlinx.coroutines.CoroutineScope

interface ImportScheduler {

    fun start(scope: CoroutineScope)

}