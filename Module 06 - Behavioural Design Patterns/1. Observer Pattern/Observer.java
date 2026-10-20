/*
 * OBSERVER PATTERN
 *
 * One object changes -> many objects need to react.
 *
 * Here: a YouTube channel uploads a video, and every subscriber must be told.
 *
 * The naive way is for the channel to notify everyone itself:
 *     sendEmail("rahul@example.com");
 *     sendAppNotification("raj");
 * ...which hardcodes WHO is notified and HOW, inside the upload logic.
 *
 * Observer flips it: the channel keeps a List<Subscriber> and just calls
 * update() on each. It never knows whether a subscriber is an email, an app,
 * or an SMS - only that it can be update()d.
 *
 * Result: adding a new notification type = write one new class.
 *         The channel is never touched again.
 */

import java.util.ArrayList;
import java.util.List;

// ========== OBSERVER ==========
// What every subscriber must be able to do: react to an update.
interface Subscriber {
    void update(String videoTitle);
}

// ========== CONCRETE OBSERVERS ==========
// Each one decides for ITSELF what "being notified" means.

class EmailSubscriber implements Subscriber {
    private String email;

    public EmailSubscriber(String email) {
        this.email = email;
    }

    @Override
    public void update(String videoTitle) {
        System.out.println("   Email to " + email + ": New video - " + videoTitle);
    }
}

class MobileAppSubscriber implements Subscriber {
    private String username;

    public MobileAppSubscriber(String username) {
        this.username = username;
    }

    @Override
    public void update(String videoTitle) {
        System.out.println("   App alert for " + username + ": New video - " + videoTitle);
    }
}

// A third type added later - note that NOTHING else in this file changes.
class SmsSubscriber implements Subscriber {
    private String phone;

    public SmsSubscriber(String phone) {
        this.phone = phone;
    }

    @Override
    public void update(String videoTitle) {
        System.out.println("   SMS to " + phone + ": New video - " + videoTitle);
    }
}

// ========== SUBJECT ==========
// What anything observable must support: add, remove, notify.
interface Channel {
    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void notifySubscribers(String videoTitle);
}

// ========== CONCRETE SUBJECT ==========
class YouTubeChannel implements Channel {
    private String channelName;

    // The channel holds the INTERFACE type, so it has no idea
    // what kind of subscribers these actually are.
    private List<Subscriber> subscribers = new ArrayList<>();

    public YouTubeChannel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(String videoTitle) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(videoTitle); // each one reacts in its own way
        }
    }

    // The business action. Upload, then broadcast.
    public void uploadVideo(String videoTitle) {
        System.out.println(channelName + " uploaded: " + videoTitle);
        notifySubscribers(videoTitle);
    }
}

// ========== CLIENT ==========

public class Observer {
    public static void main(String[] args) {
        YouTubeChannel tuf = new YouTubeChannel("takeUforward");

        Subscriber raj = new MobileAppSubscriber("raj");
        Subscriber rahul = new EmailSubscriber("rahul@example.com");
        Subscriber striver = new SmsSubscriber("+91-99999-00000");

        tuf.subscribe(raj);
        tuf.subscribe(rahul);
        tuf.subscribe(striver);

        tuf.uploadVideo("Observer Pattern");

        // Subscriptions change AT RUNTIME - this is the pattern's main strength.
        System.out.println("\n[raj unsubscribes]\n");
        tuf.unsubscribe(raj);

        tuf.uploadVideo("Strategy Pattern"); // raj is no longer notified
    }
}
