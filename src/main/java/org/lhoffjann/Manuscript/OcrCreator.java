package org.lhoffjann.Manuscript;
// Imports the Google Cloud client library

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OcrCreator {
    public  void createOCR(Faksimile faksimile) throws Exception {

        Dotenv dotenv = Dotenv.load();
        Path credPath = Path.of(System.getProperty("user.dir"),dotenv.get("google_credentials_path"));

        GoogleCredentials myCredentials = GoogleCredentials.fromStream(
                new FileInputStream(credPath.toString()));

        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
                        .build();


        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(faksimile.getJPGPath().toString()));
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }
                try {
                    EntityAnnotation annotation = res.getTextAnnotationsList().get(0);

                    try (PrintWriter out = new PrintWriter(faksimile.getOCRPath().toFile())) {
                        out.println(annotation.getDescription());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                //for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    //System.out.println(annotation.getDescription());
                    // System.out.format("Position : %s%n", annotation.getBoundingPoly());
                //}
            }
        }
    }
}
    // Detects text in the specified image.
