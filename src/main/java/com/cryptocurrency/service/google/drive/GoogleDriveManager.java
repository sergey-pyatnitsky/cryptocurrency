package com.cryptocurrency.service.google.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static com.google.api.services.drive.DriveScopes.DRIVE;

@Service
public class GoogleDriveManager {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("classpath:service_account_credentials.json")
    private Resource credentialsFile;

    public Drive getInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credentials = GoogleCredential.fromStream(
                credentialsFile.getInputStream()).createScoped(Collections.singletonList(DRIVE));

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials).build();
    }
}
