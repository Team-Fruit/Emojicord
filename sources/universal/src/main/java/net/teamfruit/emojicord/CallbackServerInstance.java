package net.teamfruit.emojicord;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.teamfruit.emojicord.util.DataUtils;

public class CallbackServerInstance implements AutoCloseable {
	public static class WebCallbackModel {
		public String key;
		public String token;
	}

	private final ExecutorService listenerExecutor;
	private final ExecutorService workerExecutor;
	private ServerSocket serversocket;

	public CallbackServerInstance(final int port) throws IOException {
		this.listenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(false).setNameFormat(Reference.MODID+"-web-listener-%d").build());
		this.workerExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat(Reference.MODID+"-web-worker-%d").build());

		// Set up the HTTP protocol processor
		final HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate())
				.add(new ResponseServer(Reference.NAME+"/1.1"))
				.add(new ResponseContent())
				.add(new ResponseConnControl())
				.build();

		// Set up request handlers
		final UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		reqistry.register("*", new HttpCallbackHandler());

		// Set up the HTTP service
		final HttpService httpService = new HttpService(httpproc, reqistry);

		final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
		this.serversocket = new ServerSocket(port, 0, InetAddress.getByName(null));

		this.listenerExecutor.submit(() -> {
			Log.log.info("Emojicord Web Listener on port "+this.serversocket.getLocalPort());
			while (!Thread.interrupted())
				try {
					// Set up HTTP connection
					final Socket socket = this.serversocket.accept();
					final HttpServerConnection conn = connFactory.createConnection(socket);

					// Start worker thread
					Log.log.info("Incoming connection from "+socket.getInetAddress());
					this.workerExecutor.submit(() -> {
						final HttpContext context = new BasicHttpContext(null);
						try {
							while (!Thread.interrupted()&&conn.isOpen())
								httpService.handleRequest(conn, context);
						} catch (final ConnectionClosedException ex) {
							Log.log.info("Client closed connection");
						} catch (final IOException ex) {
							Log.log.error("IO error: "+ex.getMessage());
						} catch (final HttpException ex) {
							Log.log.error("Unrecoverable HTTP protocol violation: "+ex.getMessage());
						} finally {
							try {
								conn.shutdown();
							} catch (final IOException ignore) {
							}
						}
					});
				} catch (final InterruptedIOException ex) {
					break;
				} catch (final IOException e) {
					Log.log.error("IO error initialising connection thread: ", e);
					break;
				}
		});
	}

	public CallbackServerInstance() throws IOException {
		this(0);
	}

	public int getPort() {
		return this.serversocket.getLocalPort();
	}

	@Override
	public void close() {
		this.listenerExecutor.shutdownNow();
		this.workerExecutor.shutdownNow();
		IOUtils.closeQuietly(this.serversocket);
	}

	@SuppressWarnings("resource")
	public static void main(final String[] args) throws IOException {
		new CallbackServerInstance();
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
			//response.addHeader(new BasicHeader(HttpHeaders.CONNECTION, "close"));

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
}