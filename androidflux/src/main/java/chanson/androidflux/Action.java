package chanson.androidflux;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by Chanson on 17/3/9.
 */
public abstract class Action extends HashMap<String,Object>{

    public int type;

    public Action(int type){
        this.type = type;
    }
}
