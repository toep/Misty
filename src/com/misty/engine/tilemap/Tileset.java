package com.misty.engine.tilemap;

import com.misty.engine.graphics.Bitmap;
import com.misty.utils.Util;

import java.io.IOException;

public class Tileset {

    Bitmap[] bms;
    private String src;
    public int width, height;

    public Tileset(String source, int tileWidth, int tileHeight) throws IOException {
        src = source;
        width = tileWidth;
        height = tileHeight;
        bms = new Bitmap[width * height];
        bms = Util.bitmapsFromSheet(new Bitmap("res/" + src), width, height);
        //TODO change all pixels in @bms that are our transparent value to 0x00000000 so our engine can render them as transparent.
    }

    public Tileset() {
        // TODO Auto-generated constructor stub
    }

    public static Tileset concat(Tileset[] tss) {
        Tileset ts = new Tileset();
        int totalTiles = 0;
        for (Tileset ts1 : tss) {
            totalTiles += ts1.bms.length;
        }
        ts.bms = new Bitmap[totalTiles];
        int bitmapIndex = 0;
        for (Tileset ts1 : tss) {
            for (int b = 0; b < ts1.bms.length; b++)
                ts.bms[bitmapIndex++] = ts1.bms[b];
        }
        ts.width = tss[0].width;
        ts.height = tss[0].height;

        return ts;
    }


}
