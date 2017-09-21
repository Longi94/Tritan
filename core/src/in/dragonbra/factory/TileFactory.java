package in.dragonbra.factory;

import in.dragonbra.model.BlankTile;
import in.dragonbra.model.Tile;
import in.dragonbra.model.enumeration.TileColor;

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
