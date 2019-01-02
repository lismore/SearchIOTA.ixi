package com.searchiota.ixi;

import org.iota.ict.Ict;
import org.iota.ict.utils.Properties;

/**
 * This class is just for testing the IXI, so we don't have to run Ict manually.
 * */
public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.ixiEnabled = true;
        new Ict(properties);

        new SearchIotaIxi(properties.name, "anonymous", "");
    }
}