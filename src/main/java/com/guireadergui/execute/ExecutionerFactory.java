package com.guireadergui.execute;

public class ExecutionerFactory {

    private ExecutionerFactory() {

    }
    public static Executioner newInstance(ExecEnum type, int patience, Executioner ref){

        switch (type){

            case SOUND: return new SoundExecutioner(patience, ref);
            default: throw new UnsupportedOperationException("Executioner-Type not implemented");
        }


    }
}
