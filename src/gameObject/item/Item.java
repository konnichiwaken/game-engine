package gameObject.item;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import view.Customizable;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gameObject.Stats;
import gameObject.action.Action;
import grid.ImageManager;
import view.Drawable;


/**
 * Items have a name and an quantity (amount), items can have a wide range of effects including
 * effecting stats, which is evident in the statEffect abstract method. Alternatively they can have
 * an effect on the properties of a gameunit, such as reviving a units health.
 * 
 * @author carlosreyes
 * 
 */
@JsonAutoDetect
public class Item extends Customizable implements Drawable {
    @JsonProperty
    private List<Action> myActions;
    @JsonProperty
    private Stats myStats;

    public Item () {
        myActions = new ArrayList<>();
        myStats = new Stats();
    }

    public List<Action> getActions () {
        return myActions;
    }

    public List<String> getActionNames () {
        List<String> actionNames = new ArrayList<>();

        for (Action action : myActions) {
            actionNames.add(action.getName());
        }

        return actionNames;
    }

    public void addAction (Action action) {
        myActions.add(action);
    }

    public void addAction (int index, Action action) {
        myActions.set(index, action);
    }

    public void removeAction (int index) {
        myActions.remove(index);
    }

    public void setActions (List<Action> actions) {
        myActions = actions;
    }

    @JsonIgnore
    public int getStat (String statName) {
        if (myStats.getStats().containsKey(statName))
            return myStats.getStatValue(statName);
        return 0;
    }

    public Stats getStats () {
        return myStats;
    }

    public void setStats (Stats myStats) {
        this.myStats = new Stats(myStats);
    }

    @JsonProperty("imagePath")
    public void setImageAndPath (String imagePath) {

        myImagePath = imagePath;
        try {
            myImage = ImageManager.addImage(imagePath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncActionsWithMaster (List<Action> masterActionList) {
        int masterIndex = -1;
        for (int i = 0; i < myActions.size(); i++) {
            for (int j = 0; j < masterActionList.size(); j++) {
                if (masterActionList.get(j).getName().equals(myActions.get(i).getName())) {
                    masterIndex = j;
                    break;
                }
            }

            if (masterIndex == -1) {
                myActions.remove(i);
                i--;
            }
            else {
                myActions.set(i, masterActionList.get(masterIndex));
            }
        }
    }

    @Override
    public void draw (Graphics g, int x, int y, int width, int height) {
        g.drawImage(getImage(), x, y, width, height, null);
    }
}
