package com.example.simulation.util;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class EegTransmitter {
    private final AssetManager assets;
    private Random r;

    private String content = "";


    public EegTransmitter(AssetManager assets) {
        this.assets = assets;
        r = new Random();


    }

    public void nextFile() {
        try {
            String file = randomFile();

            BufferedReader br = new BufferedReader(new InputStreamReader(assets.open(file)));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            sb.append("/").append(file);
            content = sb.toString();
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
