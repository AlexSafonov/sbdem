package com.entr.sbdem.service;

import com.entr.sbdem.config.FileUploadPathPropertiesImpl;
import com.entr.sbdem.exception.StorageException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileSystemStorageServiceTest {
    @Mock
    private FileUploadPathPropertiesImpl uploadPathProperties;
    private FileSystemStorageService storageService;

    @Before
    public void setUp(){
      storageService  = new FileSystemStorageService(uploadPathProperties);
    }

    @Test(expected = StorageException.class)
    public void storeNonImageFile_expectStorageException(){
        Mockito.when(uploadPathProperties.getPathToUploadImages())
                .thenReturn(Paths.get(FileSystemView
                                .getFileSystemView()
                                .getHomeDirectory()
                                .getAbsolutePath())
                        .resolve("sbdem\\upload\\uploadImages"));

        MockMultipartFile mockMultipartFile = new MockMultipartFile("mlp",
                "image.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "<<image.jpg>>".getBytes());
        storageService.storeUserAvatarImage(mockMultipartFile, "any");
        Mockito.verify(uploadPathProperties).getPathToUploadImages();
    }

    @Test
    public void testDeleteOfUserFile() throws Exception{
        Mockito.when(uploadPathProperties.getPathToUploadImages())
                .thenReturn(Paths.get(FileSystemView
                        .getFileSystemView()
                        .getHomeDirectory()
                        .getAbsolutePath())
                        .resolve("sbdem\\sbdem\\upload\\uploadImages"));

        Path tempFilesPath = uploadPathProperties.getPathToUploadImages().resolve("tempDirSimulatingUserDir");
        Files.createDirectory(tempFilesPath);
        storageService.deleteAllFilesOfUser("tempDirSimulatingUserDir");
    }

}
