package com.wlyykf.mall.controller;

import cn.hutool.core.date.DateUtil;
import com.wlyykf.mall.config.AppConfig;
import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.exception.BusinessException;
import com.wlyykf.mall.utils.StringUtil;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Resource
    private AppConfig appConfig;

    /**
     * 获取资源
     * @param response 响应
     * @param sourceName 资源路径
     */
    @GetMapping("/getResource")
    public void getResource(HttpServletResponse response, @RequestParam @NotNull String sourceName) {
        if (!StringUtil.pathIsOk(sourceName)) {
            throw new BusinessException(500, "参数错误");
        }
        String suffix = StringUtil.getFileSuffix(sourceName);
        if (suffix == null) {
            throw new BusinessException(500, "参数错误");
        }
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }

    /**
     * 上传图片
     * @param file 图片
     * @return 图片路径
     */
    @PostMapping("/uploadImage")
    public ResponseVO<String> uploadImage(@NotNull MultipartFile file) throws IOException {
        String day = DateUtil.format(new Date(), "yyyyMMdd");
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_IMAGE + day;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String suffix = StringUtil.getFileSuffix(fileName);
        String realFileName = StringUtil.getRandomString(30) + suffix;
        String filePath = folder + "/" + realFileName;
        file.transferTo(new File(filePath));

        return ResponseVO.success(Constants.FILE_IMAGE + day + "/" + realFileName);
    }

    /**
     * 读取文件
     */
    private void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常", e);
        }
    }
}
