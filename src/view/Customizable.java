package view;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect
public abstract class Customizable {
    @JsonProperty
    protected Map<String, String> myData;
    protected List<String> neededData;
    protected String myName;
    protected String myImagePath;

    public Customizable () {
        myData = new HashMap<String, String>();
        neededData = new ArrayList<String>();
        myImagePath = "resources/grass.png";
        myName = "";
    }

    public String getName () {
        return myName;
    }

    public void setName (String name) {
        myName = name;
    }

    // might never be used.
    public void addData (String key, String data) {
        myData.put(key, data);
    }

    public void setData (Map<String, String> data) {
        myData = data;
    }

    public List<String> getNeededData () {
        return neededData;
    }

    public Image getImage () {
        try {
            return ImageIO.read(new File(myImagePath));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getImagePath () {
        return myImagePath;
    }

    public void setImagePath (String imagePath) {
        myImagePath = imagePath;
    }
}
