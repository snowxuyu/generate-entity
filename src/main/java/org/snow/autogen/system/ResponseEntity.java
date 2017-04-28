package org.snow.autogen.system;

import java.io.Serializable;

/**
 * Created by snow on 2015/7/12. 所有方法的返回类型
 */
public class ResponseEntity implements Serializable {

	private static final long serialVersionUID = 7729482447094472913L;
	private Object data; // 返回数据
	private String status; // 返回状态
	private String message; // 返回消息
	private String error; // 返回错误信息
	private String code; //返回码

	public ResponseEntity() {

	}

	public ResponseEntity(Object data) {
		this.data = data;
	}

	public ResponseEntity(Object data, String status) {
		this.data = data;
		this.status = status;
	}

	public ResponseEntity(Object data, String status, String message) {
		this.data = data;
		this.status = status;
		this.message = message;
	}

	public ResponseEntity(Object data, String status, String message,
                          String error) {
		this.data = data;
		this.status = status;
		this.message = message;
		this.error = error;
	}

	public ResponseEntity(Object data, String status, String message, String error, String code) {
		this.data = data;
		this.status = status;
		this.message = message;
		this.error = error;
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
