package com.musicstore.bluevelvet.domain.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

@Log4j2
@Service
public class MassStorageService {

    //STUB
    public boolean addPicture(byte[] data, String name) throws Exception {

        FileSystemResource picture = new FileSystemResource("src/main/resources/mass_storage/pics/" + name + ".webp");
        OutputStream stream = picture.getOutputStream();
        if (!picture.isWritable()){
            throw new Exception("could not open stream for writing at " + picture.getPath() );
        }
        stream.write(data);
        return true;
    }
}