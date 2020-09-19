package com.tianya.hivemetastoreservice.business.vo;

import org.apache.hadoop.hive.metastore.api.FieldSchema;

import java.io.Serializable;

public class TableSchemaVo implements Serializable {

    private String fieldName;

    private String fieldType;

    private Integer fieldIndex;

    public TableSchemaVo() {
    }

    public TableSchemaVo(String fieldName, String fieldType, Integer fieldIndex) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldIndex = fieldIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(Integer fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    @Override
    public String toString() {
        return "TableSchemaVo{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", fieldIndex=" + fieldIndex +
                '}';
    }
}
