package usage.ywb.wrapper.audio.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

/**
 * @author frank.yu
 *
 * DATE:2015.05.28
 */
public class LyricProvider {

    public static final String LRC_PATH = Environment.getExternalStorageDirectory() + "/Musiclrc";
    private final List<String> texts = new ArrayList<String>();
    private final List<Integer> times = new ArrayList<Integer>();
    private String name;

    /**
     * @param name
     *            the name to set
     */
    public void initLyricFile(final String name) {
        this.name = name;
        openFile();
    }

    /**
     * @return the texts
     */
    public List<String> getTexts() {
        return texts;
    }

    /**
     * @return the times
     */
    public List<Integer> getTimes() {
        return times;
    }

    /**
     * 打开歌词文件
     */
    private void openFile() {
        times.clear();
        texts.clear();
        final String path = LRC_PATH + "/" + name;
        final File file = new File(path);
        String sentence = "";
        try {
            final FileInputStream stream = new FileInputStream(file);
            final InputStreamReader reader = new InputStreamReader(stream, "utf-8");
            final BufferedReader buffered = new BufferedReader(reader);
            while ((sentence = buffered.readLine()) != null) {
                getLyric(sentence);
            }
            buffered.close();
            reader.close();
            stream.close();
        } catch (final FileNotFoundException e) {
            Log.i("ywb", "文件没找到！");
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            times.clear();
            texts.clear();
            times.add(0);
            texts.add("");
            e.printStackTrace();
        }

    }

    /**
     * 获取歌词
     *
     * @param string
     */
    private void getLyrics(final String string) throws Exception {
        Log.i("ywb", "String: " + string);
        final int position1 = string.indexOf("[");
        final int position2 = string.indexOf("]");

        if (position1 == 0 && position2 != -1) {
            if (string.indexOf("[al") != -1 || string.indexOf("[ar") != -1 || string.indexOf("[ti") != -1
                    || string.indexOf("[by") != -1) {
            } else {
                final String time = string.substring(position1 + 1, position2);
                final String text = string.substring(position2 + 1);
                final int min = Integer.valueOf(time.substring(0, time.indexOf(":")));
                final int sec = Integer.valueOf(time.substring(time.indexOf(":") + 1, time.indexOf(".")));
                final int ms = Integer.valueOf(time.substring(time.indexOf(".") + 1));
                final int stime = min * 60 * 1000 + sec * 1000 + ms;
                texts.add(text);
                times.add(stime);
            }
        }
    }

    /**
     * 获取歌词
     *
     * @param string
     */
    private void getLyric(final String string) throws Exception {
        Log.i("ywb", "String: " + string);
        final int position1 = string.indexOf("[");
        final int position2 = string.indexOf("]");
        if (position1 == 0 && position2 != -1) {
            if (string.indexOf("[al") != -1 || string.indexOf("[ar") != -1 || string.indexOf("[ti") != -1
                    || string.indexOf("[by") != -1) {
            } else {
                final String time = string.substring(position1 + 1, position2);
                final String text = string.substring(position2 + 1);
                final int min = Integer.valueOf(time.substring(0, time.indexOf(":")));
                final int sec = Integer.valueOf(time.substring(time.indexOf(":") + 1, time.indexOf(".")));
                final int ms = Integer.valueOf(time.substring(time.indexOf(".") + 1));
                final int stime = min * 60 * 1000 + sec * 1000 + ms;
                texts.add(text);
                times.add(stime);
            }
        }
    }

}
