package simple.planner.realmObjects;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TODOListItem extends RealmObject
{
    @PrimaryKey
    private String id;
    @Required
    private String title;
    private String content;
    private long created;
    private long updated;
    private boolean done;
    private TODOAlarm alarm;

    public TODOListItem() {

        long currentTime    = System.currentTimeMillis();

        this.id             = UUID.randomUUID().toString();
        this.created        = currentTime;
        this.updated        = currentTime;
        this.done           = false;
        this.alarm          = null;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public TODOAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(TODOAlarm alarm) {
        this.alarm = alarm;
    }
}
