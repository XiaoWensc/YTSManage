package chanson.androidflux;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chanson on 17/3/9.
 */
public class Dispatcher {

    private static LinkedHashMap<Store,Action> stores = new LinkedHashMap<>();
    private static LinkedList<Action> actions = new LinkedList<>();

    public static void dispatch(Action action,Store store){
        if(store == null) return;
        store.doAction(action);
    }

}
