package com.entr.sbdem.config;

import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUploadPathPropertiesImpl implements  FileUploadPathProperties{
    //Root path - for me it is desktop
    private final Path pathToHome = Paths.get(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
    //Path to upload dir. Where all upload files will be stored.
    private final Path pathToUploadDir;
    //Where you going to store uploaded images.
    private final Path pathToUploadImages;
    //this will create string without "file:" and any amount of slashes at the start.
    private final String pathToUploadDirToUriToString ;

    public FileUploadPathPropertiesImpl() {
        this.pathToUploadDir = pathToHome.resolve("sbdem\\upload");
        this.pathToUploadImages = pathToHome.resolve("sbdem\\upload\\uploadImages");
        this.pathToUploadDirToUriToString = pathToHome.resolve("sbdem\\upload").toUri().toString().replaceFirst("\\w+\\:/+","");
    }

    @Override
    public Path getPathToUploadDir() {
        return pathToUploadDir;
    }
    @Override
    public Path getPathToUploadImages() {
        return pathToUploadImages;
    }
    @Override
    public String getPathToUploadDirToUriToString() {
        return pathToUploadDirToUriToString;
    }

}
