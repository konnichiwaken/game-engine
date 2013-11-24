package dialog;

import grid.Tile;
import java.util.ArrayList;
import java.util.List;
import view.Customizable;

/**
 * 
 * @author brooksmershon
 *
 */


public class TileTableModel extends GameTableModel{

    public TileTableModel(){
        String[] names = {"Move Cost", "Image path", "Passable List"};
        setColumnNames(names);
    }
    
    public void addPreviouslyDefined(List<Customizable> tiles){
        
        for(Object tile: tiles){
            Object[] array = new Object[myColumnNames.length];
            
            Tile t = (Tile) tile;
            
            array[0] = t.getImagePath();
            array[1] = t.getMoveCost();
            array[2] = t.getStatMods();
            array[3] = t.getPassableList();
            
            

            myList.add(array);        
        }
    }
    
    public Object getObject(int ID){
        if(ID < myColumnNames.length){
            Object[] current = myList.get(ID);
            Tile t = new Tile();
            t.setMoveCost((int) current[0]);
            t.setImageAndPath((String) current[1]);
           // t.setPassableList((List<String>) current[2]);
            
            return t;
        }
        
        return null;
    }
}