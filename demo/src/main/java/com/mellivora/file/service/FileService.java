package com.mellivora.file.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mellivora.file.mapper.File;
import com.mellivora.file.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>描述</p>
 *
 * @author wangchao 2024/10/14 10:55
 * @version 0.1
 **/
@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;

    public int insertResult(File file) {
        return fileMapper.insertFile(file);
    }

    @Transactional
    public int batchInsertResults(List<File> results) {
        return fileMapper.batchInsertFiles(results);
    }

    public List<Map<String, Integer>> selectPie() {
        return fileMapper.selectPie();
    }

    public List<Map<String, Integer>> selectFullFileBar(String startDate, String endDate) {
        return fileMapper.selectFullFileBar(startDate, endDate);
    }

    public List<Map<String, Integer>> selectSuccessFileBar(String startDate, String endDate) {
        return fileMapper.selectSuccessFileBar(startDate, endDate);
    }

    public PageInfo<File> getFiles(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<File> files = fileMapper.selectFiles();
        return new PageInfo<>(files);
    }

    public File getFileById(Long id) {
        return fileMapper.getFileById(id);
    }

}
