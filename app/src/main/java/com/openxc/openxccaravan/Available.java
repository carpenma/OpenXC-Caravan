package com.openxc.openxccaravan;

/**
 * Created by MCARPE53 on 10/24/2016.
 */

public class Available {
    public String pretty_name;
    public int count;
    public int max;
    public boolean locked;

    public Available(String pretty, int count, int max, boolean locked) {
        this.pretty_name = pretty;
        this.count = count;
        this.max = max;
        this.locked = locked;
    }

}
