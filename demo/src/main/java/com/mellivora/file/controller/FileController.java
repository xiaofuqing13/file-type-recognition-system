package com.mellivora.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mellivora.file.mapper.File;
import com.mellivora.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * <p>描述</p>
 *
 * @author wangchao 2024/10/13 14:26
 * @version 0.1
 **/
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // 初始化入库的实体类
        File.FileBuilder fileBuilder = File.builder().uploadTime(new Date())
                .uploadTime(new Date())
                .comparisonResult(false);
        try {
            String name = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();

            fileBuilder.fileName(name)
                    .fileSize((long) fileBytes.length)
                    .fileContent(fileBytes);
            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 创建请求体
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            // 创建请求实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送 POST 请求到 Python 服务
            String pythonServiceUrl = "http://127.0.0.1:5001/process_file";
            // 假设 Python 服务运行在 5000 端口
            ResponseEntity<String> response = restTemplate.postForEntity(pythonServiceUrl, requestEntity, String.class);
            String body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK) {
                // 需要解析python返回的参数。{status:true, fileType: .doc}
                JSONObject responseObject = JSON.parseObject(body);
                String actualType = responseObject.getString("actual-type");
                String predictedType = responseObject.getString("predicted-type");
                Boolean comparison = responseObject.getBoolean("comparison-result");
                fileBuilder
                        .predictedType(predictedType)
                        .actualType(actualType)
                        .comparisonResult(comparison)
                        .recognitionTime(System.currentTimeMillis());
            }
            log.info("file uploaded successfully : " + response.getBody());
            return ResponseEntity.ok(body);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("文件识别失败: " + e.getMessage());
        } finally {
            fileService.insertResult(fileBuilder.build());
        }
    }

    @GetMapping("list")
    public PageInfo<File> getFiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return fileService.getFiles(page, pageSize);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        File file = fileService.getFileById(id);
        if (file == null || file.getFileContent() == null) {
            return ResponseEntity.notFound().build();
        }
        String fileName = file.getFileName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getComparisonResult() ? getFileNameWithoutExtension(fileName) + "." + file.getActualType() : fileName);

        return new ResponseEntity<>(file.getFileContent(), headers, HttpStatus.OK);
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

}