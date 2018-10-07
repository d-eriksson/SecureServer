// A client-side class that uses a secure TCP/IP socket

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "LIUkeystore.ks";
	static final String TRUSTSTORE = "LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
	static final String DOWNLOAD_COMPLETE_COMMAND = "cmd:download_complete";
	BufferedReader socketIn;
	PrintWriter socketOut;


	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
		this.init();
	}

  // The method used to start a client object
	public void init() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );

			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );

			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );

			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );

			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			SSLSocketFactory sslFact = sslContext.getSocketFactory();
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port);
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");



			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			socketOut = new PrintWriter( client.getOutputStream(), true );

			//this.upload("C:\\Users\\odavi\\Documents\\KURSER.txt", "KURSER.txt");
			//this.download("KURSER.txt");
			//this.delete("KURSER.txt");
			//socketOut.println("cmd:exit");
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	public void upload(String filepath){
		try{
			socketOut.println ( "cmd:upload" );
			File file = new File(filepath);
			socketOut.println(file.getName());
		  BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null)
			{
				socketOut.println(st);
			}
			socketOut.println ( "cmd:upload_complete" );
		}
		catch (FileNotFoundException e){
    	System.out.println(e);
		}
		catch (IOException e){
    	System.out.println(e);
		}
	}
	public void download(String file){
		try{
			socketOut.println("cmd:download");
			socketOut.println(file);
			String str;
			PrintWriter writer = new PrintWriter("downloads/" + file , "UTF-8");
			while(!(str = socketIn.readLine()).equals(DOWNLOAD_COMPLETE_COMMAND)){
				writer.println(str);
			}
			writer.close();
		}
		catch(IOException e){
	    	System.out.println(e);
		}
	}
	public void	delete(String file){
		try{
			socketOut.println("cmd:delete");
			socketOut.println(file);
			System.out.println(">>>> " + socketIn.readLine());
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	public void terminate(){
		socketOut.println("cmd:exit");
	}
}
