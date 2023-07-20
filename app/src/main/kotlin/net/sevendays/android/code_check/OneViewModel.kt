/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package net.sevendays.android.code_check

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.DelicateCoroutinesApi
import net.sevendays.android.code_check.R
import net.sevendays.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.lang.Exception
import java.util.*

/**
 * Use with TwoFragment
 */
class OneViewModel : ViewModel() {
    // Search results
    fun searchResults(inputText: String): List<item> = runBlocking {
        val client = HttpClient(Android)
        var x:List<item>
        try {
            x = GlobalScope.async {
                val response: HttpResponse =
                    client.get("https://api.github.com/search/repositories") {
                        header("Accept", "application/vnd.github.v3+json")
                        parameter("q", inputText)
                    }

                val jsonBody = JSONObject(response.receive<String>())
//                print("Json body is" + jsonBody)

                val jsonItems = jsonBody.optJSONArray("items")!!

                val items = mutableListOf<item>()

                /**
                 * Loop for the number of items
                 */
                for (i in 0 until jsonItems.length()) {
                    val jsonItem = jsonItems.optJSONObject(i)!!
                    val name = jsonItem.optString("full_name")
                    val ownerIconUrl = jsonItem.optJSONObject("owner")!!.optString("avatar_url")
                    val language = jsonItem.optString("language")
                    val stargazersCount = jsonItem.optLong("stargazers_count")
                    val watchersCount = jsonItem.optLong("watchers_count")
                    val forksCount = jsonItem.optLong("forks_conut")
                    val openIssuesCount = jsonItem.optLong("open_issues_count")

                    items.add(
                        item(
                            name = name,
                            ownerIconUrl = ownerIconUrl,
//                            language = context.getString(R.string.written_language, language),
                            language = "Written in $language",
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount
                        )
                    )
                }

                lastSearchDate = Date()

                return@async items.toList()
            }.await()
        }
        catch (e:Exception){
            print("There was an exception: " + e)
            x = listOf()
        }


        return@runBlocking x
    }
}

@Parcelize
data class item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable