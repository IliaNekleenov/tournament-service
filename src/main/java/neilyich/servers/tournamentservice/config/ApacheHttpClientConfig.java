package neilyich.servers.tournamentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Profile("apache")
@Slf4j
@Configuration
public class ApacheHttpClientConfig {

    @Bean
    @Profile("apache")
    SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return new SSLContextBuilder()
                .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                .build();
    }

    @Bean
    @Profile("apache")
    CloseableHttpClient httpClient(SSLContext sslContext) {
        log.info("using apache client");
        return HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .setConnectionManager(
                        new BasicHttpClientConnectionManager(
                                RegistryBuilder.<ConnectionSocketFactory>create()
                                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                                        .register("https", new SSLConnectionSocketFactory(sslContext,
                                                NoopHostnameVerifier.INSTANCE))
                                        .build()
                        ))
                .build();
    }


}
