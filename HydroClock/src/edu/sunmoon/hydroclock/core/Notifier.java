package edu.sunmoon.hydroclock.core;

public interface Notifier {
    void notify(String title, String message);
    void setBeep(boolean enabled);
}
