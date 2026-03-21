package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Configuration
public class GigaChatConfig {

    @Value("${gigachat.api-url}")
    private String apiUrl;

    @Bean(name = "gigaChatWebClient")
    public WebClient gigaChatWebClient() throws Exception {
        // GigaChat использует self-signed сертификат — отключаем проверку для dev
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, new java.security.SecureRandom());

        io.netty.handler.ssl.SslContext nettySSL = io.netty.handler.ssl.SslContextBuilder
                .forClient()
                .trustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE)
                .build();

        var httpClient = reactor.netty.http.client.HttpClient.create()
                .secure(spec -> spec.sslContext(nettySSL));

        return WebClient.builder()
                .baseUrl(apiUrl)
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .build();
    }
}
