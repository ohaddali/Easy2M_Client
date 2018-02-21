package nok.easy2m;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.InputStream;
import java.security.SecureRandom;

/**
 * Created by pc on 2/21/2018.
 */

public class AzureBlobsManager
{
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=[ACCOUNT_NAME];"
            + "AccountKey=[ACCOUNT_KEY]";


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

    public static String UploadImage(InputStream image, int imageLength, String identifier, String containerName) throws Exception
    {
        CloudBlobContainer container = getContainer(containerName);

        container.createIfNotExists();

        String imageName = randomString(10) + identifier;

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageBlob.getUri().toString();
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
