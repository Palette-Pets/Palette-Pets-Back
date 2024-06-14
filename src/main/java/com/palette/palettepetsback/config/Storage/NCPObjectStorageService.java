package com.palette.palettepetsback.config.Storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class NCPObjectStorageService {
    private final AmazonS3 s3;

    public NCPObjectStorageService(NaverConfiguration naverConfiguration){
        s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder
                                .EndpointConfiguration(naverConfiguration.getEndPoint(),
                                naverConfiguration.getRegionName())
                )
                .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(naverConfiguration.getAccessKey(),
                                        naverConfiguration.getSecretKey())
                        )
                )
                .build();
    }

    public String uploadFile(String bucketName, String directoryPath, MultipartFile img){
        if(img.isEmpty()) return null;

        try(InputStream fileIn = img.getInputStream()){
            String fileName = UUID.randomUUID().toString();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(img.getContentType());

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName
                            , directoryPath + "/" + fileName
                            , fileIn
                            , objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putObjectRequest);

            return fileName;
        }catch(Exception e){
            throw new RuntimeException("파일 업로드 에러 " + e);// 강제로 Exception 발생시키는 throw
        }

    }

    public String deleteFile(String bucketName, String fileName) {

        // delete object
        try {

            s3.deleteObject(bucketName, fileName);
            log.info("Object deleted: '{}'", fileName);
        } catch (AmazonS3Exception e) {
            log.error("AmazonS3Exception deleting file", e);
            throw new RuntimeException("파일 삭제 에러 " + e.getMessage());
        } catch (SdkClientException e) {
            log.error("SdkClientException deleting file", e);
            throw new RuntimeException("파일 삭제 에러 " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception deleting file", e);
            throw new RuntimeException("파일 삭제 에러 " + e.getMessage());
        }
        return fileName;
    }
}
