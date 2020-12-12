package morgan.support;

import morgan.logic.PlayerInfo;
import morgan.structure.serialize.IConstDistrClass;
import morgan.structure.serialize.Serializable;

public class ConstDistrClass implements IConstDistrClass {

    public int getDistrClassId(Serializable s){
        if (s instanceof PlayerInfo){
            return 112;
        }

        return 0;
    }

    public Serializable getDistrClassById(int id){
        if (id == 112){
            return new PlayerInfo();
        }
        return null;
    }
}
