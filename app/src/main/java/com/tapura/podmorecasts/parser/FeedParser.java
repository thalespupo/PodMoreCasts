package com.tapura.podmorecasts.parser;


import android.util.Xml;

import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.Podcast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FeedParser {

    private static final String ns = null;
    private static final String RSS_TAG = "rss";
    private static final String CHANNEL_TAG = "channel";
    private static final String TITLE_TAG = "title";
    private static final String SUMMARY_TAG = "itunes:summary";
    private static final String AUTHOR_TAG = "itunes:author";
    private static final String IMAGE_TAG = "itunes:image";
    private static final String EPISODE_TAG = "item";
    private static final String LINK_TAG = "link";
    private static final String EPISODE_LINK_TAG = "enclosure";
    private static final String DESCRIPTION_TAG = "description";

    public Podcast parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Podcast readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Podcast podcast = null;

        parser.require(XmlPullParser.START_TAG, ns, RSS_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(CHANNEL_TAG)) {
                podcast = readPodcast(parser);
            } else {
                skip(parser);
            }
        }
        return podcast;
    }

    private Podcast readPodcast(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL_TAG);
        String title = null;
        String summary = null;
        String author = null;
        String imagePath = null;
        List<Episode> episodes = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case TITLE_TAG:
                    title = readSimple(parser, TITLE_TAG);
                    break;
                case SUMMARY_TAG:
                    summary = readSimple(parser, SUMMARY_TAG);
                    break;
                case AUTHOR_TAG:
                    author = readSimple(parser, AUTHOR_TAG);
                    break;
                case IMAGE_TAG:
                    imagePath = readImage(parser);
                    break;
                case EPISODE_TAG:
                    episodes.add(readEpisode(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        Podcast podcast = new Podcast();
        podcast.setTitle(title);
        podcast.setSummary(summary);
        podcast.setAuthor(author);
        podcast.setImagePath(imagePath);
        podcast.setEpisodes(episodes);

        return podcast;
    }

    private Episode readEpisode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, EPISODE_TAG);
        String title = null;
        String link = null;
        String episodeLink = null;
        String description = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case TITLE_TAG:
                    title = readSimple(parser, TITLE_TAG);
                    break;
                case LINK_TAG:
                    link = readSimple(parser, LINK_TAG);
                    break;
                case EPISODE_LINK_TAG:
                    episodeLink = readEpisodeLink(parser);
                    break;
                case DESCRIPTION_TAG:
                    description = readSimple(parser, DESCRIPTION_TAG);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, EPISODE_TAG);

        Episode episode = new Episode();
        episode.setTitle(title);
        episode.setLink(link);
        episode.setEpisodeLink(episodeLink);
        episode.setDescription(description);

        return episode;
    }


    private String readEpisodeLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, EPISODE_LINK_TAG);
        String tag = parser.getName();

        if (tag.equals(EPISODE_LINK_TAG)) {
            link = parser.getAttributeValue(null, "url");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, EPISODE_LINK_TAG);
        return link;
    }

    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String imagePath = "";
        parser.require(XmlPullParser.START_TAG, ns, IMAGE_TAG);
        String tag = parser.getName();
        if (tag.equals(IMAGE_TAG)) {
            imagePath = parser.getAttributeValue(null, "href");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, IMAGE_TAG);
        return imagePath;
    }

    private String readSimple(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
