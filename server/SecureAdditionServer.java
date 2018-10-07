
// An example class that uses the secure server socket class

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;


public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "LIUkeystore.ks";
	static final String TRUSTSTORE = "LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
	static final String UPLOAD_COMMAND = "cmd:upload";
	static final String UPLOAD_COMPLETE_COMMAND = "cmd:upload_complete";
	static final String DOWNLOAD_COMMAND = "cmd:download";
	static final String DELETE_COMMAND = "cmd:delete";
	static final String TERMINATE_CONNECTION = "cmd:exit";
	BufferedReader in;
	PrintWriter out;

	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}

	/** The method that does the work for the class */
	public void run() {
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
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );

			System.out.println("\n>>>> SecureAdditionServer: ACTIVE ");
			SSLSocket incoming = (SSLSocket)sss.accept();

      in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			out = new PrintWriter( incoming.getOutputStream(), true );

			String str;
			while ( !(str = in.readLine()).equals(TERMINATE_CONNECTION) ) {
				switch (str) {
						case UPLOAD_COMMAND:
								System.out.println("\n>>>> SecureAdditionServer: UPLOAD_COMMAND ");
								handleUpload();
								break;
						case DOWNLOAD_COMMAND:
								System.out.println("\n>>>> SecureAdditionServer: DOWNLOAD_COMMAND ");
								handleDownload();
								break;
						case DELETE_COMMAND:
								handleDelete();
								System.out.println("\n>>>> SecureAdditionServer: DELETE_COMMAND ");
								break;
						default:
								break;
				}
			}
			System.out.println("\n>>>> SecureAdditionServer: CLOSING CONNECTION ");
			incoming.close();
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
  private void handleUpload() {
			try{
				String str;
				PrintWriter writer = new PrintWriter("uploads/" + in.readLine(), "UTF-8");
				while(!(str = in.readLine()).equals(UPLOAD_COMPLETE_COMMAND)){
						writer.println(str);
				}
				writer.close();
			}
			catch (IOException e){
					System.out.println(e);
			}
	}
	private void handleDownload() {
			try{
				File file = new File("uploads/" + in.readLine());
				BufferedReader br = new BufferedReader(new FileReader(file));
				String st;
				while ((st = br.readLine()) != null)
				{
					out.println(st);
				}
				out.println ( "cmd:download_complete" );
			}
			catch (IOException e){
					System.out.println(e);
			}
	}
	private void handleDelete(){
		try{
			File file = new File("uploads/" + in.readLine());
			if(file.exists()){
				file.delete();
				out.println("File deleted");
			}
			else{
				out.println("File doesn't exist");
			}
		}
		catch(IOException e){
			System.out.println(e);
		}
	}


	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}
