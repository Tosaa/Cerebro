package redtoss.creativity.cerebro.data

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class StrategyProvider(private val assetManager: AssetManager, private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val existingStrategyFilename = "strategies.json"
    private val existingStrategyInputStream: InputStream
        get() = assetManager.open(existingStrategyFilename)
    private val cachedExistingResolvedStrategies = mutableListOf<Strategy>()

    private val customStrategyFilename = "custom_strategies.json"
    private val customStrategyInputStream: InputStream?
        get() = try {
            context.openFileInput(customStrategyFilename)
        } catch (_: FileNotFoundException) {
            // Expected until the user saves their first custom strategy.
            null
        } catch (e: IOException) {
            Log.e(TAG, "Could not open $customStrategyFilename", e)
            null
        }
    private val cachedCustomResolvedStrategies = mutableListOf<Strategy>()

    private val _resolvedStrategies = MutableSharedFlow<List<Strategy>>(replay = 1)
    val resolvedStrategies: Flow<List<Strategy>> = _resolvedStrategies.onStart { this.emit(resolveStrategies(true)) }

    suspend fun resolveStrategies(forceReload: Boolean = false): List<Strategy> {
        val existingStrategies = resolveStrategies(fileInputStream = existingStrategyInputStream, forceReload = forceReload, strategyCache = cachedExistingResolvedStrategies)
        val customStrategies =
            customStrategyInputStream?.let { resolveStrategies(fileInputStream = it, forceReload = forceReload, strategyCache = cachedCustomResolvedStrategies) }.orEmpty()
        return existingStrategies + customStrategies

    }

    fun addCustomStrategy(strategy: Strategy): Result<Unit> {
        Log.d(TAG, "addCustomStrategy(): $strategy")
        val existingStrategies = runBlocking(ioDispatcher) {
            customStrategyInputStream?.let { resolveStrategies(fileInputStream = it, forceReload = false, strategyCache = cachedCustomResolvedStrategies) }.orEmpty()
        }
        val newCustomStrategies = existingStrategies.filter { it.title == strategy.title } + listOf(strategy)

        return try {
            with(OutputStreamWriter(context.openFileOutput(customStrategyFilename, Context.MODE_PRIVATE))) {
                val strategiesAsString = Json.encodeToString<List<Strategy>>(newCustomStrategies)
                write(strategiesAsString)
                flush()
                close()
            }
            cachedCustomResolvedStrategies.removeIf { it.title == strategy.title }
            cachedCustomResolvedStrategies.add(strategy)
            runBlocking {
                _resolvedStrategies.emit(resolveStrategies())
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private suspend fun resolveStrategies(fileInputStream: InputStream, forceReload: Boolean = false, strategyCache: MutableList<Strategy>): List<Strategy> {
        return CoroutineScope(Dispatchers.IO).async {
            if (!forceReload && strategyCache.isNotEmpty()) {
                return@async strategyCache
            }

            val rawJson = try {
                BufferedReader(
                    InputStreamReader(
                        fileInputStream,
                        "UTF-8"
                    )
                ).readText()
            } catch (e: Exception) {
                Log.e(TAG, "Could not read strategy JSON", e)
                ""
            }
            return@async try {
                Json.decodeFromString<List<Strategy>>(rawJson).also {
                    strategyCache.addAll(it)
                }
            } catch (e: Exception) {
                // A SerializationException here on a minified build usually means R8
                // stripped the generated serializers; check proguard-rules.pro.
                Log.e(TAG, "Could not parse strategy JSON", e)
                emptyList<Strategy>()
            }
        }.await()
    }

    private companion object {
        const val TAG = "StrategyProvider"
    }
}
