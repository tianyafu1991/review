package com.tianya.hivemetastoreservice.business.controller;

import com.tianya.hivemetastoreservice.business.vo.TableSchemaVo;
import com.tianya.hivemetastoreservice.frame.util.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.*;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.thrift.TException;
import com.tianya.hivemetastoreservice.frame.response.BaseResponse;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.hadoop.hive.metastore.api.Table._Fields;

/**
 * 参考：https://cloud.tencent.com/developer/article/1050380
 */
@RestController
@RequestMapping(value = "/hiveMetaStore")
public class HiveMetaStoreController {

    @Autowired
    private HiveMetaStoreClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    //http://localhost:9019/hiveMetaStore/createTableWithPartition?tableName=test1&tableType=external&tableComment=test1
    @RequestMapping(value = "/createTableWithPartition", method = RequestMethod.POST)
    public BaseResponse createTableWithPartition(String dbName, String tableName, String tableType, String tableComment) {

        dbName = StringUtils.isBlank(dbName) ? "default" : dbName;
        tableType = "external".equals(tableType.trim().toLowerCase()) ? TableType.EXTERNAL_TABLE.name() : TableType.MANAGED_TABLE.name();
        String location = "/ruozedata/hive/" + tableName;

        // 与表partition_keys相关
        List<FieldSchema> partCols = new ArrayList<>();
        partCols.add(new FieldSchema("partitionDate", "string", "partition key"));
        // 与表table_params相关
        Map<String, String> tableParams = new HashMap<>();
        String externalFlag = TableType.EXTERNAL_TABLE.name().equals(tableType) ? "TRUE" : "FALSE";
        tableParams.put("EXTERNAL", externalFlag);
        tableParams.put("comment", tableComment.trim());
//        tableParams.put("field.delim", ",");
//        tableParams.put("serialization.format", ",");

        // 与表serde_params相关
        Map<String, String> serdeParams = new HashMap<>();
        serdeParams.put("field.delim", ",");
        serdeParams.put("serialization.format", ",");

        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema("name", "string", "name"));
        cols.add(new FieldSchema("age", "int", "age"));
        cols.add(new FieldSchema("sex", "string", "sex"));

        try {
            Table table = new TableBuilder()
                    .setDbName(dbName)
                    .setTableName(tableName)
                    .setOwner("hadoop")
                    .setType(tableType)
                    .setViewExpandedText(null)
                    .setViewOriginalText(null)
                    .setLocation(location)
                    .setCols(cols)
                    .setTableParams(tableParams)
                    .setPartCols(partCols) // 与表partition_keys相关
//                    .setStorageDescriptorParams(storageDescriptorParams)//这个是错的
                    .setSerdeParams(serdeParams) // 与表serde_params相关
                    .build();

            client.createTable(table);
            return BaseResponse.successInstance("建表成功");
        } catch (TException e) {
            e.printStackTrace();
            return BaseResponse.errorInstance("建表失败");
        }

    }


    /**
     * 查询出某个数据库(default)下所有的表: 表名
     *
     * @param dbName 数据库名 不填写默认为default
     * @return
     */
    //http://localhost:9019/hiveMetaStore/getAllTableNames?dbName=ruozedata
    @RequestMapping(value = "/getAllTableNames", method = RequestMethod.GET)
    public BaseResponse getAllTableNames(String dbName) {
        if (StringUtils.isBlank(dbName)) {
            dbName = "default";
        }
        List<String> tables = null;
        try {
            tables = client.getAllTables(dbName);
            return BaseResponse.successInstance(tables);
        } catch (TException ex) {
            LOGGER.error(ex.getMessage());
            return BaseResponse.errorInstance(ex.getMessage());
        }
    }

    /**
     * 查询出某个数据库(default)下某个的表的字段信息: 字段名称、字段类型、字段index
     *
     * @param dbName    数据库名 不填写默认为default
     * @param tableName 表名
     * @return
     */
    //http://localhost:9019/hiveMetaStore/getTableSchema?dbName=ruozedata&tableName=emp
    @RequestMapping(value = "/getTableSchema", method = RequestMethod.GET)
    public BaseResponse getTableSchema(String dbName, String tableName) {
        if (StringUtils.isBlank(dbName)) {
            dbName = "default";
        }
        if (StringUtils.isBlank(tableName)) {
            return BaseResponse.errorInstance("没有填写表名");
        }
        List<FieldSchema> schema = null;
        List<TableSchemaVo> results = new ArrayList<>();
        try {
            schema = client.getSchema(dbName, tableName);
            for (int i = 0; i < schema.size(); i++) {
                FieldSchema fieldSchema = schema.get(i);
                results.add(new TableSchemaVo(fieldSchema.getName(), fieldSchema.getType(), i));
            }
            return BaseResponse.successInstance(results);
        } catch (TException ex) {
            LOGGER.error(ex.getMessage());
            return BaseResponse.errorInstance(ex.getMessage());

        }
    }


    public static void main(String[] args) {
        System.out.println(System.getenv("HADOOP_USER_NAME"));
    }
}
