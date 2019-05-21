package com.entr.sbdem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {
    final List<String> imageTypList = new ArrayList<>(Arrays.asList("tiff", "jpeg","jpg", "gif", "png"));

    void storeUserAvatarImage(MultipartFile file, String username);
    void deleteAllFilesOfUser(String username);
    boolean fileIsImage(MultipartFile file);

}
