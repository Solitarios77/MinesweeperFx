package com.solitarios.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class GameTimerModel {
    private IntegerProperty gameTime = new SimpleIntegerProperty(0);
    private int tempTime = -1;
    private class TimerScheduledService extends ScheduledService<Integer> {
        private int time = 0;

        public void setTime(int time) {
            this.time = time;
        }

        @Override
        public void reset() {
            super.reset();
            time = 0;
        }

        @Override
        protected Task<Integer> createTask() {

            return new Task<>() {

                @Override
                protected void updateValue(java.lang.Integer value) {
                    super.updateValue(value);
                    gameTime.set(value);
                }

                @Override
                protected java.lang.Integer call() throws Exception {
                    return (time++);
                }
            };
        }
    }

    private TimerScheduledService service = new TimerScheduledService();
    public GameTimerModel() {
        service.setPeriod(Duration.seconds(1));
    }

    public void start() {
        service.start();
    }

    public void pause() {
        service.cancel();
        tempTime = service.getLastValue();
    }

    public void restart() {
        service.reset();
        if (tempTime != -1) {
            service.setTime(tempTime);
            tempTime = -1;
        }
        service.start();
    }

    public IntegerProperty gameTimeProperty() {
        return gameTime;
    }

    public void resetTime() {
        service.cancel();
        service.reset();
        gameTime.set(0);
    }
}
