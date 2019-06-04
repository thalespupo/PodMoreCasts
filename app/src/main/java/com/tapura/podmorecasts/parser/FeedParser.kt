package com.tapura.podmorecasts.parser


import android.util.Xml
import com.tapura.podmorecasts.model.Episode
import com.tapura.podmorecasts.model.Podcast
import com.tapura.podmorecasts.model.STATE_NOT_IN_DISK
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class FeedParser {

    companion object {
        private val ns: String? = null
        private const val RSS_TAG = "rss"
        private const val CHANNEL_TAG = "channel"
        private const val TITLE_TAG = "title"
        private const val SUMMARY_TAG = "itunes:summary"
        private const val AUTHOR_TAG = "itunes:author"
        private const val IMAGE_TAG = "itunes:image"
        private const val EPISODE_TAG = "item"
        private const val LINK_TAG = "link"
        private const val EPISODE_LINK_TAG = "enclosure"
        private const val GUID_TAG = "guid"
        private const val DESCRIPTION_TAG = "description"
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream?, feed: String): Podcast? {
        inputStream?.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser, feed)
        } ?: throw IOException("InputStream null, some error occurred inputStream download")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser, feed: String): Podcast? {
        var podcast: Podcast? = null

        parser.require(XmlPullParser.START_TAG, ns, RSS_TAG)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == CHANNEL_TAG) {
                podcast = readPodcast(parser, feed)
            } else {
                skip(parser)
            }
        }
        return podcast
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPodcast(parser: XmlPullParser, feed: String): Podcast? {
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL_TAG)
        var title: String? = null
        var summary: String? = null
        var author: String? = null
        var imagePath: String? = null
        val episodes = ArrayList<Episode>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue

            when (parser.name) {
                TITLE_TAG -> title = readSimple(parser, TITLE_TAG)
                SUMMARY_TAG -> summary = readSimple(parser, SUMMARY_TAG)
                AUTHOR_TAG -> author = readSimple(parser, AUTHOR_TAG)
                IMAGE_TAG -> imagePath = readImage(parser)
                EPISODE_TAG -> episodes.add(readEpisode(parser))

                else -> skip(parser)
            }
        }

        return Podcast().apply {
            this.title = title ?: this.title
            this.author = author ?: this.author
            this.summary = summary ?: this.summary
            this.imagePath = imagePath ?: this.imagePath
            this.episodes = episodes
            feedUrl = feed
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readEpisode(parser: XmlPullParser): Episode {
        parser.require(XmlPullParser.START_TAG, ns, EPISODE_TAG)
        var title: String? = null
        var link: String? = null
        var episodeLink: String? = null
        var guid: String? = null
        var description: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                TITLE_TAG -> title = readSimple(parser, TITLE_TAG)
                LINK_TAG -> link = readSimple(parser, LINK_TAG)
                EPISODE_LINK_TAG -> episodeLink = readEpisodeLink(parser)
                GUID_TAG -> guid = readSimple(parser, GUID_TAG)
                DESCRIPTION_TAG -> description = readSimple(parser, DESCRIPTION_TAG)
                else -> skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, EPISODE_TAG)

        return Episode().apply {
            this.title = title ?: this.title
            this.link = link ?: this.link
            this.episodeLink = episodeLink ?: this.episodeLink
            this.description = description ?: this.description
            this.guid = guid ?: this.guid
            episodeState = STATE_NOT_IN_DISK
        }
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readEpisodeLink(parser: XmlPullParser): String {
        var link = ""
        parser.require(XmlPullParser.START_TAG, ns, EPISODE_LINK_TAG)
        val tag = parser.name

        if (tag == EPISODE_LINK_TAG) {
            link = parser.getAttributeValue(null, "url")
            parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, ns, EPISODE_LINK_TAG)
        return link
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readImage(parser: XmlPullParser): String {
        var imagePath = ""
        parser.require(XmlPullParser.START_TAG, ns, IMAGE_TAG)
        val tag = parser.name
        if (tag == IMAGE_TAG) {
            imagePath = parser.getAttributeValue(null, "href")
            parser.nextTag()
        }
        parser.require(XmlPullParser.END_TAG, ns, IMAGE_TAG)
        return imagePath
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readSimple(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val text = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return text
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    // Improve - In future, I need to parse the feed while downloading it, to show to the user the episodes in RecyclerView while downloading all the feed stuff
    // Link to help https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
}
