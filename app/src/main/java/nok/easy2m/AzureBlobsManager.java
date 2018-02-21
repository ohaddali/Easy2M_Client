package nok.easy2m;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.SecureRandom;

import nok.easy2m.communityLayer.CallBack;

/**
 * Created by pc on 2/21/2018.
 */

public class AzureBlobsManager
{
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=nokstorage;AccountKey=xWzIHHH2J4jdbtJd9FfwatBsLhR4Zgx8/iWiiVGJ9pEGjLpkNxvb9Iqhld0WhaBN2Hat8sOcDOKnpc1bh74Zqw==;EndpointSuffix=core.windows.net";


    private static CloudBlobContainer getContainer(String containerName) throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference(containerName.toLowerCase());

        return container;
    }

    public static void UploadImage(InputStream image, int imageLength, String identifier, String containerName, CallBack<String> callback)
    {

        new Thread(() -> {
            try {
                CloudBlobContainer container = getContainer(containerName);
                container.createIfNotExists();
                String imageName = randomString(10) + identifier;
                CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
                imageBlob.upload(image, imageLength);
                callback.execute(imageName);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }).start();
    }


    public static void GetFile(String name, OutputStream imageStream,String containerName, CallBack<Boolean> resp)
    {
        new Thread(()-> {
            try {
                CloudBlobContainer container = getContainer(containerName);

                CloudBlockBlob blob = container.getBlockBlobReference(name);

                if (blob.exists()) {
                    blob.downloadAttributes();

                    //long imageLength = blob.getProperties().getLength();

                    blob.download(imageStream);
                    resp.execute(true);
                    return;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            resp.execute(false);
        }).start();
    }


    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }

}
