package com.shuanghu.hrms.utils;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileUtil {
    // 基础上传路径（建议在配置文件中配置）
    private static final String BASE_UPLOAD_PATH = "F:\\upLoadFile";

    /**
     * 单文件上传（返回存储的文件名）
     * @param part 上传的文件Part对象
     * @param subDir 子目录（按业务分类，如"user_avatar/"）
     * @return 存储的文件名（UUID+原后缀，避免重名）
     */
    public static String uploadSingleFile(Part part, String subDir) throws IOException {
        // 1. 验证文件类型
        String contentType = part.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) { // 示例：仅允许图片
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 2. 生成唯一文件名
        String originalFileName = part.getSubmittedFileName();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        // 3. 创建目录（按日期分目录，避免单目录文件过多）
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fullDir = BASE_UPLOAD_PATH + File.separator + subDir + File.separator + dateDir;
        File dir = new File(fullDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 保存文件
        String savePath = fullDir + File.separator + fileName;
        part.write(savePath);
        return dateDir + "/" + fileName; // 返回相对路径，便于存储数据库
    }

    /**
     * 多文件上传
     * @param parts 文件Part集合
     * @param subDir 子目录
     * @return 所有文件的相对路径数组
     */
    public static String[] uploadMultiFiles(Part[] parts, String subDir) throws IOException {
        String[] filePaths = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            filePaths[i] = uploadSingleFile(parts[i], subDir);
        }
        return filePaths;
    }

    /**
     * 获取文件完整路径
     * @param relativePath 数据库存储的相对路径
     * @return 完整本地路径
     */
    public static String getFullPath(String relativePath, String subDir) {
        return BASE_UPLOAD_PATH + File.separator + subDir + File.separator + relativePath;
    }
}