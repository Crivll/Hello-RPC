package com.ljh.myrpc.remoting.transport.socket.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: ljh
 * DateTime: 2022-04-09 16:57
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {

    private String content;
}
