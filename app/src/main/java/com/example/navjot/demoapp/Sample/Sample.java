package com.example.navjot.demoapp.Sample;

/**
 * Created by navjot on 10/14/16.
 */

class Sample {
    public static native void sayHi(String how, int count);
    static {
        System.loadLibrary("demo");
    }

    public static void main(String... args) {
        sayHi("23", 2);
    }
}
