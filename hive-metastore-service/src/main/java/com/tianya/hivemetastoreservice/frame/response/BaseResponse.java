package com.tianya.hivemetastoreservice.frame.response;

import com.tianya.hivemetastoreservice.frame.enums.StatusCode;
import java.io.Serializable;

public class BaseResponse implements Serializable {

    /**
     * 状态
     */
    private int statusCode = StatusCode.OPERATION_SUCCESS.getCode();

    /**
     * 错误信息 返回错误时必填
     */
    private String statusMsg = StatusCode.OPERATION_SUCCESS.getMsg();
    /**
     * 数据
     */
    private Object data;


    public String getStatusMsg() {
        return statusMsg;
    }

    public BaseResponse setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public BaseResponse setData(Object data) {
        this.data = data;
        return this;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public BaseResponse setStatusCode(StatusCode statusCode) {
        this.setStatusCode(statusCode.getCode());
        this.setStatusMsg(statusCode.getMsg());
        return this;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "statusCode=" + statusCode +
                ", statusMsg='" + statusMsg + '\'' +
                ", Data=" + data +
                '}';
    }

    /**
     * @param data
     * @return BaseResponse
     * @description成功结果的的快速生成
     */
    public static BaseResponse successInstance(Object data) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(StatusCode.OPERATION_SUCCESS);
        return response.setData(data);
    }

    /**
     * 错误信息的快速生成
     *
     * @param data
     * @return
     */
    public static BaseResponse errorInstance(Object data) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(StatusCode.OPERATION_ERROR);
        return response.setData(data);
    }


    /**
     * @param msg
     * @return BaseResponse
     * @description成功结果的的快速生成
     */
    public static BaseResponse successInstance(String msg) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(StatusCode.OPERATION_SUCCESS);
        response.setStatusMsg(msg);
        return response;
    }

    /**
     * @param errorMsg 错误信息
     * @return BaseResponse
     * @description错误结果的的快速生成
     */
    public static BaseResponse errorInstance(String errorMsg) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(StatusCode.SERVER_ERROR);
        return response.setStatusMsg(errorMsg);
    }

    /**
     * 创建没有查询到数据结果返回对象
     * @return
     */
    public static BaseResponse queryDataEmpty() {
        BaseResponse response = new BaseResponse();
        StatusCode statusCode = StatusCode.QUERY_DATA_EMPTY;
        response.setStatusCode(statusCode);
        return response;
    }

    /**
     * 创建没有id匹配信息返回对象
     * @return
     */
    public static BaseResponse queryIdEmpty() {
        BaseResponse response = new BaseResponse();
        StatusCode statusCode = StatusCode.QUERY_ID_EMPTY;
        response.setStatusCode(statusCode);
        return response;
    }


    public static void main(String[] args) {
        System.out.println(BaseResponse.successInstance("测试成功"));
        System.out.println(BaseResponse.errorInstance("测试失败"));
        System.out.println(BaseResponse.errorInstance(null));
        System.out.println(BaseResponse.queryDataEmpty());
    }


}
