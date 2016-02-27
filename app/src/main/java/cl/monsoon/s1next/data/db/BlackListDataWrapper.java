package cl.monsoon.s1next.data.db;

import java.util.List;

import cl.monsoon.s1next.data.db.dbmodel.BlackList;

/**
 * Created by AdminYkrank on 2016/2/25.
 */
public class BlackListDataWrapper {
    
    private List<BlackList> blackLists;
    
    
    public List<BlackList> getBlackLists() {
        return blackLists;
    }

    public void setBlackLists(List<BlackList> blackLists) {
        this.blackLists = blackLists;
    }
}
