package com.checkpoint.tp_api;

import com.checkpoint.tp_api.query.TeQueryBuilder;

/**
 * Created by edanha on 3/28/2017.
 */
public class Program {
    public static void main(String[] args) throws Exception{
        new TeQueryBuilder()
                .parseArgs(args) //Can use setArgMap() instead
                .build()
                .execute();
    }
}
