package com.silverservers.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.silverservers.app.App;

import java.util.function.Consumer;

public class ServiceNotifier {
    public static final int FOREGROUND_NOTIFICATION_ID = 1;

    private final Service service;
    private final NotificationManager manager;
    private final NotificationChannel channel;

    private int currentId = FOREGROUND_NOTIFICATION_ID;
    public ServiceNotifier(Service service, String name, String description) {
        this.service = service;
        this.manager = service.getSystemService(NotificationManager.class);
        this.channel = createChannel(name, description);
    }

    /**
     * Creates an underlying notification channel to create and update notifications.
     */
    public NotificationChannel createChannel(String name, String description) {
        NotificationChannel channel = new NotificationChannel(
            App.generateId(),
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(description);
        manager.createNotificationChannel(channel);
        return channel;
    }

    /**
     * Adds a new notification to the channel.
     */
    public int addNotification(@Nullable Consumer<NotificationCompat.Builder> build) {
        int id = currentId++;
        manager.notify(
            id,
            buildNotification(build)
        );
        return id;
    }

    /**
     * Updates an existing notification in the channel with new content.
     */
    public void updateNotification(int serviceId, @Nullable Consumer<NotificationCompat.Builder> build) {
        manager.notify(
            serviceId,
            buildNotification(build)
        );
    }

    /**
     * Builds a notification using a default builder.
     *
     * The build function can be used to inject functionalities
     * into notification before building it.
     */
    public Notification buildNotification(@Nullable Consumer<NotificationCompat.Builder> build) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, channel.getId())
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(channel.getName())
            .setContentText(channel.getDescription());

        if (build != null) { build.accept(builder); }

        return builder.build();
    }
}
