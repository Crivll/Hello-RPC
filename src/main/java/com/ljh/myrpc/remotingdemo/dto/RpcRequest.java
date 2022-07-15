package com.ljh.myrpc.remotingdemo.dto;

import lombok.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 客户端请求实体类
 *
 * @Author: ljh
 * DateTime: 2022-04-09 18:29
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}
