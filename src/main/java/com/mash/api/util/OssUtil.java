package com.mash.api.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class OssUtil {

    private static final Logger log = LoggerFactory.getLogger(OssUtil.class);

    /**
     * 外网：oss-cn-shenzhen.aliyuncs.com
     * 内网：oss-cn-shenzhen-internal.aliyuncs.com
     */
//    private static String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
//    private static String accessKeyId = "6xNC1QGpNtg5zvbX";
//    private static String accessKeySecret = "ofQucBMWCRZKPp7pkNBj0Jl7JEoL7g";

//    private static String bucketName = "kunrnbucket";

    private static String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
    private static String accessKeyId = "LTAI2IxVfJpH8gDt";
    private static String accessKeySecret = "lEoZ0n3OhdQihbF7l6m9xrGhbJoT5I";

    private static String bucketName = "hncg001";

    public static String uploadImgByFileInputStream(String fileName, InputStream inputStream)
    {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流
        ossClient.putObject(bucketName, fileName, inputStream);
        // 关闭client
        ossClient.shutdown();

        return Const.OSS_BUCKET_WEB + fileName;
    }

    /**
     * 上传图片
     * @param imageName
     * @param content
     * @return
     */
    public static String putImage(String imageName, byte[] content)
    {
        log.info("图片上传start");
        // 图片保存后的访问路径
        String url = Const.OSS_BUCKET_WEB + imageName;

        // 初始化OSSClient实例
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try
        {
            PutObjectResult result = client.putObject(bucketName, imageName, new ByteArrayInputStream(content));
        }
        catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
            url = "error";
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
            url = "error";
        } finally {
            // 关闭实例
            client.shutdown();
            return url;
        }
    }

    /**
     * 删除文件
     * @param key 文件全名
     */
    public static void deleteFile(String key)
    {
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        client.deleteObject(bucketName, key);
    }

    public static void main(String[] args)
    {
//        deleteFile("jujuju.jpg");

        File file = new File("C:\\tools\\img\\IMG_3300.JPG");
        try {
            InputStream inputStream = new FileInputStream(file);

            uploadImgByFileInputStream("test.jpg", inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("oss-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < 1000000; i++) {
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("0123456789011234567890\n");
        }
        writer.close();

        return file;
    }
}
