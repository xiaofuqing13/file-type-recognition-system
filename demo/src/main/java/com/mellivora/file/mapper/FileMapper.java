package com.mellivora.file.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * <p>描述</p>
 *
 * @author wangchao 2024/10/15 11:06
 * @version 0.1
 **/
@Mapper
public interface FileMapper {

    @Select("SELECT * FROM files ORDER BY upload_time DESC")
    List<File> selectFiles();

    @Insert("INSERT INTO files (file_name, file_size, upload_time, recognition_time, predicted_type, actual_type, comparison_result, file_content) " +
            "VALUES (#{fileName}, #{fileSize}, #{uploadTime}, #{recognitionTime}, #{predictedType}, #{actualType}, #{comparisonResult}, #{fileContent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFile(File file);

    @Insert("<script>" +
            "INSERT INTO files (file_name, file_size, upload_time, recognition_time, predicted_type, actual_type, comparison_result, file_content) VALUES " +
            "<foreach collection='files' item='file' separator=','>" +
            "(#{file.fileName}, #{file.fileSize}, #{file.uploadTime}, #{file.recognitionTime}, #{file.predictedType}, #{file.actualType}, #{file.comparisonResult}, #{file.fileContent})" +
            "</foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int batchInsertFiles(@Param("files") List<File> files);

    @Select("select actual_type, count(1) as count from files where comparison_result = 1 group by actual_type")
    List<Map<String, Integer>> selectPie();


    @Select("SELECT * FROM files WHERE id = #{id}")
    File getFileById(Long id);

    @Select("select actual_type, count(1) as count from files  where comparison_result = 1  group by actual_type")
    List<Map<String, Integer>> selectSuccessFileBar(@Param("startDate") String startDate, @Param("startDate") String endDate);

    @Select("<script>" +
            "SELECT DATE_FORMAT(upload_time, '%Y-%m') AS month, " +
            "COUNT(*) AS fileCount " +
            "FROM files " +
            "<where>" +
            "<if test='startDate != null and endDate != null'>" +
            "DATE_FORMAT(upload_time, '%Y-%m') BETWEEN #{startDate} AND #{endDate} " +
            "</if>" +
            "</where>" +
            "GROUP BY DATE_FORMAT(upload_time, '%Y-%m') " +
            "ORDER BY month DESC" +
            "</script>")
    List<Map<String, Integer>> selectFullFileBar(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
