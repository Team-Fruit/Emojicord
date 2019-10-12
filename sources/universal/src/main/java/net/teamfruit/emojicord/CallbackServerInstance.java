package net.teamfruit.emojicord;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.http.util.EntityUtils;

import net.teamfruit.emojicord.util.DataUtils;

public class CallbackServerInstance {
	public static class WebCallbackModel {
		public String key;
		public String token;
	}

	public static void main(final String[] args) throws Exception {
		// Set up the HTTP protocol processor
		final HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate())
				.add(new ResponseServer("Emojicord/1.1"))
				.add(new ResponseContent())
				.add(new ResponseConnControl())
				.build();

		// Set up request handlers
		final UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		reqistry.register("*", new HttpCallbackHandler());

		// Set up the HTTP service
		final HttpService httpService = new HttpService(httpproc, reqistry);

		final Thread t = new RequestListenerThread(httpService, null);
		t.setDaemon(false);
		t.start();
	}

	private static class HttpCallbackHandler implements HttpRequestHandler {
		@Override
		public void handle(
				final HttpRequest request, final HttpResponse response,
				final HttpContext context
		) throws HttpException, IOException {
			response.addHeader(new BasicHeader("Access-Control-Allow-Origin", "https://emojicord.teamfruit.net"));
			response.addHeader(new BasicHeader("Access-Control-Allow-Methods", "POST, OPTIONS"));
			response.addHeader(new BasicHeader("Access-Control-Allow-Headers", "Content-Type"));
			response.addHeader(new BasicHeader("Access-Control-Max-Age", "86400"));
			response.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
			response.addHeader(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8"));

			final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			if (method.equals("OPTIONS")) {
				response.setStatusCode(HttpStatus.SC_NO_CONTENT);
				Log.log.info("OPTIONS");
				return;
			}

			final String target = request.getRequestLine().getUri();
			Log.log.info(String.format("URL: %s, Method: %s", target, method));

			if (!target.equals("/")) {
				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
				response.setEntity(new StringEntity("NG\nNot Found", ContentType.create("text/plain", StandardCharsets.UTF_8)));
				Log.log.warn("Invalid Request: Not Found");
				return;
			}

			if (!method.equals("POST")) {
				response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
				response.setEntity(new StringEntity("NG\nMethod Not Allowed", ContentType.create("text/plain", StandardCharsets.UTF_8)));
				Log.log.warn("Invalid Request: Method Not Allowed");
				return;
			}

			if (!(request instanceof HttpEntityEnclosingRequest)) {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				response.setEntity(new StringEntity("NG\nNo Request Data", ContentType.create("text/plain", StandardCharsets.UTF_8)));
				Log.log.warn("Invalid Request: No Request Data");
				return;
			}

			final HttpEntityEnclosingRequest eRequest = (HttpEntityEnclosingRequest) request;
			final HttpEntity entity = eRequest.getEntity();
			//if (eRequest.expectContinue()) {}
			final byte[] data = EntityUtils.toByteArray(entity);
			Log.log.info(StringUtils.toEncodedString(data, StandardCharsets.UTF_8));
			final WebCallbackModel callback = DataUtils.loadStream(
					new ByteArrayInputStream(data),
					WebCallbackModel.class, "Parsing Web Callback");

			if (callback==null||StringUtils.isEmpty(callback.key)||StringUtils.isEmpty(callback.token)) {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				response.setEntity(new StringEntity("NG\nInvalid Json", ContentType.create("text/plain", StandardCharsets.UTF_8)));
				Log.log.warn("Invalid Request: Invalid Json");
				return;
			}

			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(new StringEntity("OK", ContentType.create("text/plain", StandardCharsets.UTF_8)));
			Log.log.info(String.format("key: %s, token: %s", callback.key, callback.token));
		}
	}

	private static class RequestListenerThread extends Thread {
		private final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;
		private final ServerSocket serversocket;
		private final HttpService httpService;

		public RequestListenerThread(
				final HttpService httpService,
				final SSLServerSocketFactory sf
		) throws IOException {
			this.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
			this.serversocket = sf!=null
					? sf.createServerSocket(0, 0, InetAddress.getByName(null))
					: new ServerSocket(0, 0, InetAddress.getByName(null));
			this.httpService = httpService;
		}

		@Override
		public void run() {
			Log.log.info("Emojicord Web Listener on port "+this.serversocket.getLocalPort());
			while (!Thread.interrupted())
				try {
					// Set up HTTP connection
					final Socket socket = this.serversocket.accept();
					final HttpServerConnection conn = this.connFactory.createConnection(socket);

					// Start worker thread
					final Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
					Log.log.info("Incoming connection from "+socket.getInetAddress()+", thread "+t.getName());
				} catch (final InterruptedIOException ex) {
					break;
				} catch (final IOException e) {
					Log.log.error("IO error initialising connection thread: ", e);
					break;
				}
		}
	}

	private static class WorkerThread extends Thread {
		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			final HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted()&&this.conn.isOpen())
					this.httpservice.handleRequest(this.conn, context);
			} catch (final ConnectionClosedException ex) {
				Log.log.error("Client closed connection");
			} catch (final IOException ex) {
				Log.log.error("IO error: "+ex.getMessage());
			} catch (final HttpException ex) {
				Log.log.error("Unrecoverable HTTP protocol violation: "+ex.getMessage());
			} finally {
				try {
					this.conn.shutdown();
				} catch (final IOException ignore) {
				}
			}
		}

	}
}