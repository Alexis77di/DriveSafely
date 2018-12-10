package com.example.simulation.util;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class EegTransmitter {
    private final AssetManager assets;
    private String line;
    private String file;
    private BufferedReader br;
    private Random r;

    public EegTransmitter(AssetManager assets) {
        this.assets = assets;
        r = new Random();

        try {
            nextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextLine() {
        try {
            line = br.readLine();
            if (line == null) {
                nextFile();
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextFile() throws IOException {
        file = randomFile();
        br = new BufferedReader(new InputStreamReader(assets.open(file)));
        br.readLine();
    }

    private String randomFile() throws IOException {
        String[] files = assets.list("csv");
        return "csv/" + files[r.nextInt(files.length)];
    }

    public String getFile() {
        return file;
    }

    public String getLine() {
        return line;
    }
}
