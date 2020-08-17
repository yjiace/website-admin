package cn.smallyoung.websiteadmin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.util.UPYunUtil;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author smallyoung
 * @date 2020/8/16
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${article.img}")
    private String dirPath;

    @PostMapping("img")
    public String uploadImg(MultipartFile file, String path) throws IOException, UpException {
        if(file == null){
            throw new NullPointerException("参数错误");
        }
        String fileName = file.getOriginalFilename();
        if(StrUtil.isBlank(fileName)){
            throw new NullPointerException("参数错误");
        }
        //重命名图片地址
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String localFileName = IdUtil.simpleUUID() + fileSuffix;
        String filePath = dirPath + File.separator + path + File.separator + localFileName;
        FileUtil.touch(filePath);
        FileWriter writer = new FileWriter(filePath, "UTF-8");
        writer.writeFromStream(file.getInputStream());
        //上传到又拍云
        Response response = UPYunUtil.writeFile("/article/" + path + "/" + localFileName, writer.getFile(), null);
        return response.isSuccessful() ? localFileName : "";
    }
}
