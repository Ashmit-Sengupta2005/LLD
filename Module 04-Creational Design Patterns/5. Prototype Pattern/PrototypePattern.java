import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ========== Mutable Style Config (demonstrates deep vs shallow copy) ==========
class EmailStyle {
    private String font;
    private int fontSize;

    public EmailStyle(String font, int fontSize) {
        this.font = font;
        this.fontSize = fontSize;
    }

    // Copy constructor for deep cloning
    public EmailStyle(EmailStyle other) {
        this.font = other.font;
        this.fontSize = other.fontSize;
    }

    public String getFont() {
        return font;
    }

    public int getFontSize() {
        return fontSize;
    }

    @Override
    public String toString() {
        return "font=" + font + ", size=" + fontSize;
    }
}

// ========== Prototype Interface ==========
interface EmailTemplate {
    EmailTemplate clone();

    void setContent(String content);

    void send(String to);
}

// ========== Concrete Prototype ==========
class WelcomeEmail implements EmailTemplate {
    private String subject;
    private String content;
    private List<String> templateTokens;
    private EmailStyle style;

    public WelcomeEmail() {
        // Fixed template subject
        this.subject = "Welcome to TUF+";
        // Default content
        this.content = "Hi there! Thanks for joining us.";
        // Simulate heavy initialization work (template parsing)
        this.templateTokens = Arrays.asList("{name}", "{plan}", "{cta_link}");
        // Shared configuration object
        this.style = new EmailStyle("Inter", 14);
        System.out.println("[WelcomeEmail] Heavy initialization done (template parsed).");
    }

    // Private copy constructor — used by clone()
    private WelcomeEmail(WelcomeEmail other) {
        // Copy trivial fields
        this.subject = other.subject;
        this.content = other.content;
        // List.copyOf creates an immutable deep copy of the list
        this.templateTokens = List.copyOf(other.templateTokens);
        // Deep copy the style to avoid shared mutation issues
        this.style = new EmailStyle(other.style);
        System.out.println("[WelcomeEmail] Cloned (no heavy init).");
    }

    @Override
    public EmailTemplate clone() {
        return new WelcomeEmail(this);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void send(String to) {
        System.out.println("Sending to " + to + ": [" + subject + "] " + content);
        System.out.println("  Style: " + style);
    }
}

// ========== Prototype Registry ==========
class EmailTemplateRegistry {
    private Map<String, EmailTemplate> templates = new HashMap<>();

    public void registerTemplate(String type, EmailTemplate prototype) {
        templates.put(type, prototype);
    }

    public EmailTemplate getTemplate(String type) {
        EmailTemplate prototype = templates.get(type);
        if (prototype == null) {
            throw new IllegalArgumentException("No template registered for type: " + type);
        }
        // Always return a clone so the prototype stays unchanged
        return prototype.clone();
    }
}

// ========== Main Method ==========
class Main {
    public static void main(String[] args) {
        // Register the prototype once (heavy init happens here)
        EmailTemplateRegistry registry = new EmailTemplateRegistry();
        registry.registerTemplate("welcome", new WelcomeEmail());

        System.out.println("---");

        // Clone and customize per user (no heavy init)
        EmailTemplate email1 = registry.getTemplate("welcome");
        email1.send("alice@example.com");

        System.out.println("---");

        EmailTemplate email2 = registry.getTemplate("welcome");
        email2.setContent("Hi Bob, welcome to TUF Premium!");
        email2.send("bob@example.com");

        System.out.println("---");

        EmailTemplate email3 = registry.getTemplate("welcome");
        email3.setContent("Thanks for signing up. Let's get started!");
        email3.send("charlie@example.com");
    }
}
