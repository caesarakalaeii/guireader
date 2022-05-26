package com.guireadergui;

public class ExecutionerFactory {
    public static Executioner newInstance(ExecEnum type, Executioner ref){
        switch (type){
            case SOUND: return new SoundExecutioner(ref);
        }
        return new BasicExecutioner();

    }
}
