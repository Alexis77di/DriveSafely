package com.example.simulation.util;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class EegTransmitter {
    private final AssetManager assets;
    private Random r;

    String content = "";


    public EegTransmitter(AssetManager assets) {
        this.assets = assets;
        r = new Random();

        nextFile();
    }

    public void nextFile() {
        try {
            String file = randomFile();

            BufferedReader br = new BufferedReader(new InputStreamReader(assets.open(file)));
            String line = br.readLine();
            content = "";
            while (line != null) {
                content += line;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String randomFile() throws IOException {
        String[] files = assets.list("csv");
        return "csv/" + files[r.nextInt(files.length)];
    }

    public String getContent() {
        return content;
    }

}
