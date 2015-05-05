package Spout;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.util.UUID;


public class S3 {

    public AWSCredentials credentials;
    public AmazonS3 s3;
    public String bucketName;

    public S3() {

        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        s3 = new AmazonS3Client(credentials);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest2);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");
    }

    public S3(String bucketName){
        this();
        setBucketName(bucketName);
    }

    public void setBucketName(String bucketName){
        this.bucketName = bucketName;
    }

    public S3Object get(String key){
        return s3.getObject(new GetObjectRequest(bucketName, key));
    }

    public void delete(String key){
        s3.deleteObject(bucketName, key);
    }

    public static void main(){

        String bucketName = "my-first-s3-bucket-" + UUID.randomUUID();

    }
}
