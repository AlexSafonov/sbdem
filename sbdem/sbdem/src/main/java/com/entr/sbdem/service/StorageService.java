package com.entr.sbdem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void storeUserAvatarImage(MultipartFile file, String username);
    Stream<Path> loadAll();
    Path load(String filename);
    void deleteFile();
    void deleteAllFilesOfUser(String username);
    boolean fileIsImage(MultipartFile file);

}
