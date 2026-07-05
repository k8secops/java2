package com.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class App {

    static String NAME;
    static String PASSWORD;

    static void loadConfig() throws IOException {
        Properties p = new Properties();
        try (InputStream in = App.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                p.load(in);
            }
        }
        NAME = resolve(p.getProperty("name"));
        PASSWORD = resolve(p.getProperty("password"));
    }

    // Maven leaves an unresolved ${env.X} placeholder as-is when the secret
    // wasn't set at build time (e.g. a local build outside the pipeline).
    private static String resolve(String value) {
        if (value == null || value.isBlank() || value.startsWith("${")) {
            return "(not set)";
        }
        return value;
    }

    static String renderPage() {
        return "My name is " + NAME + " and my password is " + PASSWORD;
    }

    public static void main(String[] args) throws IOException {
        loadConfig();
        int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handle);
        server.setExecutor(null);
        server.start();
        System.out.println("Listening on port " + port);
    }

    static void handle(HttpExchange exchange) throws IOException {
        byte[] bytes = renderPage().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
