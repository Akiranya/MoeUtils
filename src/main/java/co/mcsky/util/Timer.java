package co.mcsky.util;

public class Timer {
    private String title;
    private long start;
    private long end;

    public Timer(String className) {
        this.title = className;
        this.start = System.currentTimeMillis();
        System.out.println(title + ": timer start!");
    }

    public void end() {
        end = System.currentTimeMillis();
        System.out.println(title + ": elapsed " + (end - start) + " ms");
    }
}
