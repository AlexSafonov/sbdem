package com.entr.sbdem.config;

import java.nio.file.Path;

public interface FileUploadPathProperties {
    public Path getPathToUploadDir();
    public Path getPathToUploadImages();
    public String getPathToUploadDirToUriToString();
}
