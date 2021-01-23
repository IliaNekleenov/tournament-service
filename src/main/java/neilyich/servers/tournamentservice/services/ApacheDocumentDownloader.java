package neilyich.servers.tournamentservice.services;

import lombok.AllArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("apache")
@Service
@AllArgsConstructor
public class ApacheDocumentDownloader implements DocumentDownloader {
    private final CloseableHttpClient httpClient;

    @Override
    public Document download(String url) throws IOException {
        var request = new HttpGet(url);
        var response = httpClient.execute(request);
        var entity = response.getEntity();
        return Jsoup.parse(EntityUtils.toString(entity));
    }
}
