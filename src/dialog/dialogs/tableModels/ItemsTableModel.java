package dialog.dialogs.tableModels;

import java.util.HashMap;
import java.util.Map;
import controllers.EditorData;


@SuppressWarnings("serial")
public class ItemsTableModel extends GameTableModel {

    /**
     * Column names: Key, Value
     */
    public ItemsTableModel (EditorData ed) {
        String[] names = { "Item", "Amount" };
        setColumnNames(names);
        myName = "Item";
        myED = ed;
    }

    @Override
    public boolean isCellEditable (int row, int col) {
        return col > 0;
    }

    @SuppressWarnings("rawtypes")
    public void loadObject (Object object) {
        Map map = (Map) object;
        myList.clear();
        for (Object key : map.keySet()) {
            Object[] row = new Object[myColumnNames.length];
            row[0] = key;
            row[1] = map.get(key);
            addNewRow(row);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getObject () {
        Map myMap = new HashMap<String, Integer>();
        for (Object[] row : myList) {
            myMap.put(row[0], row[1]);
        }

        return myMap;
    }

    @Override
    public Object[] getNew () {
        Object[] array = new Object[myColumnNames.length];
        array[0] = "key";
        array[1] = 0;
        return array;
    }

    @Override
    public String getRowType () {
        return "Map Object";
    }
}
