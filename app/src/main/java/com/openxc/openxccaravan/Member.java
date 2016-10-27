package com.openxc.openxccaravan;

/**
 * Created by MCARPE53 on 10/27/2016.
 */

public class Member {
    public String pretty_name;
    public String make;
    public String model;
    public int year;

    public Member(String pretty_name, String make, String model, int year) {
        this.pretty_name = pretty_name;
        this.make = make;
        this.model = model;
        this.year = year;
    }
}
