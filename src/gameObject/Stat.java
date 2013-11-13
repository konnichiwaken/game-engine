package gameObject;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class Stat {
    @JsonProperty
    private Map<String, Integer> myStatList = new HashMap<String, Integer>();

    public Stat () {
    }

    public Integer getStatValue (String statName) {
        return myStatList.get(statName);
    }

    public void modExisting (String statName, Integer value) {
        if (myStatList.containsKey(statName)) {
            myStatList.put(statName, value);
        }
    }

    public void setStatValue (String name, Integer value) {
        myStatList.put(name, value);
    }
}
