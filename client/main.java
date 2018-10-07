import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class main{
  public static void main(String []args) {
    SecureAdditionClient Client;
    try {
      InetAddress host = InetAddress.getLocalHost();
      int port = 8189;
      if ( args.length > 0 ) {
        port = Integer.parseInt( args[0] );
      }
      if ( args.length > 1 ) {
        host = InetAddress.getByName( args[1] );
      }
      Client = new SecureAdditionClient(host, port);
      while (true) {
              System.out.println("\n Menu");
              System.out.println("1. Upload file");
              System.out.println("2. Download file");
              System.out.println("3. Delete file");
              System.out.println("4. Exit");
              String input = "";
              try {
                  input = (new BufferedReader(new InputStreamReader(System.in))).readLine();
              } catch(IOException e) {
                  System.out.println("Something went wrong: " + e.toString());
                  break;
              }
              if (input.equals("1")){
                System.out.println("Enter filepath to the file you want to upload: ");
                String filepath;
                try {
                    filepath = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                } catch(IOException e) {
                    System.out.println("Something went wrong: " + e.toString());
                    break;
                }
                Client.upload(filepath);

              }
              else if(input.equals("2")){
                System.out.println("What file do you want to download: ");
                String filename;
                try {
                    filename = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                } catch(IOException e) {
                    System.out.println("Something went wrong: " + e.toString());
                    break;
                }
                Client.download(filename);
              }
              else if(input.equals("3")){
                System.out.println("What file do you want to delete: ");
                String filename;
                try {
                    filename = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                } catch(IOException e) {
                    System.out.println("Something went wrong: " + e.toString());
                    break;
                }
                Client.delete(filename);
              }
              else{
                Client.terminate();
                break;
              }
          }
    }
    catch ( UnknownHostException uhx ) {
      System.out.println( uhx );
      uhx.printStackTrace();
    }
  }
}
