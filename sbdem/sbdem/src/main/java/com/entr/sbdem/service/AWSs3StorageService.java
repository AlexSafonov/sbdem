package com.entr.sbdem.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.entr.sbdem.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class AWSs3StorageService implements StorageService {

    @Value("${aws.bucketName}")
    private String bucketName;

    private final String uploadDir;

    private final AmazonS3 awsS3Client;

    public AWSs3StorageService(AmazonS3 amazonS3) {
        this.awsS3Client = amazonS3;
        uploadDir = "upload/uploadImages/";
    }

    @Override
    public void storeUserAvatarImage(MultipartFile file, String username) {
        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            String filename = file.getOriginalFilename();

            if(!fileIsImage(file)){
                throw new StorageException("Uploaded file is not a valid image. Only JPG, PNG and GIF files are allowed. Filename: " + file.getOriginalFilename());
            }
            //Create directory for user or use existed
            String userDir = uploadDir+username+"/";
            if(awsS3Client.doesObjectExist(bucketName, userDir)){
                ObjectMetadata folderMetadata = new ObjectMetadata();
                folderMetadata.setContentLength(0);
                InputStream inputStream = new ByteArrayInputStream(new byte[0]);
                try{
                    awsS3Client.putObject( bucketName , userDir, inputStream, folderMetadata);
                } catch (AmazonClientException e){
                    throw new StorageException("AmazonClientException: " + file.getOriginalFilename());
                }

            }
            //Check if file with this name already exists and upload it
            if(!awsS3Client.doesObjectExist(bucketName, userDir+filename)){
                //Clear user dir. User can have only 1 image for avatar
                ObjectListing objLits = awsS3Client.listObjects(bucketName , userDir);
                for(S3ObjectSummary summary: objLits.getObjectSummaries()){
                    try {
                        awsS3Client.deleteObject(bucketName,summary.getKey());
                    }  catch (AmazonClientException e){
                        throw new StorageException("AmazonClientException: " + file.getOriginalFilename());
                    }
                }
                ObjectMetadata fileMetadata = new ObjectMetadata();
                fileMetadata.setContentLength(file.getSize());
                fileMetadata.setCacheControl(file.getContentType());
                try {
                    awsS3Client.putObject(bucketName, userDir+filename, file.getInputStream(), fileMetadata);
                }  catch (AmazonClientException e){
                    throw new StorageException("AmazonClientException: " + file.getOriginalFilename());
                }
            }
        } catch (AmazonClientException | IOException e){
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void deleteAllFilesOfUser(String username) {
        String pathToUserFiles = uploadDir+username+"/";
        if(awsS3Client.doesObjectExist(bucketName , pathToUserFiles)){
            try   {
                awsS3Client.deleteObject(bucketName,pathToUserFiles);
            } catch(AmazonClientException e) {
                throw new StorageException("Failed to delete files of  " + username, e);
            }
        }
    }

    @Override
    public boolean fileIsImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        //check if file is image.
        Optional<String> fileExt = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        return imageTypList.contains(fileExt.get());
    }
}
