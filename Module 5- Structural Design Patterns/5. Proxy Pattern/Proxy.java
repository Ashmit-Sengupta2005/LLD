/*
 * PROXY PATTERN
 *
 * A proxy is a STAND-IN for the real object. It implements the SAME interface,
 * so the client can't tell the difference - but it gets to run code BEFORE
 * (and after) deciding whether to call the real object at all.
 *
 * Here: downloading a video is slow and expensive. If 5 users request the same
 * video, we download it 5 times. Wasteful.
 *
 * CachedVideoDownloader sits in front of RealVideoDownloader and answers from
 * a cache when it can, so the real download happens only ONCE per URL.
 *
 * Note the client never knows a proxy exists - it just holds a VideoDownloader.
 */

import java.util.HashMap;
import java.util.Map;

// ========== SUBJECT ==========
// The interface. BOTH the real object and the proxy implement this.
// This is what makes the swap invisible to the client.
interface VideoDownloader {
    String downloadVideo(String videoUrl);
}

// ========== REAL SUBJECT ==========
// The actual expensive worker. Note: it knows nothing about caching,
// permissions or logging. It does ONE job - download.
class RealVideoDownloader implements VideoDownloader {

    public RealVideoDownloader() {
        // Pretend this is expensive: open connections, allocate buffers, etc.
        System.out.println("   [RealVideoDownloader created - expensive setup done]");
    }

    @Override
    public String downloadVideo(String videoUrl) {
        System.out.println("   Downloading from network: " + videoUrl);
        simulateSlowNetwork();
        return "Video content from " + videoUrl;
    }

    private void simulateSlowNetwork() {
        try {
            Thread.sleep(300); // pretend the network is slow
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ========== PROXY ==========
// Same interface as RealVideoDownloader, so it can stand in for it.
class CachedVideoDownloader implements VideoDownloader {

    // Not created yet! Built on first real use (this is the VIRTUAL proxy idea).
    private RealVideoDownloader realDownloader;

    // url -> already-downloaded content (this is the CACHE / smart proxy idea)
    private Map<String, String> cache = new HashMap<>();

    @Override
    public String downloadVideo(String videoUrl) {

        // 1. Can we answer WITHOUT touching the real object?
        if (cache.containsKey(videoUrl)) {
            System.out.println("   CACHE HIT - no download needed");
            return cache.get(videoUrl); // real object never called
        }

        // 2. Cache miss. Create the real object only now, and only once.
        System.out.println("   CACHE MISS");
        if (realDownloader == null) {
            realDownloader = new RealVideoDownloader(); // lazy initialization
        }

        // 3. Delegate the real work
        String video = realDownloader.downloadVideo(videoUrl);

        // 4. Remember it for next time
        cache.put(videoUrl, video);
        return video;
    }
}

// ========== CLIENT ==========

public class Proxy {
    public static void main(String[] args) {
        String url = "https://video.com/proxy-pattern";

        // ----- WITHOUT proxy: every request hits the network -----
        System.out.println("=== WITHOUT Proxy ===");
        VideoDownloader plain = new RealVideoDownloader();
        long start = System.currentTimeMillis();
        System.out.println("User 1:");
        plain.downloadVideo(url);
        System.out.println("User 2 (same video):");
        plain.downloadVideo(url); // downloads AGAIN - wasteful
        System.out.println("Time taken: " + (System.currentTimeMillis() - start) + "ms\n");

        // ----- WITH proxy: second request is free -----
        // Client declares the INTERFACE type, so swapping in the proxy
        // requires zero other changes.
        System.out.println("=== WITH Proxy ===");
        VideoDownloader downloader = new CachedVideoDownloader();
        start = System.currentTimeMillis();
        System.out.println("User 1:");
        downloader.downloadVideo(url);
        System.out.println("User 2 (same video):");
        downloader.downloadVideo(url); // served from cache - instant
        System.out.println("Time taken: " + (System.currentTimeMillis() - start) + "ms");
    }
}
