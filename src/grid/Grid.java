package grid;

import gameObject.GameObject;
import gameObject.GameObjectConstants;
import gameObject.GameUnit;
import gameObject.action.Action;
import gameObject.action.CombatAction;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import grid.Coordinate;
import view.Drawable;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 * Grid class. Holds all tiles and objects, calculates movement
 * 
 * @author Kevin
 * @author Ken
 * 
 */
@JsonAutoDetect
public class Grid implements Drawable {
    @JsonProperty
    private int myWidth;
    @JsonProperty
    private int myHeight;
    @JsonProperty
    private Tile[][] myTiles;

    @JsonProperty
    private GameObject[][] myObjects;
    @JsonProperty
    private GameUnit[][] myUnits;
    private FromJSONFactory myFactory;

    /**
     * Creates a grid with the width and height set
     * Only for use by deserializer
     */
    public Grid () {
        myFactory = new FromJSONFactory();
    }

    /**
     * Creates a grid with the width and height set, and default tiles of tileID
     * 
     * @param width int of columns of grid
     * @param height int of rows of grid
     * @param tileID int that defines the tile type
     */
    public Grid (int width, int height, int tileID) {
        myWidth = width;
        myHeight = height;
        myTiles = new Tile[width][height];
        myObjects = new GameObject[width][height];
        myUnits = new GameUnit[width][height];
        myFactory = new FromJSONFactory();
        initGrid(tileID);
    }

    /**
     * Sets up default grid with tiles and objects
     */
    private void initGrid (int tileID) {
        initTiles(tileID);
        initObjects();
    }

    /**
     * Creates default tiles for grid
     */
    private void initTiles (int tileID) {
        for (int i = 0; i < myWidth; i++) {
            for (int j = 0; j < myHeight; j++) {
                myTiles[i][j] = (Tile) myFactory.make("Tile", tileID);
            }
        }
    }

    /**
     * Creates default objects and units for grid
     */
    private void initObjects () {
        GameObject tree = (GameObject) myFactory.make("GameObject", 0);
        placeObject(new Coordinate(3, 5), tree);
        GameObject hero = (GameUnit) myFactory.make("GameUnit", 0);
        placeObject(new Coordinate(4, 5), hero);
        beginMove(new Coordinate(4, 5));
    }

    /**
     * Initiates the moving process for a gameUnit
     * 
     * @param coordinate Coordinate where the gameUnit is located
     * 
     */
    public void beginMove (Coordinate coordinate) {
        GameUnit gameUnit = (GameUnit) getObject(coordinate);
        findMovementRange(coordinate,
                          ((GameUnit) gameUnit).getTotalStat(GameObjectConstants.MOVEMENT),
                          gameUnit);
    }

    /**
     * Return boolean of if a gameUnit can move to a given coordinate
     * 
     * @param coordinate Coordinate being moved to
     * @return boolean of if move is possible
     */
    public boolean canMove (Coordinate coordinate) {
        return onGrid(coordinate) && isActive(coordinate) &&
               myObjects[coordinate.getX()][coordinate.getY()] == null;
    }

    /**
     * Moves the unit to a new coordinate if the move is valid
     * 
     * @param oldCoordinate - Coordinate of the gameUnit's original position
     * @param newCoordinate - Coordinate that unit is moving to
     * 
     */
    public void doMove (Coordinate oldCoordinate, Coordinate newCoordinate) {
        if (canMove(newCoordinate)) {
            GameObject gameUnit = removeObject(oldCoordinate);
            placeObject(newCoordinate, gameUnit);
            setTilesInactive(); // TODO: front end, if coordinate clicked is invalid, tiles will
                                // still be active, but will they call doMove again?
        }
    }

    /**
     * Sets the tiles active that the GameObject can move to
     * 
     * @param coordinate Coordinate of the current position of the GameObject
     * @param range int of range that the GameObject can move
     * @param gameObject GameObject that we are finding the range of
     * 
     */
    private void findMovementRange (Coordinate coordinate, int range, GameUnit gameObject) {
        int[] rdelta = { -1, 0, 0, 1 };
        int[] cdelta = { 0, -1, 1, 0 };

        for (int i = 0; i < rdelta.length; i++) {
            int newX = coordinate.getX() + cdelta[i];
            int newY = coordinate.getY() + rdelta[i];
            if (onGrid(coordinate)) {
                Tile currentTile = getTile(new Coordinate(newX, newY));
                int newRange = range - currentTile.getMoveCost();

                if (currentTile.isPassable(gameObject) && newRange >= 0) {
                    GameObject currentObject = getObject(new Coordinate(newX, newY));
                    if (currentObject != null) {
                        if (currentObject.isPassable(gameObject)) {
                            findMovementRange(new Coordinate(newX, newY), newRange, gameObject);
                        }
                        continue;
                    }
                    else {
                        currentTile.setActive(true);
                        findMovementRange(new Coordinate(newX, newY), newRange, gameObject);
                    }
                }
            }
        }
    }

    /**
     * Checks if the input coordinate is on the grid
     * 
     * @param coordinate Coordinate being checked
     * @return boolean of if the coordinate is valid
     */
    private boolean onGrid (Coordinate coordinate) {
        return (0 <= coordinate.getX() && coordinate.getX() < myWidth && 0 <= coordinate.getY() && coordinate
                .getY() < myHeight);
    }

    /**
     * Checks if a coordinate is a valid move or action (the tile is active)
     * 
     * @param coordinate Coordinate being checked
     * @return boolean of if the coordinate is active
     */
    public boolean isActive (Coordinate coordinate) {
        return getTile(coordinate).isActive();
    }

    // TODO: fix so that beginAction can be called with objectCoordinate it's originating from, and
    // action being used
    /**
     * Initiates the action process
     * 
     * @param objectCoordinate Coordinate where the action originates
     * @param gameUnit GameUnit that is doing the action
     * @param combatAction CombatAction that is being used
     */
    public void beginAction (Coordinate coordinate, Action action) {
        findActionRange(coordinate, action);
    }

    // TODO: fix so that doAction can be called with two coordinates and action being used. make
    // canAction if necessary to check
    /**
     * Returns the game objects affected by the action
     * 
     * @param objectCoordinate Coordinate where the action originates
     * @param gameUnit GameUnit that is doing the action
     * @param combatAction CombatAction that is being used
     * @param actionCoordinate Coordinate that the user selects for the action
     * @return List of GameObjects that are affected
     */
    public List<GameObject> doAction (Coordinate objectCoordinate, GameUnit gameUnit,
                                      CombatAction combatAction,
                                      Coordinate actionCoordinate) {
        // String direction = findDirection(objectCoordinate, combatAction, actionCoordinate);
        // return findAffectedObjects(objectCoordinate, combatAction, direction);
        return null;
    }

    // TODO: fix so that it just sets adjacent tiles to active. or units to trade with, or chests to
    // open
    /**
     * Sets the tiles active that an action can affect
     * 
     * @param coordinate Coordinate where the action originates
     * @param area List of Coordinates that map the area of the action
     * @param isAround boolean of whether the action only affects one direction, or is all around
     *        the unit
     */
    private void findActionRange (Coordinate coordinate, Action action) {
    }

    /**
     * Creates a list of information that a coordinate contains, including tiles and objects
     * 
     * @param coordinate Coordinate that is being asked for
     * @return List of Strings that contain information about the coordinate
     */
    public List<String> generateInfoList (Coordinate coordinate) {
        List<String> data = new ArrayList<>();
        Tile tile = myTiles[coordinate.getX()][coordinate.getY()];
        data.add("Tile");
        data.addAll(tile.getInfo());
        GameObject gameObject = myObjects[coordinate.getX()][coordinate.getY()];
        if (gameObject != null) {
            data.add("");
            data.add(gameObject.getName());
            data.addAll(gameObject.getInfo());
        }
        return data;
    }

    /**
     * Generates a list of names of actions that a unit at the given coordinate can perform
     * 
     * @param coordinate Coordinate of the unit's location
     * @return List of Strings of the action names. Null if there is no unit at coordinate
     */
    public List<String> generateActionList (Coordinate coordinate) {
        if (getUnit(coordinate) != null) {
            List<String> actionList = new ArrayList<>();
            for (Action action : generateActions(coordinate)) {
                actionList.add(action.getName());
            }
            return actionList;
        }
        return null;
    }

    /**
     * Generates a list of valid actions that a unit at the given coordinate can perform
     * 
     * @param coordinate Coordinate of the unit's location
     * @return List of Actions
     */
    private List<Action> generateActions (Coordinate coordinate) {
        List<Action> actions = new ArrayList<>();
        GameUnit gameUnit = myUnits[coordinate.getX()][coordinate.getY()];
        // TODO: actions.addAll(gameUnit.getValidInteractions(coordinate)) needs to be fixed in
        // gameUnit/action
        actions.addAll(getInteractions(coordinate));
        return actions;
    }

    /**
     * Returns the action that matches the action name provided
     * 
     * @param actionName String of name of action being searched for
     * 
     * @return Action of the action being searched for, and null if no action found
     */
    private Action selectAction (String actionName, Coordinate coordinate) {
        for (Action action : generateActions(coordinate)) {
            if (action.getName().equals(actionName)) { return action; }
        }

        return null;
    }

    // TODO: when getting interactions, trade should only be valid between matching affiliations
    /**
     * Gets a list of valid actions that the unit can perform on the objects around him
     * 
     * @param gameUnit
     * @return
     */
    private List<Action> getInteractions (Coordinate coordinate) {
        List<Action> interactions = new ArrayList<>();
        int[] rdelta = { -1, 0, 0, 1 };
        int[] cdelta = { 0, -1, 1, 0 };

        for (int i = 0; i < rdelta.length; i++) {
            Action interaction =
                    getInteraction(new Coordinate(coordinate.getX() + cdelta[i], coordinate.getY() +
                                                                                 rdelta[i]));
            if (interaction != null) {
                interactions.add(interaction);
            }
        }
        return interactions;
    }

    /**
     * Gets an interaction if one exists at the given coordinate
     * 
     * @param coordinate Coordinate of the location being searched
     * @return Action that can be performed
     */
    private Action getInteraction (Coordinate coordinate) {
        if (onGrid(coordinate)) {
            if (getObject(coordinate) != null) { return myObjects[coordinate.getX()][coordinate
                    .getY()].getInteraction(); }
        }
        return null;
    }

    /**
     * Returns an object at the given coordinates
     * 
     * @param coordinate Coordinate being checked
     * @return GameObject at coordinate
     */
    public GameObject getObject (Coordinate coordinate) {
        // TODO: Generic method?
        return myObjects[coordinate.getX()][coordinate.getY()];
    }

    /**
     * Returns a unit at the given coordinates
     * 
     * @param coordinate Coordinate being checked
     * @return GameUnit at the coordinate
     */
    public GameUnit getUnit (Coordinate coordinate) {
        // TODO: Generic method?
        return myUnits[coordinate.getX()][coordinate.getY()];
    }

    /**
     * Returns the coordinates of a unit's location
     * 
     * @param gameUnit GameUnit that is being located
     * @return Coordinate of unit's location
     */
    public Coordinate getUnitCoordinate (GameUnit gameUnit) {
        for (int i = 0; i < myUnits.length; i++) {
            for (int j = 0; j < myUnits[0].length; j++) {
                if (myUnits[i][j].equals(gameUnit)) return new Coordinate(i, j);
            }
        }
        return null;
    }

    /**
     * Places a GameObject at given coordinates
     * 
     * @param coordinate Coordinate being checked
     * @param gameObject GameObject to be placed
     * 
     */
    public void placeObject (Coordinate coordinate, GameObject gameObject) {
        // TODO: Generic method?
        myObjects[coordinate.getX()][coordinate.getY()] = gameObject;

        if (gameObject instanceof GameUnit) {
            myUnits[coordinate.getX()][coordinate.getY()] = (GameUnit) gameObject;
        }
    }

    /**
     * Sets position in myObjects map to null
     * 
     * @param coordinate Coordinate being checked
     * @return Object removed from position (x,y)
     */
    private GameObject removeObject (Coordinate coordinate) {
        GameObject objToRemove = getObject(coordinate);
        myObjects[coordinate.getX()][coordinate.getY()] = null;
        return objToRemove;
    }

    public GameUnit[][] getGameUnits () {
        return myUnits;
    }
    
    /**
     * Finds all coordinates adjacent to the coordinate
     * given.
     * 
     * @param - Coordinate from which to find adjacent coords
     * @return
     */
    public List<Coordinate> adjacentCoordinates(Coordinate coord) {
        List<Coordinate> returnArray = new ArrayList<Coordinate>();
        returnArray.add(new Coordinate(coord.getX()+1, coord.getY()));
        returnArray.add(new Coordinate(coord.getX(), coord.getY()+1));
        returnArray.add(new Coordinate(coord.getX()-1, coord.getY()));
        returnArray.add(new Coordinate(coord.getX(), coord.getY()-1));
        return returnArray;

    }
    
    /**
     * Returns an tile at the given coordinates
     * 
     * @param coordinate Coordinate being checked
     * @return Tile at coordinate
     */
    public Tile getTile (Coordinate coordinate) {
        // TODO: Generic method?
        return myTiles[coordinate.getX()][coordinate.getY()];
    }

    /**
     * Places a Tile at given coordinates
     * 
     * @param coordinate Coordinate being checked
     * @param tile Tile to be placed
     */
    public void placeTile (Coordinate coordinate, Tile tile) {
        // TODO: Generic method?
        myTiles[coordinate.getX()][coordinate.getY()] = tile;
    }

    /**
     * Sets all tiles on grid to be inactive
     */
    private void setTilesInactive () {
        for (int i = 0; i < myTiles.length; i++) {
            for (int j = 0; j < myTiles[i].length; j++) {
                myTiles[i][j].setActive(false);
            }
        }
    }

    /**
     * Draws the tiles and objects on the grid
     * 
     * @param g - Graphics for the image
     * @param x - int of x coordinate on the grid
     * @param y - int of y coordinate on the grid
     * @param width - int of width of object
     * @param height - int of height of object
     */
    public void draw (Graphics g, int x, int y, int width, int height) {
        int tileWidth = width / myWidth;
        int tileHeight = height / myHeight;

        for (int i = 0; i < myTiles.length; i++) {
            for (int j = 0; j < myTiles[i].length; j++) {
                Tile tile = myTiles[i][j];
                tile.draw(g, i * tileWidth, j * tileHeight, tileWidth, tileHeight);
            }
        }

        // TODO: dupe for tile and object. generic
        for (int i = 0; i < myObjects.length; i++) {
            for (int j = 0; j < myObjects[i].length; j++) {
                GameObject gameObject = myObjects[i][j];
                if (gameObject != null) {
                    gameObject.draw(g, i * tileWidth, j * tileHeight, tileWidth, tileHeight);
                }
            }
        }
    }

    public Tile[][] getTiles () {
        return myTiles;
    }

    public void setTiles (Tile[][] tiles) {
        myTiles = tiles;
    }

    public Coordinate getCoordinate (double fracX, double fracY) {

        int gridX = (int) (fracX * myWidth);
        int gridY = (int) (fracY * myHeight);

        return new Coordinate(gridX, gridY);
    }

}
