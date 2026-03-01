package com.mellivora.file.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * <p>描述</p>
 *
 * @author wangchao 2024/10/15 19:30
 * @version 0.1
 **/
@Data
@Builder
@AllArgsConstructor
public class File {
    private Long id;
    private String fileName;
    private Long fileSize;
    private Date uploadTime;
    private Long recognitionTime;
    private String predictedType;
    private String actualType;
    private Boolean comparisonResult;
    private byte[] fileContent;
}
