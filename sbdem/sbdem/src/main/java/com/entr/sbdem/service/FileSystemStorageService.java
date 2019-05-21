package com.entr.sbdem.service;

import com.entr.sbdem.config.FileUploadPathProperties;
import com.entr.sbdem.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final FileUploadPathProperties uploadPathProperties;

    public FileSystemStorageService(final FileUploadPathProperties uploadPathProperties) {
        this.uploadPathProperties = uploadPathProperties;
        //Saving into external dir
        //Path is in FileUploadPathProperties.  desktop/sbdem/upload/uploadImages
        //Main path of app is desktop/sbdem/sbdem/srs/main.

    }
    @Override
    public boolean fileIsImage(MultipartFile file){
        String filename = file.getOriginalFilename();
        //check if file is image.
        Optional<String> fileExt = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        return imageTypList.contains(fileExt.get());
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
            Path userDirPath = this.uploadPathProperties.getPathToUploadImages().resolve(username).toAbsolutePath();
            File userDir = new File(userDirPath.toString());
            if(!userDir.exists()){Files.createDirectory(userDirPath);}
            //Check if file with this name already exists
            Path filepath = userDirPath.resolve(filename);
            //Clear user dir. User can have only 1 image for avatar
            for(File f: userDir.listFiles())
                if (!f.isDirectory())
                    f.delete();
            Files.copy(file.getInputStream(),filepath);
        } catch (IOException e){
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void deleteAllFilesOfUser(String username) {
        Path pathToUserFiles = uploadPathProperties.getPathToUploadImages().resolve(username).toAbsolutePath();
        if(Files.exists(pathToUserFiles)){
            try   {
                Files.delete(pathToUserFiles);
            } catch(IOException e) {
                throw new StorageException("Failed to delete files of  " + username, e);
            }
        }
    }
}
