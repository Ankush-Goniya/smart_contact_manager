 


package com.SCM.services.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SCM.helper.AppConstants;
import com.SCM.services.IMAGESERV;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageServiceImpl implements IMAGESERV {

    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage,String filename) {


        // String filename=UUID.randomUUID().toString();
        // ✅ Step 1: Handle empty or null file
        if (contactImage == null || contactImage.isEmpty()) {
            System.out.println("⚠️ Empty file detected — skipping upload");
            return null; // Or return a default image URL if you prefer
        }

        try {

byte[] data=new byte[contactImage.getInputStream().available()];
contactImage.getInputStream().read(data);
cloudinary.uploader().upload(data,ObjectUtils.asMap(
    "public_id",filename
));


            // ✅ Step 2: Upload file to Cloudinary directly
            // cloudinary.uploader().upload(
            //     contactImage.getBytes(),
            //     ObjectUtils.asMap(
            //         "public_id", filename,
            //         "folder", "scm_contacts" // optional: organize your uploads
            //     )
            // );

            // ✅ Step 3: Return generated URL
            return getUrlFromPublicId(filename);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary.url()
                .transformation(new Transformation<>()
                        .width(AppConstants.CONTACT_IMAGE_WIDTH)
                        .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                        .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);
    }
}
