package com.tapura.podmorecasts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.tapura.podmorecasts.model.Podcast;
import com.tapura.podmorecasts.parser.FeedParser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tapura.podmorecasts", appContext.getPackageName());
    }

    @Test
    public void parsePodcast() {
        InputStream inputStream = InstrumentationRegistry.getTargetContext().getResources().openRawResource(R.raw.fakedata);

        FeedParser parser = new FeedParser();
        Podcast podcast = null;
        try {
            podcast = parser.parse(inputStream);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(podcast);
    }
}
