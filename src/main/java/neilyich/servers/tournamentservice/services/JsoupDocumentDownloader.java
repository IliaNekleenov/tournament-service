package neilyich.servers.tournamentservice.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("jsoup")
@Service
public class JsoupDocumentDownloader implements DocumentDownloader {
    @Override
    public Document download(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
