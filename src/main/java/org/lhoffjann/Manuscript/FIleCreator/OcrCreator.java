package org.lhoffjann.Manuscript.FIleCreator;
// Imports the Google Cloud client library

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import io.github.cdimascio.dotenv.Dotenv;
import org.lhoffjann.Manuscript.Faksimile.Faksimile;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }
                boolean b = res.getTextAnnotationsList().size() == 0;
                if(b){
                    try (PrintWriter out = new PrintWriter(faksimile.getOCRPath().toFile())) {
                        out.println(0);
                    }

                }else{
                    EntityAnnotation annotation = res.getTextAnnotationsList().get(0);

                    try (PrintWriter out = new PrintWriter(faksimile.getOCRPath().toFile())) {
                        out.println(annotation.getDescription());
                    }
                }
            }
        }
    }
}
