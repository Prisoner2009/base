package filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * 全局拦截器，作用所有的微服务
 */
@Slf4j
@Component
public class RequestGlobalFilter implements GlobalFilter, Ordered {

    public static final String X_ACCESS_TOKEN = "X-Access-Token";
    public static final String X_GATEWAY_BASE_PATH = "X_GATEWAY_BASE_PATH";
    private static final long STRIP_PREFIX_COUNT = 1L;
    private static final String REQUEST_START_TIME = "REQUEST_START_TIME";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 记录请求开始时间
        exchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());

        // 2. 组装 basePath
        String scheme = exchange.getRequest().getURI().getScheme();
        String host = exchange.getRequest().getURI().getHost();
        int port = exchange.getRequest().getURI().getPort();
        String basePath = scheme + "://" + host + (port == -1 ? "" : ":" + port);

        // 3. 重写 StripPrefix
        addOriginalRequestUrl(exchange, exchange.getRequest().getURI());
        String rawPath = exchange.getRequest().getURI().getRawPath();
        String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/"))
                .skip(STRIP_PREFIX_COUNT)
                .collect(Collectors.joining("/"));
        ServerHttpRequest newRequest = exchange.getRequest().mutate().path(newPath).build();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());

//        // 4. 解析用户名
//        String username = parseUsernameFromToken(newRequest);

        // 5. 注入自定义请求头
        ServerHttpRequest.Builder builder = newRequest.mutate()
                .header(X_GATEWAY_BASE_PATH, basePath)
                .header("Authorization-UserName", "");

        ServerWebExchange mutableExchange = exchange.mutate().request(builder.build()).build();

        // 6. 执行过滤并在响应完成后打印日志
        return chain.filter(mutableExchange)
                .doOnSuccess(done -> logRequest(mutableExchange))
                .doOnError(error -> logRequest(mutableExchange));
    }

//    /**
//     * 从 JWT Token 中解析用户名
//     */
//    private String parseUsernameFromToken(ServerHttpRequest request) {
//        String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
//        if (StringUtils.isBlank(token)) {
//            return "";
//        }
//
//        try {
//            // ⚠️ 这里用 jjwt 库解析（spring-cloud-gateway 常用）
//            Claims claims = Jwts.parserBuilder()
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            // 优先取 username，没有就取 sub
//            String username = claims.get("username", String.class);
//            if (StringUtils.isBlank(username)) {
//                username = claims.getSubject();
//            }
//            return username != null ? username : "";
//        } catch (Exception e) {
//            log.warn("JWT 解析失败: {}", e.getMessage());
//            return "";
//        }
//    }

    /**
     * 打印请求日志
     */
    private void logRequest(ServerWebExchange exchange) {
        Long startTime = exchange.getAttribute(REQUEST_START_TIME);
        long duration = (startTime == null ? 0 : System.currentTimeMillis() - startTime);

        ServerHttpRequest request = exchange.getRequest();
        HttpStatus status = (exchange.getResponse() != null ? (HttpStatus) exchange.getResponse().getStatusCode() : null);

        log.info("请求日志 => {} {} | 状态码={} | 耗时={}ms | 用户={}",
                request.getMethod(),
                request.getURI(),
                status != null ? status.value() : "N/A",
                duration,
                request.getHeaders().getFirst("Authorization-UserName"));
    }

    @Override
    public int getOrder() {
        return 0; // 前置执行
    }
}
