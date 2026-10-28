
public class BrowserNavigationSystem {

    // Maximum number of websites each stack can store
    private String[] items;
    private int top;
    private int capacity;

    // Stack constructor
    public BrowserNavigationSystem(int stackSize, int queueSize) {
        backStack = new WebsiteStack(stackSize);
        forwardStack = new WebsiteStack(stackSize);
        recentVisits = new RecentVisitsQueue(queueSize);
        currentWebsite = null;
    }

    private WebsiteStack backStack;
    private WebsiteStack forwardStack;
    private RecentVisitsQueue recentVisits;
    private String currentWebsite;

    // Stack implementation using an array
    static class WebsiteStack {
        private String[] items;
        private int top;
        private int capacity;

        public WebsiteStack(int size) {
            capacity = size;
            items = new String[capacity];
            top = -1;
        }

        // Add a website to the top of the stack
        public boolean push(String url) {
            if (isFull()) {
                return false;
            }

            top++;
            items[top] = url;
            return true;
        }

        // Remove and return the top website
        public String pop() {
            if (isEmpty()) {
                return null;
            }

            String url = items[top];
            items[top] = null;
            top--;
            return url;
        }

        public boolean isEmpty() {
            return top == -1;
        }

        public boolean isFull() {
            return top == capacity - 1;
        }

        // Remove all websites from the stack
        public void clear() {
            while (!isEmpty()) {
                pop();
            }
        }

        // Display websites from top to bottom
        public void display() {
            if (isEmpty()) {
                System.out.println("Empty");
                return;
            }

            for (int i = top; i >= 0; i--) {
                System.out.println(items[i]);
            }
        }
    }

    // Circular queue implementation using an array
    static class RecentVisitsQueue {
        private String[] items;
        private int front;
        private int rear;
        private int size;
        private int capacity;

        public RecentVisitsQueue(int capacity) {
            this.capacity = capacity;
            items = new String[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isFull() {
            return size == capacity;
        }

        // Remove the oldest website from the queue
        public String dequeue() {
            if (isEmpty()) {
                return null;
            }

            String url = items[front];
            items[front] = null;

            // Move front circularly
            front = (front + 1) % capacity;
            size--;

            return url;
        }

        // Add a website to the recent visits queue
        public void recordVisit(String url) {
            // Do not add the same URL consecutively
            if (size > 0 && items[rear].equals(url)) {
                return;
            }

            // Remove the oldest website if the queue is full
            if (isFull()) {
                dequeue();
            }

            // Move rear circularly
            rear = (rear + 1) % capacity;
            items[rear] = url;
            size++;
        }

        // Display visits from oldest to newest
        public void displayRecentVisits() {
            if (isEmpty()) {
                System.out.println("No recent visits.");
                return;
            }

            for (int i = 0; i < size; i++) {
                int index = (front + i) % capacity;
                System.out.println(items[index]);
            }
        }
    }

    // Visit a new website
    public void visitWebsite(String url) {
        if (url == null || url.trim().isEmpty()) {
            System.out.println("Invalid website URL.");
            return;
        }

        // Open the first website
        if (currentWebsite == null) {
            currentWebsite = url;
            recentVisits.recordVisit(url);
            System.out.println("Opened: " + url);
            return;
        }

        // Avoid visiting the website already open
        if (currentWebsite.equals(url)) {
            System.out.println("This website is already open.");
            return;
        }

        // Check capacity before changing navigation history
        if (backStack.isFull()) {
            System.out.println("Cannot visit website. Back stack is full.");
            return;
        }

        // Save the current website before opening the new one
        backStack.push(currentWebsite);
        currentWebsite = url;

        // A new visit clears the forward history
        forwardStack.clear();

        // Only new website visits enter the recent visits queue
        recentVisits.recordVisit(url);

        System.out.println("Opened: " + url);
    }

    // Go to the previous website
    public void goBack() {
        if (currentWebsite == null || backStack.isEmpty()) {
            System.out.println("Cannot go back. No previous website.");
            return;
        }

        // Make sure the current website can be saved
        if (forwardStack.isFull()) {
            System.out.println("Cannot go back. Forward stack is full.");
            return;
        }

        // Move the current website to the forward stack
        forwardStack.push(currentWebsite);

        // Restore the previous website
        currentWebsite = backStack.pop();

        System.out.println("Moved back to: " + currentWebsite);
    }

    // Go to the next website
    public void goForward() {
        if (forwardStack.isEmpty()) {
            System.out.println("Cannot go forward. No next website.");
            return;
        }

        // Make sure the current website can be saved
        if (backStack.isFull()) {
            System.out.println("Cannot go forward. Back stack is full.");
            return;
        }

        // Move the current website to the back stack
        backStack.push(currentWebsite);

        // Restore the next website
        currentWebsite = forwardStack.pop();

        System.out.println("Moved forward to: " + currentWebsite);
    }

    // Display recent visits without changing the queue
    public void displayRecentVisits() {
        System.out.println("\n--- Recent Visits (Oldest to Newest) ---");
        recentVisits.displayRecentVisits();
    }

    // Display the current website and both navigation stacks
    public void displayNavigationHistory() {
        System.out.println("\n--- Navigation History ---");

        if (currentWebsite == null) {
            System.out.println("Current Website: None");
        } else {
            System.out.println("Current Website: " + currentWebsite);
        }

        System.out.println("\nBack Stack (Top to Bottom):");
        backStack.display();

        System.out.println("\nForward Stack (Top to Bottom):");
        forwardStack.display();
    }

    // Main method to test normal operations and edge cases
    public static void main(String[] args) {

        BrowserNavigationSystem browser =
                new BrowserNavigationSystem(5, 3);

        System.out.println("=== Browser Navigation System ===");

        // Visit websites to fill the recent visits queue
        browser.visitWebsite("Google");
        browser.visitWebsite("YouTube");
        browser.visitWebsite("Wikipedia");
        browser.visitWebsite("GitHub");

        // The queue is full, so Google is removed
        browser.displayRecentVisits();

        // Test back and forward navigation
        System.out.println("\n--- Back and Forward ---");
        browser.goBack();
        browser.goBack();
        browser.goForward();

        // Visiting a new website clears forward history
        browser.visitWebsite("OpenAI");

        browser.displayNavigationHistory();
        browser.displayRecentVisits();

        // Test an empty back stack
        System.out.println("\n--- Empty Stack Test ---");
        BrowserNavigationSystem emptyBrowser =
                new BrowserNavigationSystem(2, 3);

        emptyBrowser.goBack();
        emptyBrowser.goForward();
        emptyBrowser.displayRecentVisits();

        // Test a full back stack
        System.out.println("\n--- Full Stack Test ---");
        BrowserNavigationSystem smallBrowser =
                new BrowserNavigationSystem(2, 3);

        smallBrowser.visitWebsite("Site A");
        smallBrowser.visitWebsite("Site B");
        smallBrowser.visitWebsite("Site C");

        // This visit will be rejected because the back stack is full
        smallBrowser.visitWebsite("Site D");

        smallBrowser.displayNavigationHistory();
    }
}
