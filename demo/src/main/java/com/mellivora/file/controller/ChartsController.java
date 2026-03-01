package com.mellivora.file.controller;

import com.alibaba.fastjson.JSONObject;
import com.mellivora.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>描述</p>
 *
 * @author wangchao 2024/10/14 10:49
 * @version 0.1
**/
@Slf4j
@RestController
@RequestMapping("/api/charts")
public class ChartsController {

    @Autowired
    private FileService fileTypeService;


    @GetMapping("pie")
    public ResponseEntity<String> getPieChart() {
        return ResponseEntity.ok(JSONObject.toJSONString(fileTypeService.selectPie()));
    }

    @GetMapping("fullFileBar")
    public ResponseEntity<String> getFullFileBarChart(@Param("startDate") String startDate, @Param("endDate") String endDate) {
        return ResponseEntity.ok(JSONObject.toJSONString(fileTypeService.selectFullFileBar(startDate, endDate)));
    }

    @GetMapping("successFileBar")
    public ResponseEntity<String> getSuccessFileBarChart(@Param("startDate") String startDate, @Param("endDate") String endDate) {
        return ResponseEntity.ok(JSONObject.toJSONString(fileTypeService.selectSuccessFileBar(startDate, endDate)));
    }


}
