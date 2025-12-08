package com.musicstore.bluevelvet.domain.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;

@Log4j2
@Service
public class MassStorageService {

    private final String BASE_PATH = "src/main/resources/mass_storage/pics/";

    // GRAVAR IMAGEM
    public boolean addPicture(byte[] data, String name) throws Exception {

        FileSystemResource picture = new FileSystemResource(BASE_PATH + name + ".webp");
        OutputStream stream = picture.getOutputStream();
        if (!picture.isWritable()) {
            throw new Exception("could not open stream for writing at " + picture.getPath());
        }
        stream.write(data);
        stream.close();
        return true;
    }

    // REMOVER IMAGEM ANTIGA
    public boolean removePicture(String uuid) {
        try {
            File file = new File(BASE_PATH + uuid + ".webp");

            if (file.exists()) {
                return file.delete();
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

}