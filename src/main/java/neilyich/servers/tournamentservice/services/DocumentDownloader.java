package neilyich.servers.tournamentservice.services;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentDownloader {
    Document download(String url) throws IOException;
}
