package com.ctx.gmall0311.logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctx.gmall0311.common.Constant.GmallConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController //@RestController == @Controller + @ResponsBody
public class LoggerController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @PostMapping("log")
    public String dolog(@RequestParam("logString") String logString){
        // 1  补充服务串时间戳
        JSONObject jsonObject = JSON.parseObject(logString);

        jsonObject.put("ts",System.currentTimeMillis());

        String logjson = jsonObject.toJSONString();

        // 2 写日志 (用于离线采集)
        log.info(logjson);

        // 3 发送kafka
        if("startup".equals(jsonObject.getString("type"))){
            kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_STARTUP,logjson);
        }else{
            kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_EVENT,logjson);
        }

        return "success";
    }
}
