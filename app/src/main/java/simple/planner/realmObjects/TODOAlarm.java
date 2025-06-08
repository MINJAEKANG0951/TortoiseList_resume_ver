package simple.planner.realmObjects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TODOAlarm extends RealmObject
{
    @PrimaryKey
    private int id;

    private long scheduled;
    private boolean rang;

    public static int getNextId() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Number currentMaxId = realm.where(TODOAlarm.class).max("id");
            return currentMaxId == null ? 100 : currentMaxId.intValue() + 1;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getScheduled() {
        return scheduled;
    }

    public void setScheduled(long scheduled) {
        this.scheduled = scheduled;
    }

    public boolean isRang() {
        return rang;
    }

    public void setRang(boolean rang) {
        this.rang = rang;
    }
}
