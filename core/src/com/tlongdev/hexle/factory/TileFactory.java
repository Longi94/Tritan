package com.tlongdev.hexle.factory;

import com.tlongdev.hexle.model.BlankTile;
import com.tlongdev.hexle.model.Tile;
import com.tlongdev.hexle.model.TileColor;

/**
 * @author longi
 * @since 2016.04.14.
 */
public class TileFactory {
    public Tile get(int x, int y) {
        Tile tile = new Tile();
        tile.setPosX(x);
        tile.setPosY(y);

        tile.setTileColor(TileColor.RED);
        tile.updateIndices();
        return tile;
    }

    public Tile getBlank(int x, int y) {
        Tile tile = new BlankTile();
        tile.setPosX(x);
        tile.setPosY(y);

        tile.updateIndices();
        return tile;
    }
}
