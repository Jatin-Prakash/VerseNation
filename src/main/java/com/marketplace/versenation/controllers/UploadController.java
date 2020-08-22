package com.marketplace.versenation.controllers;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.models.Image;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.music.PendingMusic;
import com.marketplace.versenation.payload.requests.SingleMusicRequest;
import com.marketplace.versenation.payload.responses.ApiResponse;
import com.marketplace.versenation.repository.ImageRepository;
import com.marketplace.versenation.repository.PendingMusicRepository;
import com.marketplace.versenation.repository.UserRepository;
import javassist.bytecode.stackmap.BasicBlock;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final List<String> imageContentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");
    private static final List<String> musicContentTypes = Arrays.asList("audio/ogg","audio/mpeg");
    @Autowired
    ServletContext context;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    PendingMusicRepository pendingMusicRepository;

    @RequestMapping(value="/picture/{imageType}",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadPicture(@RequestParam(required = true,value = "file")MultipartFile file
    ,@PathVariable String imageType){
        //check path
        String uploadType="none";
        if(imageType.equals("profilepic")){
            uploadType="profilepicture";
        }else if(imageType.equals("coverpic")){
            uploadType="coverpicture";
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //get the user that's uploading profile picture first
        User user;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
             user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"expired token"),HttpStatus.BAD_REQUEST);
        }
        //check if file is not empty
        Image image = new Image();
        Path path;
        if (file.isEmpty()) {
            return new ResponseEntity(new ApiResponse(false,"file is empty"), HttpStatus.BAD_REQUEST);
        }
        //check if file is an image file
        String fileContentType = file.getContentType();
        if(!imageContentTypes.contains(fileContentType)){
            return new ResponseEntity(new ApiResponse(false,"file is not an image file"),HttpStatus.BAD_REQUEST);
        }
        try{
            byte[] bytes = file.getBytes();
            //get file extension
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            //make a directory using user id
            Path currentPath = Paths.get(".");
            Path absolutePath = currentPath.toAbsolutePath();
            String uploadsFile = absolutePath+"/src/main/resources/uploads/images/";
            File idDirectory = new File(uploadsFile,  user.getId().toString());
            File typeDirectory = new File(idDirectory,uploadType);
            if(!idDirectory.exists()){
                idDirectory.mkdir();
            }
            if(!typeDirectory.exists()){
                typeDirectory.mkdir();
            }
            String imageName = image.generateUniqueFileName()+"."+extension;
            path = Paths.get(typeDirectory.getPath()+"/"+ imageName);
            Files.write(path,bytes);
            //image.setPath(path.toString());
            image.setPath("/image/"+user.getId().toString()+"/"+uploadType+"/"+imageName);
        } catch (IOException e){
            //failed uploading image
            return new ResponseEntity(new ApiResponse(false,"problem writing image to disk"),HttpStatus.BAD_REQUEST);
        }
        Image savedImage = imageRepository.save(image);
        if(imageType.equals("profilepic")){
            user.addProfilePicture(savedImage);
        }else if(imageType.equals("coverpic")){
           user.addCoverPicture(savedImage);
        }

          userRepository.save(user);
        //image uploaded succesfully
        return ResponseEntity.ok(new ApiResponse(true,uploadType+" uploaded succesfully"));
       // File uploadedFile = new File(absolutePath, "your_file_name");
    }
    /**
     * upload music controller
     * **/
    @RequestMapping(value="/music/single",method = RequestMethod.POST)
    public ResponseEntity<Object> uploadSingle(@RequestParam(required = true,value = "music")MultipartFile music
        //   ,@RequestParam SingleMusicRequest singleMusicRequest
        ,@RequestParam String title
    ){
        //get the user that's uploading profile picture first
        User user;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
        }catch (Exception e){
            return new ResponseEntity(new ApiResponse(false,"expired token"),HttpStatus.BAD_REQUEST);
        }
        //check if account is fan
        if(user.getAccountType().toString().equals("FAN")){
            return new ResponseEntity(new ApiResponse(false,"only creators can upload music"),HttpStatus.BAD_REQUEST);
        }
        //check if file is empty
        if(music.isEmpty()){
            return new ResponseEntity(new ApiResponse(false,"music file is empty"),HttpStatus.BAD_REQUEST);
        }
        //check if file is music file or not
        String fileType="";
        try {
            Tika tika = new Tika();
            fileType = tika.detect(music.getBytes());
        } catch (IOException e){

        }
        if(!musicContentTypes.contains(fileType)){
            return new ResponseEntity(new ApiResponse(false,"not a music file"),HttpStatus.BAD_REQUEST);
        }
        /**
         * now try to upload music to server
         * **/
        Path path;
        PendingMusic pendingMusic = new PendingMusic();
        try{
            byte[] bytes = music.getBytes();
            //get file extension
            String extension = FilenameUtils.getExtension(music.getOriginalFilename());
            //make a directory using user id
            Path currentPath = Paths.get(".");
            Path absolutePath = currentPath.toAbsolutePath();
            String uploadsFile = absolutePath+"/src/main/resources/uploads/music/";
            File idDirectory = new File(uploadsFile,  user.getId().toString());
            if(!idDirectory.exists()){
                idDirectory.mkdir();
            }

            String imageName = pendingMusic.generateUniqueFileName()+"."+extension;
            path = Paths.get(idDirectory.getPath()+"/"+ imageName);
            Files.write(path,bytes);

            pendingMusic.setLocation(path.toString());
        } catch (IOException e){
            //failed uploading image
            return new ResponseEntity(new ApiResponse(false,"problem uploading music to disk"),HttpStatus.BAD_REQUEST);
        }
        pendingMusic.setTitle(title);
        pendingMusic.setMusicCreator(user.getCreatorAccount());

        pendingMusicRepository.save(pendingMusic);

        return ResponseEntity.ok(new ApiResponse(true,"music uploaded succesfully waiting on approval now"));

    }




    //get profile pic by id
//    @GetMapping(value="get/profilepic/{id}")
//    public ResponseEntity<?> getProfilePicture(@PathVariable long id){
//      User user;
//        try{
//            user = userRepository.findById(id).get();
//        }catch (Exception e){
//            return new ResponseEntity(new ApiResponse(false,"user doesnt exist"),HttpStatus.BAD_REQUEST);
//        }
//        List<Image> profilePictures = user.getProfilePictures();
//       if(profilePictures.isEmpty()){
//           return new ResponseEntity(new ApiResponse(false,"user doesnt have a profile pic"),HttpStatus.BAD_REQUEST);
//       }//if profile picture is not empty
//        byte[] data;
//        String pathOfImage;
//        try {
//            pathOfImage = profilePictures.get(profilePictures.size() - 1).getPath();
//            Path fileLocation = Paths.get(pathOfImage);
//             data = Files.readAllBytes(fileLocation);
//        }catch (IOException e){
//            return new ResponseEntity(new ApiResponse(false,"couldn't load picture"),HttpStatus.BAD_REQUEST);
//        }
//        ImageResponse imageResponse = new ImageResponse();
//        imageResponse.setImageType(FilenameUtils.getExtension(pathOfImage));
//        imageResponse.setImageBody(data);
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.IMAGE_PNG);
////        headers.setContentLength(data.length);
////        return new ResponseEntity<>(imageResponse,headers,HttpStatus.OK);
//        return ResponseEntity.ok(imageResponse);
//    }
}
