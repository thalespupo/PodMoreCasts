package com.tapura.podmorecasts.details


import android.os.AsyncTask

import com.tapura.podmorecasts.MyApplication
import com.tapura.podmorecasts.Utils
import com.tapura.podmorecasts.model.Podcast
import com.tapura.podmorecasts.parser.FeedParser

import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.io.InputStream

class DownloadAndParseFeedTask(private val block: (Podcast?) -> Unit) : AsyncTask<String, Int, Podcast>() {

    override fun doInBackground(vararg strings: String): Podcast? {
        val feed = strings[0]
        var inputStream: InputStream? = null

        try {
            inputStream = Utils.downloadXml(feed, MyApplication.getApp())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var podcast: Podcast? = null

        try {
            podcast = FeedParser().parse(inputStream, feed)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return podcast
    }

    override fun onPostExecute(podcast: Podcast?) = block(podcast)
}
