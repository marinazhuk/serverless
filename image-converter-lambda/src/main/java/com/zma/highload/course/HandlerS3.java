package com.zma.highload.course;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HandlerS3 implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLogger logger = context.getLogger();

        S3EventNotification.S3EventNotificationRecord record = event.getRecords().get(0);

        String srcBucket = record.getS3().getBucket().getName();
        String srcKey = record.getS3().getObject().getUrlDecodedKey();

        String destFileName = getDestinationFileName(srcKey);

        logger.log("Destination file name: " + destFileName);

        // Download the image from S3 into a stream
        S3Client s3Client = S3Client.builder().build();
        InputStream s3Object = getObject(s3Client, srcBucket, srcKey);

        try {

            // Read the source image
            BufferedImage srcImage = ImageIO.read(s3Object);

            ByteArrayOutputStream outputPng = new ByteArrayOutputStream();
            ImageIO.write(srcImage, "png", outputPng);

            ByteArrayOutputStream outputGif = new ByteArrayOutputStream();
            ImageIO.write(srcImage, "gif", outputGif);

            ByteArrayOutputStream outputBMP = new ByteArrayOutputStream();
            ImageIO.write(srcImage, "bmp", outputBMP);


            putObject(s3Client, outputPng, srcBucket, destFileName, "png");
            logger.log("Uploaded png file to " + srcBucket);

            putObject(s3Client, outputGif, srcBucket, destFileName, "gif");
            logger.log("Uploaded gif file to " + srcBucket);

            putObject(s3Client, outputPng, srcBucket, destFileName, "bmp");
            logger.log("Uploaded bmp file to " + srcBucket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Ok";
    }

    private InputStream getObject(S3Client s3Client, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    private void putObject(S3Client s3Client, ByteArrayOutputStream outputStream,
                           String bucket, String key, String imageType) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Integer.toString(outputStream.size()));
        metadata.put("Content-Type", "image/" + imageType);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key + "." + imageType)
                .metadata(metadata)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(outputStream.toByteArray()));
    }

    private static String getDestinationFileName(String srcKey) {
        String fileName;
        if (srcKey.toLowerCase().endsWith("jpg")) {
            fileName = srcKey.toLowerCase().replace(".jpg", "");
        } else {
            fileName = srcKey.toLowerCase().replace(".jpeg", "");
        }
        return fileName;
    }
}
