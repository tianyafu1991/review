package com.tianya.hivemetastoreservice;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HiveMetastoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HiveMetastoreServiceApplication.class, args);
    }

    @Bean
    public HiveMetaStoreClient hiveMetaStoreClient() {
        System.setProperty("HADOOP_USER_NAME","hadoop");
        HiveConf hiveConf = new HiveConf();
        HiveMetaStoreClient client = null;
        try {
            client = new HiveMetaStoreClient(hiveConf);
        } catch (MetaException e) {
            e.printStackTrace();
        }

        return client;
    }

}
