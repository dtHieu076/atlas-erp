package com.atlas_erp.ai_service.Config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

@Configuration
public class NettyDnsConfig {

    @Bean
    public WebClientCustomizer webClientCustomizer() {
        return webClientBuilder -> {
            // "Tịch thu ván trượt", ép Netty dùng bộ phân giải DNS mặc định, an toàn của
            // Java
            HttpClient httpClient = HttpClient.create()
                    .resolver(DefaultAddressResolverGroup.INSTANCE);

            // Cài đặt anh bảo vệ mới này vào WebClient
            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        };
    }
}