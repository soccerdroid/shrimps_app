package com.example.belen.shrimps;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JsonWorker {
    JsonWriter writer;


    public JsonWorker(OutputStream out) throws UnsupportedEncodingException {
        this.writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
    }

    public void writeJsonStream(ArrayList<Thumbnail> thumbnails) throws IOException {
        this.writer.setIndent("  ");
        writeThumbnails(thumbnails);
        this.writer.close();
    }

    public void writeThumbnails(ArrayList<Thumbnail> thumbnails) throws IOException {
        writer.beginArray();
        for (Thumbnail thumb : thumbnails) {
            writeThumbnailInfo(thumb);
        }
        writer.endArray();
    }

    public void writeThumbnailInfo(Thumbnail thumbnail) throws IOException {
        writer.beginObject();
        writer.name("id").value(thumbnail.getName());
        if (thumbnail.getTags().isEmpty()) {
            writer.name("tags");
            writeCoordsArray(thumbnail.getTags());
        } else {
            writer.name("tags").nullValue();
        }

        writer.endObject();
    }

    public void writeCoordsArray(ArrayList<ArrayList<Integer>> tags) throws IOException {
        writer.beginArray();
        for (ArrayList<Integer> coords : tags ){
            this.writer.beginArray();
            for (Integer value : coords){
                this.writer.value(value);
            }
            this.writer.endArray();
        }
        writer.endArray();
    }

}
