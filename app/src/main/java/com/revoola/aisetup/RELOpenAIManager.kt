package com.revoola.aisetup

import android.util.Log
import com.revoola.utils.RLConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.revoola.BuildConfig
import com.revoola.commonobject.RLTools

object RELOpenAIManager {

    private const val TAG = "RELOpenAIManager"
    private const val API_URL = "https://api.groq.com/openai/v1/chat/completions"
    private const val MODEL = "llama-3.1-8b-instant"

    private val client by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Cache like in Swift
    @Volatile private var motivationMessage: String? = null
    @Volatile private var notificationMessage: String? = null
    @Volatile private var weightMessage: String? = null

    // Duplicate guards (with Mutex to keep it simple & thread-safe)
    private val motivationLock = Mutex()
    private val forecastLock = Mutex()
    private val notificationLock = Mutex()
    private val titleLock = Mutex()
    
    fun isConfigured(): Boolean = BuildConfig.GROQ_KEY.isNotEmpty()

    // -------- Public API (suspend) --------

    suspend fun generateMotivationMessage(stats: MonthlyStats): String? = motivationLock.withLock {
        motivationMessage ?: run {
            val tone = when (MonthPhase.current()) {
                MonthPhase.START -> "It's the beginning of a new month. Inspire the user to set goals and build momentum."
                MonthPhase.MID   -> "We're halfway through the month. Encourage the user to stay consistent and keep progressing."
                MonthPhase.END   -> "It's the end of the month. Celebrate their progress and motivate them to finish strong."
            }

            val prompt = """
                The user has completed ${stats.sessions} workout sessions, walked ${stats.steps} steps, and burned ${stats.calories} calories so far this month.
                $tone
                Write a short, friendly motivational message (under 10 words) to keep them inspired.
            """.trimIndent()

            val sys = "You are a motivational fitness coach. Create short, encouraging messages tailored to the user's current phase of the month."

            sendGroqChatRequest(prompt, sys).also { motivationMessage = it }
        }
    }

    suspend fun generateWeightLossForecast(stats: MonthlyStats): String? = forecastLock.withLock {
        weightMessage ?: run {
            val prompt = """
                The user has burned ${stats.calories} calories this month.
                Based on 7700 calories per 1 kg fat loss, estimate their possible weight loss.
                Write a short, friendly message encouraging them on their progress in under 15 words.
            """.trimIndent()

            val sys = "You are a helpful fitness assistant who explains calorie burn and weight loss in simple and motivating language."

            sendGroqChatRequest(prompt, sys).also { weightMessage = it }
        }
    }

    /**
     * Returns Pair<title, message>
     */
    suspend fun generateWorkoutNotification(stats: MonthlyStats, day: Int): Pair<String?, String?> = notificationLock.withLock {
        val prompt = """
            The user has completed ${stats.sessions} workout sessions, taken ${stats.steps} steps, and burned ${stats.calories} calories this month.

            You are to generate a **short, motivational workout notification** for them. Output both a **title** and a **message**, formatted like this:

            Title: <5-word catchy workout title>  
            Message: <Under 15-word motivational message>

            The tone depends on messageNumber:
            - If 1 → Friendly & motivating
            - If 2 → Slightly concerned
            - If 3 → Urgent, about to miss streak

            Current messageNumber: $day

            Make it fun, inspiring, and actionable. Return only the two lines: Title and Message.
        """.trimIndent()

        val sys = "You are a fitness coach writing short, punchy notifications to motivate users to work out."

        val raw = sendGroqChatRequest(prompt, sys) ?: return Pair(null, null)

        var title: String? = null
        var message: String? = null
        raw.split('\n').forEach { line ->
            val lower = line.lowercase()
            when {
                lower.startsWith("title:") -> title = line.substringAfter(":", "").trim()
                lower.startsWith("message:") -> message = line.substringAfter(":", "").trim()
            }
        }
        Pair(title, message)
    }

    suspend fun generateNextWorkoutNotificationMessage(stats: MonthlyStats, day: Int): String? = notificationLock.withLock {
        notificationMessage ?: run {
            val prompt = """
                The user has completed ${stats.sessions} workout sessions, taken ${stats.steps} steps, and burned ${stats.calories} calories this month.

                You are to generate a **single short motivational notification (under 15 words)** for their next workout.

                The message tone depends on the messageNumber:

                - If messageNumber == 1 → Friendly & motivating: Light encouragement to stay consistent.
                - If messageNumber == 2 → Slightly concerned: Suggest the user might be slacking or slowing down.
                - If messageNumber == 3 → Urgent: Warn that they’re close to breaking their streak or losing momentum.

                Current messageNumber: $day

                Make the message **fun, inspiring, and actionable** based on that tone. Return **only the message text**, with no labels or explanations.
            """.trimIndent()

            val sys = "You are a fitness coach writing short, punchy notifications to motivate users to work out."

            sendGroqChatRequest(prompt, sys).also { notificationMessage = it }
        }
    }

    suspend fun generateTitle(userPrompt: String): String? = titleLock.withLock {
        val sys = "You're a creative assistant generating short, catchy workout titles (max 5 words)."
        sendGroqChatRequest(userPrompt, sys)
    }

    // -------- Networking --------

    private suspend fun sendGroqChatRequest(prompt: String, systemMessage: String): String? = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            Log.w(TAG, "GROQ_KEY is missing. Call isConfigured() before using the manager.")
            return@withContext null
        }

        val json = JSONObject().apply {
            put("model", MODEL)
            put("temperature", 0.8)
            put("messages", org.json.JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", systemMessage))
                put(JSONObject().put("role", "user").put("content", prompt))
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val reqBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(reqBody)
            .build()

        try {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    Log.w(TAG, "HTTP ${resp.code}: ${resp.body?.string()}")
                    return@withContext null
                }
                val body = resp.body?.string() ?: return@withContext null
                val root = JSONObject(body)
                val choices = root.optJSONArray("choices") ?: return@withContext null
                if (choices.length() == 0) return@withContext null
                val message = choices.getJSONObject(0).optJSONObject("message") ?: return@withContext null
                val content = message.optString("content").trim()
                if (content.isEmpty()) null else content
            }
        } catch (t: Throwable) {
            Log.e(TAG, "sendGroqChatRequest error", t)
            null
        }
    }
}
